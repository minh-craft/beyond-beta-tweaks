package com.minhcraft.beyondbetatweaks.config;

import org.spongepowered.include.com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class RecipeCustomSortingConfig {
    @SerializedName("recipeCustomSorting")
    private final Map<String, Integer> recipeCustomSorting = new HashMap<>();
//
//    @SerializedName("giantMaxHealth")
//    private final double giantMaxHealth;

    // Default values
    public RecipeCustomSortingConfig() {
        recipeCustomSorting.put("minecraft:bundle", -1000);
        recipeCustomSorting.put("minecraft:clock", -1000);
        recipeCustomSorting.put("minecraft:compass", -1000);
        recipeCustomSorting.put("simple_quiver:quiver", -1000);

        recipeCustomSorting.put("minecraft:bucket", -100);
        recipeCustomSorting.put("minecraft:flint_and_steel", -100);
        recipeCustomSorting.put("minecraft:fishing_rod", -100);
        recipeCustomSorting.put("minecraft:lead", -100);
        recipeCustomSorting.put("minecraft:shears", -100);
        recipeCustomSorting.put("minecraft:spyglass", -100);

        recipeCustomSorting.put("minecraft:arrow", -10);
        recipeCustomSorting.put("minecraft:bow", -10);
        recipeCustomSorting.put("minecraft:crossbow", -10);
//
//        giantMaxHealth = 45;
    }

    public Map<String, Integer> getRecipeCustomSorting() {
        return recipeCustomSorting;
    }

//    public double getGiantMaxHealth() {
//        return giantMaxHealth;
//    }
}
