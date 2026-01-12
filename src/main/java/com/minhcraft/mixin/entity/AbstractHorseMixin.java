package com.minhcraft.mixin.entity;

import com.minhcraft.config.ModConfig;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
    private static void crt$overrideHorseSpeed(DoubleSupplier supplier, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(ModConfig.horseMovementSpeed);
    }

    // Jump strength of 0.6 allows clearing two blocks
    @Inject(
            method = "generateJumpStrength",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void crt$overrideHorseJumpStrength(DoubleSupplier supplier, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(0.6);
    }

    // Horses always have 10 hearts
    @Inject(
            method = "generateMaxHealth",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void crt$overrideHorseMaxHealth(IntUnaryOperator operator, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(20.0F);
    }
}
