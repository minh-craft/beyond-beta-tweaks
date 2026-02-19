package com.minhcraft.beyondbetatweaks.util;

import com.minhcraft.beyondbetatweaks.interfaces.IFractionalProtectionEnchantment;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.mutable.MutableFloat;

public class ProtectionEnchantmentHelper {

    // version of EnchantmentHelper.getDamageProtection that allows fractional values for damage protection
    public static float getFloatDamageProtection(Iterable<ItemStack> stacks, DamageSource source) {
        MutableFloat mutableFloat = new MutableFloat();
        EnchantmentHelper.runIterationOnInventory((enchantment, i) -> mutableFloat.add(((IFractionalProtectionEnchantment)enchantment).beyond_beta_tweaks$getFloatDamageProtection(i, source)), stacks);
        return mutableFloat.floatValue();
    }
}
