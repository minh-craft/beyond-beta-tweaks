package com.minhcraft.beyondbetatweaks.mixin.client;

import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RecipeButton.class)
public class RecipeButtonMixin {

    @Shadow
    private RecipeCollection collection;

    @Shadow
    private RecipeBook book;

    @Shadow
    private RecipeBookMenu<?> menu;

    @Inject(method = "getOrderedRecipes", at = @At("HEAD"), cancellable = true)
    private void onlyShowCraftableWhenCycling(CallbackInfoReturnable<List<Recipe<?>>> cir) {
        if (this.collection.hasCraftable()) {
            cir.setReturnValue(this.collection.getDisplayRecipes(true));
        }
    }

    @Inject(method = "isOnlyOption", at = @At("HEAD"), cancellable = true)
    private void checkAllRecipesForOverlay(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.collection.getRecipes(this.book.isFiltering(this.menu)).size() <= 1);
    }
}
