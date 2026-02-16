package com.minhcraft.beyondbetatweaks.mixin.feature.map_atlas_recipe_book;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Inventory.class)
public class InventoryMixin {

    // Replace check for same tags so that items with nbt can be matched
    // Allows filled maps with nbt data in map atlas recipe to be matched
    @WrapOperation(method = "findSlotMatchingUnusedItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameTags(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean beyond_beta_tweaks$findSlotIgnoringNbt(ItemStack stack, ItemStack other, Operation<Boolean> original) {
        return ItemStack.isSameItem(stack, other);
    }
}