package com.minhcraft.beyondbetatweaks.mixin.feature.innate_enchantments;

import com.minhcraft.beyondbetatweaks.config.InnateEnchantmentConfig;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.item.CreativeModeTab$ItemDisplayBuilder")
public abstract class CreativeModeTabMixin {

    @Inject(method = "accept", at = @At("HEAD"))
    private void beyond_beta_tweaks$addInnateEnchantmentsToCreativeItems(ItemStack stack, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (InnateEnchantmentConfig.hasEnchantments(stack.getItem())) {
            for (InnateEnchantmentConfig.EnchantmentEntry entry : InnateEnchantmentConfig.getEnchantments(stack.getItem())) {
                int currentLevel = EnchantmentHelper.getItemEnchantmentLevel(entry.enchantment, stack);
                if (currentLevel == 0) {
                    stack.enchant(entry.enchantment, entry.level);
                }
            }
        }
    }
}