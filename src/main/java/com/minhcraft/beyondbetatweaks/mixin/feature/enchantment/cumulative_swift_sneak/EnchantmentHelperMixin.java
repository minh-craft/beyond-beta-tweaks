package com.minhcraft.beyondbetatweaks.mixin.feature.enchantment.cumulative_swift_sneak;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Unique
    private static int getEnchantmentLevelCumulative(Enchantment enchantment, LivingEntity entity) {
        Iterable<ItemStack> iterable = enchantment.getSlotItems(entity).values();
        int cumulativeEnchantLevel = 0;

        for (ItemStack itemStack : iterable) {
            cumulativeEnchantLevel += getItemEnchantmentLevel(enchantment, itemStack);
        }

        return cumulativeEnchantLevel;
    }

    @Inject(
            method = "getSneakingSpeedBonus",
            at = @At("HEAD"),
            cancellable = true
    )
    // Allow swift sneak bonus to be cumulative from different equipped armor pieces
    private static void beyond_beta_tweaks$getSneakingSpeedBonus(LivingEntity entity, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(getEnchantmentLevelCumulative(Enchantments.SWIFT_SNEAK, entity) * 0.15F);
        cir.cancel();
    }
}
