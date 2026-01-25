package com.minhcraft.mixin.compat_better_recipe_book;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.api.BRBBookCategories;
import marsh.town.brb.util.BRBHelper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BetterRecipeBook.class)
public abstract class BetterRecipeBookMixin {

    @WrapOperation(
            method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lmarsh/town/brb/util/BRBHelper$Book;createCategory([Lnet/minecraft/world/item/ItemStack;)Lmarsh/town/brb/api/BRBBookCategories$Category;", ordinal = 2)
    )
    private static BRBBookCategories.Category crt$disableLingeringPotionRecipeBookCategory(BRBHelper.Book instance, ItemStack[] entries, Operation<BRBBookCategories.Category> original) {

        return null;
    }

}
