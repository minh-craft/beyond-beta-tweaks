package com.minhcraft.beyondbetatweaks.mixin.feature.enchantment.glint;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {

    // Disable enchantment glint on enchanted armor and tools
    // Code copied from https://github.com/jmb05/LessGlintyThings
    @Inject(method = "isFoil", at = @At("HEAD"), cancellable = true)
    private void beyond_beta_tweaks$disableEnchantmentGlint(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
