package com.minhcraft.beyondbetatweaks.mixin.feature.end_dimension_space_sky;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DimensionSpecialEffects.EndEffects.class)
public class EndEffectsMixin {

    // Setting a cloud height allows clouds to render in the End
    // Actual cloud height is meant to be configured with Cloud Layers mod
    @ModifyArg(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;<init>(FZLnet/minecraft/client/renderer/DimensionSpecialEffects$SkyType;ZZ)V"),
            index = 0
    )
    private static float beyond_beta_tweaks$setEndCloudHeight(float original) {
        return 0.0F;
    }
}
