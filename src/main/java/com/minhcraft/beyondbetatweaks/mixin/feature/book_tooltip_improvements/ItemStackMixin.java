package com.minhcraft.beyondbetatweaks.mixin.feature.book_tooltip_improvements;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    private void beyond_beta_tweaks$addUndyedTooltip(@Nullable Player player, TooltipFlag isAdvanced, CallbackInfoReturnable<List<Component>> cir) {
        ItemStack self = (ItemStack)(Object) this;
        if (!(self.getItem() instanceof DyeableLeatherItem dyeable)) return;
        if (dyeable.hasCustomColor(self)) return;

        int hideFlags = self.hasTag() ? self.getTag().getInt("HideFlags") : 0;
        if ((hideFlags & ItemStack.TooltipPart.DYE.getMask()) != 0) return;

        // Insert just before the attribute modifiers blank line (or at end)
        List<Component> list = cir.getReturnValue();
        int insertIndex = list.size();
        for (int j = 1; j < list.size(); j++) {
            if (list.get(j) == CommonComponents.EMPTY) {
                insertIndex = j;
                break;
            }
        }

        list.add(insertIndex, Component.translatable("beyond-beta-tweaks.item.undyed")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
}
