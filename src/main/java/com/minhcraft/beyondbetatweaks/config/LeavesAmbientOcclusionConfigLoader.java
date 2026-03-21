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

public class LeavesAmbientOcclusionConfigLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve(BeyondBetaTweaks.MOD_ID + "/leaves_ambient_occlusion.json");

    private static LeavesAmbientOcclusionConfig config;

    private static Map<ResourceLocation, Float> leavesLookup = new HashMap<>();
    private static float defaultLeavesAmbientOcclusionReductionFactor = 1.0f;

    public static void loadConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH);
                config = GSON.fromJson(json, LeavesAmbientOcclusionConfig.class);
                if (config == null || config.getLeavesAmbientOcclusionReductionFactor() == null) {
                    BeyondBetaTweaks.LOGGER.warn("Invalid Leaves Ambient Occlusion config file, using default configuration");
                    config = new LeavesAmbientOcclusionConfig();
                }
            } else {
                BeyondBetaTweaks.LOGGER.info("Leaves Ambient Occlusion config file not found, creating default configuration");
                config = new LeavesAmbientOcclusionConfig();
                saveConfig();
            }
        } catch (IOException e) {
            BeyondBetaTweaks.LOGGER.error("Failed to load Leaves Ambient Occlusion config, using default configuration", e);
            config = new LeavesAmbientOcclusionConfig();
        }

        rebuildLookup();
    }

    public static void saveConfig() {
        try {
            String json = GSON.toJson(config);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            BeyondBetaTweaks.LOGGER.error("Failed to save Leaves Ambient Occlusion config", e);
        }
    }

    private static void rebuildLookup() {
        leavesLookup = new HashMap<>();
        defaultLeavesAmbientOcclusionReductionFactor = config.getDefaultLeavesAmbientOcclusionReductionFactor();

        for (Map.Entry<String, Float> entry : config.getLeavesAmbientOcclusionReductionFactor().entrySet()) {
            ResourceLocation id = new ResourceLocation(entry.getKey());
            leavesLookup.put(id, Mth.clamp(entry.getValue(), 0.0F, 1.0F));
        }

        BeyondBetaTweaks.LOGGER.info("Leaves Ambient Occlusion config loaded: {} leaf types configured", leavesLookup.size());
    }

    public static float getAmbientOcclusionReductionFactor(ResourceLocation blockId) {
        if (config == null) {
            loadConfig();
        }
        return leavesLookup.getOrDefault(blockId, defaultLeavesAmbientOcclusionReductionFactor);
    }

    public static LeavesAmbientOcclusionConfig getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }
}