package com.minhcraft.beyondbetatweaks.mixin.item;

import net.minecraft.world.item.crafting.RepairItemRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(RepairItemRecipe.class)
public abstract class RepairItemRecipeMixin {

    // Allow enchantments to be retained when repairing in crafting grid
    @ModifyArg(
            method = "assemble(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;"), index = 0)
    private <T> Predicate<? super T> test(Predicate<? super T> predicate) {
        return (enchantment) -> true;
    }
}
