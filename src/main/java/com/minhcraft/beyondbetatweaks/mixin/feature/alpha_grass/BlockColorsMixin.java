package com.minhcraft.beyondbetatweaks.mixin.feature.alpha_grass;

import com.minhcraft.beyondbetatweaks.config.AlphaGrassConfig;
import com.minhcraft.beyondbetatweaks.util.AlphaGrassHelper;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin into BlockColors.getColor (vanilla path).
 *
 * This DOES NOT modify the tint color anymore. Instead, it passes through
 * unchanged — the shader handles the blending. The alpha-grass signal is
 * encoded separately via the vertex alpha channel (see the ModelBlockRenderer
 * and Embeddium mixins).
 *
 * This mixin is kept as a placeholder for when you add the proper
 * AlphaGrassHelper checks (block eligibility, biome factor, etc).
 * For now, the actual color modification happens in the shader.
 */
@Mixin(BlockColors.class)
public abstract class BlockColorsMixin {

    // This class now serves as a utility holder for the shared alpha factor
    // lookup. The actual color is NOT modified here — the shader handles it.


    @Inject(
            method = "getColor(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;I)I",
            at = @At("RETURN")
    )
    private void alphaGrass$storeAlphaFactor(BlockState state, BlockAndTintGetter level,
                                             BlockPos pos, int tintIndex,
                                             CallbackInfoReturnable<Integer> cir) {
        if (level == null || pos == null) {
            AlphaGrassHelper.CURRENT_ALPHA_FACTOR.set(-1.0f);
            return;
        }

        // TODO: replace with proper checks:
        // if (!ModConfig.enableAlphaGrassColoring || !AlphaGrassHelper.shouldApplyAlphaStyle(state)) {
        //     CURRENT_ALPHA_FACTOR.set(-1.0f);
        //     return;
        // }
        // float factor = AlphaGrassHelper.getAveragedAlphaFactor(level, pos);

        float factor = 1.0f; // Testing: full alpha-grass for everything
        AlphaGrassHelper.CURRENT_ALPHA_FACTOR.set(factor);
    }
}
