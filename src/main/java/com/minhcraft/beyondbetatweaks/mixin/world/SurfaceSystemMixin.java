package com.minhcraft.beyondbetatweaks.mixin.world;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SurfaceSystem.class)
public abstract class SurfaceSystemMixin {

    // Disable giant icebergs from generating in frozen oceans (still generate in deep frozen oceans)
    @ModifyExpressionValue(
            method = "buildSurface",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/resources/ResourceKey;)Z", ordinal = 1)
    )
    private boolean beyond_beta_tweaks$disableFrozenOceanGiantIceberg(boolean original) {
        return false;
    }

    // Modify iceberg scaling variable which affects iceberg height
    // Larger values = larger height
    @ModifyConstant(
            method = "frozenOceanExtension",
            constant = @Constant(doubleValue = 1.2)
    )
    private double beyond_beta_tweaks$modifyIcebergScalingVariableOne(double constant) {
        return 1.4;
    }

    // Modify iceberg scaling variable which affects iceberg height
    // Larger values = larger height
    @ModifyConstant(
            method = "frozenOceanExtension",
            constant = @Constant(doubleValue = 14.0)
    )
    private double beyond_beta_tweaks$modifyIcebergScalingVariableTwo(double constant) {
        return 16.0;
    }
}
