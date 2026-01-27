package com.minhcraft.beyondbetatweaks.mixin.client;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(RecipeBookCategories.class)
public interface RecipeBookCategoriesAccessor {

    @Accessor("itemIcons")
    @Mutable
    void setItemIcons(List<ItemStack> itemIcons);
}
