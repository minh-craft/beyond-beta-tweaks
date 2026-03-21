package com.minhcraft.beyondbetatweaks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class BiomeAlphaTintingConfigLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve(BeyondBetaTweaks.MOD_ID + "/biome_alpha_tinting.json");

    private static BiomeAlphaTintingConfig config;

    // Cached lookup maps for fast runtime access
    private static Map<ResourceLocation, Float> biomeLookup = new HashMap<>();
    private static Map<ResourceLocation, Float> blockLookup = new HashMap<>();
    private static float defaultBiomeAlphaTintingStrength = 0.0f;
    private static float defaultBlockAlphaTintingStrength = 0.0f;

    public static void loadConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH);
                config = GSON.fromJson(json, BiomeAlphaTintingConfig.class);
                if (config == null || config.getBiomeAlphaTintingFactor() == null || config.getBlockAlphaTintingFactor() == null) {
                    BeyondBetaTweaks.LOGGER.warn("Invalid biome alpha tinting config file, using default configuration");
                    config = new BiomeAlphaTintingConfig();
                }
            } else {
                BeyondBetaTweaks.LOGGER.info("Biome alpha tinting config file not found, creating default configuration");
                config = new BiomeAlphaTintingConfig();
                saveConfig();
            }
        } catch (IOException e) {
            BeyondBetaTweaks.LOGGER.error("Failed to load biome alpha tinting config, using default configuration", e);
            config = new BiomeAlphaTintingConfig();
        }

        rebuildLookup();
    }

    public static void saveConfig() {
        try {
            String json = GSON.toJson(config);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            BeyondBetaTweaks.LOGGER.error("Failed to save biome alpha tinting config", e);
        }
    }

    private static void rebuildLookup() {
        biomeLookup = new HashMap<>();
        blockLookup = new HashMap<>();
        defaultBiomeAlphaTintingStrength = config.getDefaultBiomeAlphaTintingStrength();
        defaultBlockAlphaTintingStrength = config.getDefaultBlockAlphaTintingStrength();

        for (Map.Entry<String, Float> entry : config.getBiomeAlphaTintingFactor().entrySet()) {
            ResourceLocation id = new ResourceLocation(entry.getKey());
            biomeLookup.put(id, Mth.clamp(entry.getValue(), 0.0F, 1.0F));
        }

        for (Map.Entry<String, Float> entry : config.getBlockAlphaTintingFactor().entrySet()) {
            ResourceLocation id = new ResourceLocation(entry.getKey());
            blockLookup.put(id, Mth.clamp(entry.getValue(), 0.0F, 1.0F));
        }

        BeyondBetaTweaks.LOGGER.info("Biome alpha tinting config loaded: {} biomes, {} blocks configured",
                biomeLookup.size(), blockLookup.size());
    }

    public static float getAlphaTintingFactorForBiome(ResourceLocation biomeId) {
        if (config == null) {
            loadConfig();
        }
        return biomeLookup.getOrDefault(biomeId, defaultBiomeAlphaTintingStrength);
    }

    public static float getAlphaTintingFactorForBlock(ResourceLocation blockId) {
        if (config == null) {
            loadConfig();
        }
        return blockLookup.getOrDefault(blockId, defaultBlockAlphaTintingStrength);
    }

    public static boolean isBlockAlphaTinted(ResourceLocation blockId) {
        if (config == null) {
            loadConfig();
        }
        float factor = blockLookup.getOrDefault(blockId, defaultBlockAlphaTintingStrength);
        return factor > 0.0f;
    }

    public static BiomeAlphaTintingConfig getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }
}