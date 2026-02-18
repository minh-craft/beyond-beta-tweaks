package com.minhcraft.beyondbetatweaks.mixin.feature.enchantment.tooltip;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public abstract class ItemMixin {

    @WrapOperation(
            method = "getRarity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEnchanted()Z")
    )
    private boolean beyond_beta_tweaks$disableEnchantedTooltipColoring(ItemStack instance, Operation<Boolean> original) {
        return false;
    }
}
