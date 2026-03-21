package com.minhcraft.beyondbetatweaks.util;

import com.minhcraft.beyondbetatweaks.config.BiomeAlphaTintingConfigLoader;
import com.minhcraft.beyondbetatweaks.interfaces.AlphaTintingFactorAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;


public final class BlockAlphaTinting {

    public static boolean isBlockAlphaTinted(BlockState state) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return BiomeAlphaTintingConfigLoader.isBlockAlphaTinted(blockId);
    }

    public static float getBlockAlphaTintingFactor(BlockState state) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return BiomeAlphaTintingConfigLoader.getAlphaTintingFactorForBlock(blockId);
    }


    // Get the blended biome alpha tinting factor using the cached blur from WorldSlice.
    // For smooth biome transitions
    public static float getBlendedAlphaTintingFactor(BlockAndTintGetter world, BlockPos pos) {
        if (world instanceof AlphaTintingFactorAccess access) {
            AlphaTintingFactorCache cache = access.beyond_beta_tweaks$getOverlayFactorCache();
            if (cache != null) {
                return cache.getFactor(pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return 0.0f;
    }

    // Alpha tinting factor is encoded into vertex alpha, so shaders can handle the alpha style biome tinting
    // See sodium/shaders/blocks/block_layer.opaque.fsh and sodium/shaders/blocks/block_layer_opaque.vsh
    public static int encodeAlphaTintingFactorIntoVertexAlpha(int packedColor, float factor) {
        if (factor <= 0.0f) {
            return packedColor;
        }

        int alpha = (packedColor >> 24) & 0xFF;
        float scale = 1.0f - (factor * 0.5f);
        int newAlpha = Math.round(alpha * scale);
        newAlpha = Math.max(0, Math.min(255, newAlpha));

        return (packedColor & 0x00FFFFFF) | (newAlpha << 24);
    }

    private BlockAlphaTinting() {}
}