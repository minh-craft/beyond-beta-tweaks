package com.minhcraft.beyondbetatweaks.util;

import com.minhcraft.beyondbetatweaks.config.AlphaGrassConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Unique;

/**
 * Helper for determining alpha-grass eligibility and computing biome-averaged
 * alpha factors with smooth transitions at biome boundaries.
 */
public class AlphaGrassHelper {

    /**
     * Thread-local storage for the alpha factor computed during getColor.
     * This is read by the ModelBlockRenderer mixin when writing vertex alpha.
     */
    @Unique
    public static final ThreadLocal<Float> CURRENT_ALPHA_FACTOR = ThreadLocal.withInitial(() -> -1.0f);

    /**
     * Thread-local alpha factor for Embeddium's rendering path.
     */
    public static final ThreadLocal<Float> EMBEDDIUM_ALPHA_FACTOR = ThreadLocal.withInitial(() -> -1.0f);

    /**
     * Determines whether alpha-style tinting should be applied to the given block.
     */
    public static boolean shouldApplyAlphaStyle(BlockState state) {
        Block block = state.getBlock();

        // Grass block — always eligible
        if (block == Blocks.GRASS_BLOCK) {
            return true;
        }

        // Plants — only if config enables it
        if (AlphaGrassConfig.shouldAffectPlants() && isGrassPlant(block)) {
            return true;
        }

        // Leaves — only if config enables it
        if (AlphaGrassConfig.shouldAffectLeaves() && block instanceof LeavesBlock) {
            return true;
        }

        return false;
    }

    private static boolean isGrassPlant(Block block) {
        // In 1.20.1 Mojang mappings:
        // Short grass = Blocks.GRASS (the plant, not GRASS_BLOCK)
        // Tall grass = Blocks.TALL_GRASS
        // Fern = Blocks.FERN
        // Large fern = Blocks.LARGE_FERN
        return block == Blocks.GRASS
                || block == Blocks.TALL_GRASS
                || block == Blocks.FERN
                || block == Blocks.LARGE_FERN;
    }

    /**
     * Computes the average alpha factor for a position, sampling a 3x3 grid
     * of biomes around the block. This matches vanilla's biome color averaging
     * pattern and provides smooth transitions at biome boundaries.
     *
     * If a biome with alphaFactor=1.0 borders a vanilla biome (alphaFactor=0.0),
     * the transition blocks will get intermediate values like 0.33, 0.67, etc.,
     * resulting in a smooth visual blend.
     *
     * @param level The block/tint getter (can be Level, RenderChunkRegion, etc.)
     * @param pos   The block position
     * @return A value from 0.0 (vanilla) to 1.0 (full alpha-style)
     */
    public static float getAveragedAlphaFactor(BlockAndTintGetter level, BlockPos pos) {
        float total = 0.0f;
        int count = 0;

        // Sample 3x3 grid matching vanilla's biome color averaging
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos samplePos = pos.offset(dx, 0, dz);
                float factor = getAlphaFactorAtPos(level, samplePos);
                total += factor;
                count++;
            }
        }

        return count > 0 ? total / count : 0.0f;
    }

    /**
     * Gets the alpha factor for a single block position by looking up its biome.
     */
    private static float getAlphaFactorAtPos(BlockAndTintGetter level, BlockPos pos) {
        // BlockAndTintGetter extends LevelReader which has getBiome()
        // Both Level and RenderChunkRegion implement this
        try {
            if (level instanceof net.minecraft.world.level.LevelReader reader) {
                Holder<Biome> biomeHolder = reader.getBiome(pos);
                ResourceLocation biomeId = biomeHolder.unwrapKey()
                        .map(key -> key.location())
                        .orElse(null);

                if (biomeId != null) {
                    return AlphaGrassConfig.getAlphaFactor(biomeId);
                }
            }
        } catch (Exception e) {
            // Silently fall back to vanilla
        }

        return 0.0f;
    }
}
