package com.minhcraft.beyondbetatweaks.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RecipeBookComponent.class)
public abstract class RecipeBookComponentMixin {

    @Inject(
            method = "updateCollections",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookPage;updateCollections(Ljava/util/List;Z)V")
    )
    // Makes craftable recipes show up first in the recipe book
    // Final sorting: craftable, recipe book category, recipe custom sorting, recipe id
    private void updateCollection(boolean resetPageNumber, CallbackInfo ci, @Local(ordinal = 1) List<RecipeCollection> list2) {
        list2.sort((collection1, collection2) -> {
            boolean craftable1 = collection1.hasCraftable();
            boolean craftable2 = collection2.hasCraftable();
            if (craftable1 != craftable2) {
                return craftable1 ? -1 : 1;
            }
            return 0;
        });
    }
}
