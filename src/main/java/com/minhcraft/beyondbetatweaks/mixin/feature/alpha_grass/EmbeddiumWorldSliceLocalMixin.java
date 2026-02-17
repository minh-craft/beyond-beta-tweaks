package com.minhcraft.beyondbetatweaks.mixin.feature.alpha_grass;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "org.embeddedt.embeddium.render.world.WorldSliceLocal", remap = false)
public abstract class EmbeddiumWorldSliceLocalMixin {

    @Inject(
            method = "getBlockTint",
            at = @At("RETURN"),
            cancellable = true,
            remap = true
    )
    private void alphaGrass$captureAlphaFactor(BlockPos pos, ColorResolver resolver,
                                               CallbackInfoReturnable<Integer> cir) {
        // Same logic as EmbeddiumWorldSliceMixin
        float factor = 1.0f;

        int color = cir.getReturnValue();
        int encodedAlpha = (int) ((1.0f - factor) * 0.95f * 255.0f);
        encodedAlpha = Math.max(0, Math.min(242, encodedAlpha));
        int modifiedColor = (encodedAlpha << 24) | (color & 0x00FFFFFF);
        cir.setReturnValue(modifiedColor);
    }
}
