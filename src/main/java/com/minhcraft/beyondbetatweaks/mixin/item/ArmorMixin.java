package com.minhcraft.beyondbetatweaks.mixin.item;

import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ArmorMixin {

    // Disable enchantment glint on enchanted armor
    // Code copied from https://github.com/jmb05/LessGlintyThings
    @Inject(method = "isFoil", at = @At("HEAD"), cancellable = true)
    private void beyond_beta_tweaks$disableEnchantedArmorGlint(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof Equipable) {
            cir.setReturnValue(false);
        }
    }
}
