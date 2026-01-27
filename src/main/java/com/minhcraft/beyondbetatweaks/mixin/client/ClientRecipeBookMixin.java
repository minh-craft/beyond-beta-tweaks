package com.minhcraft.beyondbetatweaks.mixin.client;

import com.minhcraft.beyondbetatweaks.config.RecipeCustomSortingConfigLoader;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Mixin(ClientRecipeBook.class)
public abstract class ClientRecipeBookMixin {

    @Unique
    Map<String, Integer> RECIPE_CUSTOM_SORTING = RecipeCustomSortingConfigLoader.getRecipeCustomSortingConfig().getRecipeCustomSorting();

    @Redirect(
            method = "setupCollections",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V", ordinal = 0)
    )
    // Initial version of sort fix copied from Debugify mod https://github.com/isXander/Debugify/commit/fb207eacfc7a82cb23715a34a0ab78026642eb45#diff-c405d19a3af549943ad29679da9386026fea6fede7a3321a1ea11141c9aaf98a
    private void beyond_beta_tweaks$sortRecipes(Map<RecipeBookCategories, List<List<Recipe<?>>>> map, BiConsumer<RecipeBookCategories, List<List<Recipe<?>>>> action) {
        map.entrySet().stream().map(entry -> Map.entry(entry.getKey(), entry.getValue()
                .stream()
                .sorted((o1, o2) -> {
                    if (o1.isEmpty() || o2.isEmpty()) {
                        return 0;
                    }

                    // Primary sort: custom sort defined in config/recipeCustomSorting json
                    // by default, recipe ids have a sort level of 0 if not explicitly defined
                    // can define custom sort levels for recipe ids
                    // (ex: make food recipe ids have a sort level of -100 so they appear earlier in the recipe book)
                    int level1 = o1.stream()
                            .map(Recipe::getId)
                            .mapToInt(itemId -> RECIPE_CUSTOM_SORTING.getOrDefault(itemId.toString(), 0))
                            .min()
                            .orElse(0);
                    int level2 = o2.stream()
                            .map(Recipe::getId)
                            .mapToInt(itemId -> RECIPE_CUSTOM_SORTING.getOrDefault(itemId.toString(), 0))
                            .min()
                            .orElse(0);
                    if (level1 != level2) {
                        return Integer.compare(level1, level2);
                    }

                    // Secondary sort: by recipe id (ex: minecraft:blue_dye_from_cornflower)
                    return o1.get(0).getId().compareTo(o2.get(0).getId());
                })
                .collect(Collectors.toList())
        )).forEach(entry -> action.accept(entry.getKey(), entry.getValue()));
    }

}
