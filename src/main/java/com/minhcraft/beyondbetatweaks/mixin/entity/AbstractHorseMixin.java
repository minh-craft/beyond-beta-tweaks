package com.minhcraft.beyondbetatweaks.mixin.entity;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Mule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.DoubleSupplier;
import java.util.function.IntUnaryOperator;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin {

    @Inject(
            method = "generateSpeed",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void beyond_beta_tweaks$overrideHorseSpeed(DoubleSupplier supplier, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(ModConfig.horseMovementSpeed);
    }

    // Jump strength of 0.6 allows clearing two blocks
    @Inject(
            method = "generateJumpStrength",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void beyond_beta_tweaks$overrideHorseJumpStrength(DoubleSupplier supplier, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(0.6);
    }

    // Horses always have 10 hearts
    @Inject(
            method = "generateMaxHealth",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void beyond_beta_tweaks$overrideHorseMaxHealth(IntUnaryOperator operator, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(20.0F);
    }

    @Inject(
            method = "setOffspringAttributes",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beyond_beta_tweaks$hardcodeBredHorseStats(AgeableMob parent, AbstractHorse child, CallbackInfo ci) {
        if ( child instanceof Donkey) {
            child.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0);
            child.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(0.5);
            child.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(ModConfig.donkeyMovementSpeed);
            ci.cancel();
        } else if (child instanceof Horse) {
            child.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0);
            child.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(0.6);
            child.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(ModConfig.horseMovementSpeed);
            ci.cancel();
        } else if ( child instanceof Mule) {
            child.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0);
            child.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(0.6);
            child.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(ModConfig.donkeyMovementSpeed);
            ci.cancel();
        }
    }
}
