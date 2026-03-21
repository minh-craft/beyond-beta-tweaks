package com.minhcraft.beyondbetatweaks.mixin.feature.cave_biomes_no_lava;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ProtoChunk.class)
public abstract class ProtoChunkMixin {

    @ModifyVariable(
            method = "setBlockState",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private BlockState beyond_beta_tweaks$replaceCaveBiomeLavaWithWater(BlockState state, BlockPos pos) {
        if (state.is(Blocks.LAVA)) {
            try {
                ProtoChunk self = (ProtoChunk) (Object) this;
                Holder<Biome> biome = self.getNoiseBiome(
                        QuartPos.fromBlock(pos.getX()),
                        QuartPos.fromBlock(pos.getY()),
                        QuartPos.fromBlock(pos.getZ())
                );

                boolean isLushCaves = biome.is(Biomes.LUSH_CAVES);
                boolean isDripstoneCaves = biome.is(Biomes.DRIPSTONE_CAVES)
                        && pos.getY() <= ModConfig.overworldLavaLevel; // only replace deep lava lakes with water in dripstone caves, instead of replacing all lava

                if (isLushCaves || isDripstoneCaves) {
                    // Preserve fluid level if it's flowing lava
                    if (state.getFluidState().isSource()) {
                        return Blocks.WATER.defaultBlockState();
                    } else {
                        int level = state.getFluidState().getAmount();
                        return net.minecraft.world.level.material.Fluids.WATER
                                .getFlowing(level, false)
                                .createLegacyBlock();
                    }
                }
            } catch (Exception e) {
                // If biome data isn't available yet, skip replacement.
                // This can happen during very early generation stages.
            }
        }
        return state;
    }
}
