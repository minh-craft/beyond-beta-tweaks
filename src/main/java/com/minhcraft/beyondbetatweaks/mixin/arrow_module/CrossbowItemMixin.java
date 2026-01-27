package com.minhcraft.beyondbetatweaks.mixin.arrow_module;

import com.llamalad7.mixinextras.sugar.Local;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {

    // Override shooting power - default is 3.15
    @Inject(
            method = "getShootingPower",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void beyond_beta_tweaks$overrideShootingPower(ItemStack crossbowStack, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(ModConfig.crossbowShootingPower);
        cir.cancel();
    }

    // Override charge duration - default is 25
    @Inject(
            method = "getChargeDuration",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void beyond_beta_tweaks$overrideChargeDuration(ItemStack crossbowStack, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ModConfig.crossbowChargeDuration);
        cir.cancel();
    }

    // Override arrow base damage
    @Inject(
            method = "getArrow",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setSoundEvent(Lnet/minecraft/sounds/SoundEvent;)V")
    )
    private static void beyond_beta_tweaks$overrideArrowBaseDamage(Level level, LivingEntity livingEntity, ItemStack crossbowStack, ItemStack ammoStack, CallbackInfoReturnable<AbstractArrow> cir, @Local AbstractArrow abstractArrow) {
        abstractArrow.setBaseDamage(ModConfig.crossbowArrowBaseDamage);
    }
}
