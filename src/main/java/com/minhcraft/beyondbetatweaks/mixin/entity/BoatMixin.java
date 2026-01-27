package com.minhcraft.beyondbetatweaks.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Boat.class)
public abstract class BoatMixin {

    // Clamp boat slipperiness to nerf ice boats
    // 0.90 slipperiness makes ice boat speed about the same as a boat on water
    // Code adapted from implementation in https://modrinth.com/mod/ice-boat-nerf by @supersaiyansubtlety
    @ModifyReturnValue(method = "getGroundFriction", at = @At("RETURN"))
    private float beyond_beta_tweaks$clampSlipperiness(float original) {
        return (float) Mth.clamp(original, 0, ModConfig.boatMaxGroundSlipperiness);
    }

    // Add option to modify boat water friction
    @ModifyConstant(method = "floatBoat", constant = @Constant(floatValue = 0.9F, ordinal = 0))
    private float beyond_beta_tweaks$modifyBoatWaterFriction(float constant) {
        return ModConfig.boatWaterSlipperiness;
    }
}
