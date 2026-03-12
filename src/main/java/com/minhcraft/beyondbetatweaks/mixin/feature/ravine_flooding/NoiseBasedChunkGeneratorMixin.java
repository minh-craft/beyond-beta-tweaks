package com.minhcraft.beyondbetatweaks.mixin.feature.ravine_flooding;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.ScheduledTick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorMixin {

    @Unique
    private static final int[][] CARDINAL_NEIGHBORS = {
            { 1, 0, 0}, {-1, 0, 0},
            { 0, 1, 0}, { 0,-1, 0},
            { 0, 0, 1}, { 0, 0,-1}
    };

    @Unique
    private static void fixCanyonFluidFlow(ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int chunkMinX = chunkPos.getMinBlockX();
        int chunkMinZ = chunkPos.getMinBlockZ();
        int seaLevel = 63;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos neighbor = new BlockPos.MutableBlockPos();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = chunk.getMinBuildHeight(); y <= seaLevel; y++) {
                    pos.set(chunkMinX + x, y, chunkMinZ + z);
                    BlockState state = chunk.getBlockState(pos);

                    boolean isWater = state.is(Blocks.WATER);
                    boolean isLava = state.is(Blocks.LAVA);
                    if (!isWater && !isLava) continue;

                    for (int[] offset : CARDINAL_NEIGHBORS) {
                        neighbor.set(pos.getX() + offset[0], pos.getY() + offset[1], pos.getZ() + offset[2]);

                        if (neighbor.getX() < chunkMinX || neighbor.getX() > chunkMinX + 15
                                || neighbor.getZ() < chunkMinZ || neighbor.getZ() > chunkMinZ + 15) {
                            continue;
                        }

                        BlockState neighborState = chunk.getBlockState(neighbor);
                        if (neighborState.isAir() || neighborState.is(Blocks.CAVE_AIR)) {
                            chunk.getFluidTicks().schedule(new ScheduledTick<>(
                                    isWater ? Fluids.WATER : Fluids.LAVA,
                                    pos.immutable(), 1, 0
                            ));
                            break;
                        }
                    }
                }
            }
        }
    }

    @Inject(
            method = "applyCarvers",
            at = @At("TAIL")
    )
    private void beyond_beta_tweaks$afterCarving(
            WorldGenRegion level,
            long seed,
            RandomState randomState,
            BiomeManager biomeManager,
            StructureManager structureManager,
            ChunkAccess chunk,
            GenerationStep.Carving step,
            CallbackInfo ci
    ) {
        fixCanyonFluidFlow(chunk);
    }
}
