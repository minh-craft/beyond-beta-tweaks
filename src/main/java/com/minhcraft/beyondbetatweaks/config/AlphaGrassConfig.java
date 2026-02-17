package com.minhcraft.beyondbetatweaks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for the Alpha Grass mod.
 *
 * The config file (config/alphagrass.json) controls:
 * - blend_mode: "screen" or "additive"
 *     "screen"   -> 1 - (1 - tex) * (1 - tint * alphaFactor)  — brighter, more saturated
 *     "additive"  -> lerp(tex * tint, tex + tint * intensity, alphaFactor) — lighter, washed-out alpha feel
 * - affect_plants: whether to also apply alpha-style tinting to grass, ferns, tall grass, etc.
 * - affect_leaves: whether to also apply alpha-style tinting to leaves
 * - biomes: a map of biome ID -> alpha_factor (0.0 = vanilla, 1.0 = full alpha-style)
 *
 * Biomes not listed default to 0.0 (vanilla multiply tinting).
 */
public class AlphaGrassConfig {

    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(BeyondBetaTweaks.MOD_ID + "/alpha_grass.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<ResourceLocation, Float> BIOME_ALPHA_FACTORS = new HashMap<>();
    private static BlendMode blendMode = BlendMode.SCREEN;
    private static boolean affectPlants = false;
    private static boolean affectLeaves = false;

    public enum BlendMode {
        SCREEN,
        ADDITIVE
    }

    public static void load() {
        Path configFile = PATH;

        if (!Files.exists(configFile)) {
            createDefaultConfig(configFile);
        }

        try (Reader reader = Files.newBufferedReader(configFile)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            // Read blend mode
            if (root.has("blend_mode")) {
                String mode = root.get("blend_mode").getAsString().toUpperCase();
                try {
                    blendMode = BlendMode.valueOf(mode);
                } catch (IllegalArgumentException e) {
                    BeyondBetaTweaks.LOGGER.warn("Unknown blend_mode '{}', defaulting to SCREEN", mode);
                    blendMode = BlendMode.SCREEN;
                }
            }

            // Read affect_plants
            if (root.has("affect_plants")) {
                affectPlants = root.get("affect_plants").getAsBoolean();
            }

            // Read affect_leaves
            if (root.has("affect_leaves")) {
                affectLeaves = root.get("affect_leaves").getAsBoolean();
            }

            // Read biome entries
            BIOME_ALPHA_FACTORS.clear();
            if (root.has("biomes")) {
                JsonObject biomes = root.getAsJsonObject("biomes");
                for (Map.Entry<String, JsonElement> entry : biomes.entrySet()) {
                    ResourceLocation biomeId = new ResourceLocation(entry.getKey());
                    float factor = entry.getValue().getAsFloat();
                    factor = Math.max(0.0f, Math.min(1.0f, factor)); // clamp 0-1
                    BIOME_ALPHA_FACTORS.put(biomeId, factor);
                }
            }

            BeyondBetaTweaks.LOGGER.info("Config loaded: blend_mode={}, affect_plants={}, affect_leaves={}, biomes={}",
                    blendMode, affectPlants, affectLeaves, BIOME_ALPHA_FACTORS.size());

        } catch (Exception e) {
            BeyondBetaTweaks.LOGGER.error("Failed to load alphagrass.json config", e);
            createDefaultConfig(configFile);
        }
    }

    private static void createDefaultConfig(Path configFile) {
        JsonObject root = new JsonObject();

        root.addProperty("_comment_blend_mode",
                "Blend mode for alpha-style grass. Options: SCREEN (brighter, saturated) or ADDITIVE (lighter, washed-out alpha feel)");
        root.addProperty("blend_mode", "SCREEN");

        root.addProperty("_comment_affect_plants",
                "If true, alpha-style tinting also applies to short grass, tall grass, and ferns");
        root.addProperty("affect_plants", false);

        root.addProperty("_comment_affect_leaves",
                "If true, alpha-style tinting also applies to leaves");
        root.addProperty("affect_leaves", false);

        root.addProperty("_comment_biomes",
                "Map of biome ID to alpha_factor (0.0 = vanilla, 1.0 = full alpha-style). Biomes not listed use vanilla tinting.");

        JsonObject biomes = new JsonObject();
        // Default example entries — plains gets full alpha, forest gets partial
        biomes.addProperty("minecraft:plains", 1.0);
        biomes.addProperty("minecraft:forest", 0.0);
        biomes.addProperty("hybrid-beta:seasonal_forest", 0.0);
        root.add("biomes", biomes);

        try {
            Files.createDirectories(configFile.getParent());
            try (Writer writer = Files.newBufferedWriter(configFile)) {
                GSON.toJson(root, writer);
            }
            BeyondBetaTweaks.LOGGER.info("Created default alphagrass.json config");
        } catch (IOException e) {
            BeyondBetaTweaks.LOGGER.error("Failed to create default config", e);
        }
    }

    /**
     * Get the alpha factor for a specific biome.
     * Returns 0.0 (vanilla) if the biome is not configured.
     */
    public static float getAlphaFactor(ResourceLocation biomeId) {
        return BIOME_ALPHA_FACTORS.getOrDefault(biomeId, 0.0f);
    }

    /**
     * Get all configured biome entries.
     */
    public static Map<ResourceLocation, Float> getBiomeEntries() {
        return BIOME_ALPHA_FACTORS;
    }

    public static BlendMode getBlendMode() {
        return blendMode;
    }

    public static boolean shouldAffectPlants() {
        return affectPlants;
    }

    public static boolean shouldAffectLeaves() {
        return affectLeaves;
    }
}
