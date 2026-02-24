package com.minhcraft.beyondbetatweaks.mixin.feature.book_tooltip_improvements;

import net.anvian.visualizerbookshelf.util.PlayerLookHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = PlayerLookHelper.class, remap = false)
public abstract class PlayerLookHelperMixin {

    @Inject(method = "getBookText", at = @At("TAIL"), cancellable = true)
    private static void beyond_beta_tweaks$addWrittenBookAuthor(ItemStack book, CallbackInfoReturnable<List<Component>> cir) {
        if (book.getItem() == Items.WRITTEN_BOOK) {
            CompoundTag tag = book.getTag();
            if (tag != null && !tag.getString("author").isEmpty()) {
                List<Component> list = new ArrayList<>(cir.getReturnValue());
                list.add(Component.translatable("book.byAuthor", tag.getString("author"))
                        .copy().withStyle(s -> s.withItalic(true).withColor(0xCECECE)));
                cir.setReturnValue(list);
            }
        }
    }
}