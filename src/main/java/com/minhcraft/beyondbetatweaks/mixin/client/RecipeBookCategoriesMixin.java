package com.minhcraft.beyondbetatweaks.mixin.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeBookCategories.class)
public class RecipeBookCategoriesMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void modifyItemIcons(CallbackInfo ci) {
        ((RecipeBookCategoriesAccessor) (Object) RecipeBookCategories.CRAFTING_MISC)
                .setItemIcons(ImmutableList.of(new ItemStack(Blocks.BREWING_STAND), new ItemStack(Items.APPLE)));

        ((RecipeBookCategoriesAccessor) (Object) RecipeBookCategories.FURNACE_MISC)
                .setItemIcons(ImmutableList.of(new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.IRON_INGOT)));
    }
}
