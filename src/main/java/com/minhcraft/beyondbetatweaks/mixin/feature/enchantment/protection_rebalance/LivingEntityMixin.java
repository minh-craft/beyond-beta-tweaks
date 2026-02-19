package com.minhcraft.beyondbetatweaks.mixin.feature.enchantment.protection_rebalance;

import com.minhcraft.beyondbetatweaks.util.ProtectionEnchantmentHelper;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract Iterable<ItemStack> getArmorSlots();

    @Inject(
            method = "getDamageAfterMagicAbsorb",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getDamageProtection(Ljava/lang/Iterable;Lnet/minecraft/world/damagesource/DamageSource;)I"),
            cancellable = true
    )
    private void beyond_beta_tweaks$useFloatDamageProtection(DamageSource damageSource, float damageAmount, CallbackInfoReturnable<Float> cir) {
        float i = ProtectionEnchantmentHelper.getFloatDamageProtection(this.getArmorSlots(), damageSource);
        if (i > 0) {
            damageAmount = CombatRules.getDamageAfterMagicAbsorb(damageAmount, i);
        }

        cir.setReturnValue(damageAmount);
    }
}
