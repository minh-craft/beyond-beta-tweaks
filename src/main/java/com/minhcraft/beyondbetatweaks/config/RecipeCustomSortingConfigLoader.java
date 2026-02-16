package com.minhcraft.beyondbetatweaks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RecipeCustomSortingConfigLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path RECIPE_CUSTOM_SORTING_CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve(BeyondBetaTweaks.MOD_ID + "/recipe_custom_sorting.json");
    private static RecipeCustomSortingConfig config;

    public static void loadConfig() {
        try {
            // Ensure config directory exists
            Files.createDirectories(RECIPE_CUSTOM_SORTING_CONFIG_PATH.getParent());

            if (Files.exists(RECIPE_CUSTOM_SORTING_CONFIG_PATH)) {
                // Read and parse JSON
                String json = Files.readString(RECIPE_CUSTOM_SORTING_CONFIG_PATH);
                config = GSON.fromJson(json, RecipeCustomSortingConfig.class);
                if (config == null
                    || config.getRecipeCustomSorting() == null
                )
                {
                    BeyondBetaTweaks.LOGGER.warn("Invalid recipe custom sorting config file, using default configuration");
                    config = new RecipeCustomSortingConfig();
                    // don't save config in case of invalid config, don't want to overwrite even if broken
                    // saveConfig();
                }
            } else {
                // Create default config
                BeyondBetaTweaks.LOGGER.info("Recipe custom sorting config file not found, creating default configuration");
                config = new RecipeCustomSortingConfig();
                saveConfig();
            }
        } catch (IOException e) {
            BeyondBetaTweaks.LOGGER.error("Failed to load recipe custom sorting config, using default configuration", e);
            config = new RecipeCustomSortingConfig();
        }
    }

    public static void saveConfig() {
        try {
            String json = GSON.toJson(config);
            Files.writeString(RECIPE_CUSTOM_SORTING_CONFIG_PATH, json);
        } catch (IOException e) {
            BeyondBetaTweaks.LOGGER.error("Failed to save config", e);
        }
    }

    public static RecipeCustomSortingConfig getRecipeCustomSortingConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }
}
