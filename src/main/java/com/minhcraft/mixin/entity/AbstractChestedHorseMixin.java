package com.minhcraft.mixin.entity;

import com.minhcraft.config.ModConfig;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.entity.animal.horse.AbstractHorse.createBaseHorseAttributes;

@Mixin(AbstractChestedHorse.class)
public abstract class AbstractChestedHorseMixin {

    @Inject(
            method = "createBaseChestedHorseAttributes",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void test(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        cir.setReturnValue(
                createBaseHorseAttributes()
                        .add(Attributes.MOVEMENT_SPEED, ModConfig.donkeyMovementSpeed)
                        .add(Attributes.JUMP_STRENGTH, 0.5)
                        .add(Attributes.MAX_HEALTH, 20.0));
    }

    @Inject(
            method = "randomizeAttributes",
            at = @At("HEAD"),
            cancellable = true
    )
    private void crt$cancelRandomizeHealth(RandomSource random, CallbackInfo ci) {
        ci.cancel();
    }
}
