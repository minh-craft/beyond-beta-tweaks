package com.minhcraft.mixin.better_recipe_book_compat;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.brewingstand.BrewableResult;
import marsh.town.brb.brewingstand.BrewingRecipeBookComponent;
import marsh.town.brb.brewingstand.BrewingRecipeCollection;
import marsh.town.brb.generic.GenericRecipeBookComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static marsh.town.brb.brewingstand.fabric.PlatformPotionUtilImpl.getFrom;
import static marsh.town.brb.brewingstand.fabric.PlatformPotionUtilImpl.getIngredient;

@Mixin(BrewingRecipeBookComponent.class)
public abstract class BrewingRecipeBookComponentMixin extends GenericRecipeBookComponent<BrewingStandMenu, BrewingRecipeCollection, BrewableResult> {

    @Inject(
            method = "getInputStack",
            at = @At("HEAD"),
            cancellable = true
    )
    private void crt$disableLingeringPotionRecipeBook(BrewableResult result, CallbackInfoReturnable<ItemStack> cir) {
        Potion inputPotion = getFrom(result.recipe);
        Ingredient ingredient = getIngredient(result.recipe);
        ResourceLocation identifier = BuiltInRegistries.POTION.getKey(inputPotion);
        ItemStack inputStack;
        if (this.selectedTab.getCategory() == BetterRecipeBook.BREWING_SPLASH_POTION) {
            inputStack = new ItemStack(Items.SPLASH_POTION);
//        } else if (this.selectedTab.getCategory() == BetterRecipeBook.BREWING_LINGERING_POTION) {
//            inputStack = new ItemStack(Items.LINGERING_POTION);
        } else {
            inputStack = new ItemStack(Items.POTION);
        }

        inputStack.getOrCreateTag().putString("Potion", identifier.toString());
        cir.setReturnValue(inputStack);
    }
}
