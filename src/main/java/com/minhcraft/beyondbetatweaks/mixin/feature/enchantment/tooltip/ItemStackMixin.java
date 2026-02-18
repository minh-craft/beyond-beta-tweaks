package com.minhcraft.beyondbetatweaks.mixin.feature.enchantment.tooltip;

import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Unique
    private final ThreadLocal<List<Component>> capturedEnchantLines = ThreadLocal.withInitial(ArrayList::new);

    /**
     * Redirect the call to ItemStack.appendEnchantmentNames to capture the enchantment
     * lines instead of adding them to the tooltip directly.
     */
    @Redirect(
            method = "getTooltipLines",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;appendEnchantmentNames(Ljava/util/List;Lnet/minecraft/nbt/ListTag;)V"
            )
    )
    private void redirectEnchantmentNames(List<Component> tooltip, ListTag enchantmentTags) {
        // Instead of appending to the real tooltip, capture into our temporary list
        List<Component> temp = new ArrayList<>();
        ItemStack.appendEnchantmentNames(temp, enchantmentTags);
        capturedEnchantLines.get().clear();
        capturedEnchantLines.get().addAll(temp);
    }

    /**
     * After the tooltip is fully built, insert the captured enchantment lines
     * right after the "When on X:" equipment slot header.
     */
    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    private void insertEnchantsAfterSlotHeader(Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> cir) {
        List<Component> enchants = capturedEnchantLines.get();
        if (enchants.isEmpty()) return;

        List<Component> tooltip = cir.getReturnValue();

        // Find the last "When on X:" header (item.modifiers.slot)
        int slotHeaderIndex = -1;
        for (int i = 0; i < tooltip.size(); i++) {
            Component comp = tooltip.get(i);
            if (comp instanceof net.minecraft.network.chat.MutableComponent mc) {
                if (mc.getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents tc) {
                    if (tc.getKey().startsWith("item.modifiers.")) {
                        slotHeaderIndex = i;
                    }
                }
            }
        }

        if (slotHeaderIndex != -1) {
            // Insert enchantments right after the slot header
            tooltip.addAll(slotHeaderIndex + 1, enchants);
        } else {
            // No attribute section found — fall back to just appending them normally
            // Find index after item name (index 0) to put them back in original spot
            tooltip.addAll(1, enchants);
        }

        enchants.clear();
    }
}
