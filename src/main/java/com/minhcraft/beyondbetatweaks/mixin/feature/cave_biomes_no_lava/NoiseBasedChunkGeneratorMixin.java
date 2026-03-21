package com.minhcraft.beyondbetatweaks.mixin.feature.cave_biomes_no_lava;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.ScheduledTick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorMixin {

    @Unique
    private static void processChunkReplaceLavaWithWater(ChunkAccess chunk, BiomeManager biomeManager) {
        ChunkPos chunkPos = chunk.getPos();
        int chunkMinX = chunkPos.getMinBlockX();
        int chunkMinZ = chunkPos.getMinBlockZ();
        int chunkMaxX = chunkMinX + 15;
        int chunkMaxZ = chunkMinZ + 15;
        int worldMinY = chunk.getMinBuildHeight();
        int worldMaxY = chunk.getMaxBuildHeight() - 1;

        // ── Pass 1: Replace all lava in lush caves and dripstone caves with water ──

        Set<Long> replacedPositions = new HashSet<>();

        for (int sectionIndex = 0; sectionIndex < chunk.getSectionsCount(); sectionIndex++) {
            LevelChunkSection section = chunk.getSection(sectionIndex);
            if (section.hasOnlyAir()) continue;

            int baseY = chunk.getSectionYFromSectionIndex(sectionIndex) << 4;

            for (int lx = 0; lx < 16; lx++) {
                for (int lz = 0; lz < 16; lz++) {
                    for (int ly = 0; ly < 16; ly++) {
                        BlockState state = section.getBlockState(lx, ly, lz);
                        if (!state.is(Blocks.LAVA)) continue;

                        int wx = chunkMinX + lx;
                        int wy = baseY + ly;
                        int wz = chunkMinZ + lz;

                        Holder<Biome> biome = biomeManager.getBiome(new BlockPos(wx, wy, wz));

                        boolean isLushCaves = biome.is(Biomes.LUSH_CAVES);
                        boolean isDripstoneCaves = biome.is(Biomes.DRIPSTONE_CAVES)
                                && wy <= ModConfig.overworldLavaLevel; // only replace deep lava lakes with water in dripstone caves, instead of replacing all lava

                        if (isLushCaves || isDripstoneCaves) {
                            if (state.getFluidState().isSource()) {
                                section.setBlockState(lx, ly, lz,
                                        Blocks.WATER.defaultBlockState(), false);
                            } else {
                                int level = state.getFluidState().getAmount();
                                section.setBlockState(lx, ly, lz,
                                        Fluids.WATER.getFlowing(level, false).createLegacyBlock(),
                                        false);
                            }
                            replacedPositions.add(packPos(wx, wy, wz));
                        }
                    }
                }
            }
        }

        if (replacedPositions.isEmpty()) return;

        // ── Pass 2: Schedule fluid ticks for water blocks that neighbor lava ──
        // When the chunk loads and these ticks fire, vanilla's fluid interaction
        // code will naturally convert the lava/water contact to obsidian/cobblestone.

        for (long packed : replacedPositions) {
            int wx = unpackX(packed);
            int wy = unpackY(packed);
            int wz = unpackZ(packed);

            if (hasLavaNeighborOrIsOnEdgeOfChunk(chunk, wx, wy, wz, chunkMinX, chunkMinZ, chunkMaxX, chunkMaxZ, worldMinY, worldMaxY, replacedPositions)) {
                BlockPos pos = new BlockPos(wx, wy, wz);
                // Schedule a fluid tick for water at this position.
                // Delay of 1 tick — fires as soon as the chunk starts ticking.
                chunk.getFluidTicks().schedule(new ScheduledTick<>(
                        Fluids.WATER,
                        pos,
                        1,   // delay in ticks
                        0    // sub-tick ordering
                ));
            }
        }
    }

    @Unique
    private static boolean hasLavaNeighborOrIsOnEdgeOfChunk(ChunkAccess chunk, int wx, int wy, int wz,
                                                            int chunkMinX, int chunkMinZ,
                                                            int chunkMaxX, int chunkMaxZ,
                                                            int worldMinY, int worldMaxY,
                                                            Set<Long> replacedPositions) {
        for (int[] offset : CARDINAL_NEIGHBORS) {
            int nx = wx + offset[0];
            int ny = wy + offset[1];
            int nz = wz + offset[2];

            if (ny < worldMinY || ny > worldMaxY) continue;
            // If neighbor is outside this chunk, we can't check it —
            // schedule a tick just in case there's lava over there.
            if (nx < chunkMinX || nx > chunkMaxX || nz < chunkMinZ || nz > chunkMaxZ) return true;

            long neighborPacked = packPos(nx, ny, nz);
            if (replacedPositions.contains(neighborPacked)) continue;

            BlockState neighborState = getBlockState(chunk, nx, ny, nz);
            if (neighborState.is(Blocks.LAVA)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static final int[][] CARDINAL_NEIGHBORS = {
            { 1, 0, 0}, {-1, 0, 0},
            { 0, 1, 0}, { 0,-1, 0},
            { 0, 0, 1}, { 0, 0,-1}
    };

    @Unique
    private static BlockState getBlockState(ChunkAccess chunk, int wx, int wy, int wz) {
        int sectionIndex = chunk.getSectionIndex(wy);
        if (sectionIndex < 0 || sectionIndex >= chunk.getSectionsCount()) {
            return Blocks.AIR.defaultBlockState();
        }
        LevelChunkSection section = chunk.getSection(sectionIndex);
        int lx = wx - chunk.getPos().getMinBlockX();
        int ly = wy & 15;
        int lz = wz - chunk.getPos().getMinBlockZ();
        return section.getBlockState(lx, ly, lz);
    }

    // --- Position packing ---
    @Unique
    private static long packPos(int x, int y, int z) {
        return ((long) (x & 0x3FFFFFF))
                | ((long) (y & 0xFFF) << 26)
                | ((long) (z & 0x3FFFFFF) << 38);
    }

    @Unique
    private static int unpackX(long packed) {
        int raw = (int) (packed & 0x3FFFFFF);
        return (raw << 6) >> 6;
    }

    @Unique
    private static int unpackY(long packed) {
        int raw = (int) ((packed >> 26) & 0xFFF);
        return (raw << 20) >> 20;
    }

    @Unique
    private static int unpackZ(long packed) {
        int raw = (int) ((packed >> 38) & 0x3FFFFFF);
        return (raw << 6) >> 6;
    }


    @Inject(
            method = "buildSurface(Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/WorldGenerationContext;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/blending/Blender;)V",
            at = @At("TAIL")
    )
    private void beyond_beta_tweaks$afterBuildSurface(
            ChunkAccess chunk,
            WorldGenerationContext context,
            RandomState randomState,
            StructureManager structureManager,
            BiomeManager biomeManager,
            Registry<Biome> biomeRegistry,
            Blender blender,
            CallbackInfo ci
    ) {
        processChunkReplaceLavaWithWater(chunk, biomeManager);
    }
}
