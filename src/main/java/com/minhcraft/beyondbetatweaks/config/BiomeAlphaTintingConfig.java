package com.minhcraft.beyondbetatweaks.config;

import org.spongepowered.include.com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;
import java.util.Map;

public final class BiomeAlphaTintingConfig {

    @SerializedName("defaultBiomeAlphaTintingFactor")
    private final float defaultBiomeAlphaTintingFactor = 0.0f;

    @SerializedName("biomeAlphaTintingFactor")
    private final Map<String, Float> biomeAlphaTintingFactor = new LinkedHashMap<>();

    @SerializedName("blockAlphaTintingFactor")
    private final Map<String, Float> blockAlphaTintingFactor = new LinkedHashMap<>();

    @SerializedName("defaultBlockAlphaTintingFactor")
    private final float defaultBlockAlphaTintingFactor = 0.0f;

    public BiomeAlphaTintingConfig() {
        // Default biome configuration
        biomeAlphaTintingFactor.put("minecraft:plains", 1.0f);
        biomeAlphaTintingFactor.put("minecraft:savanna", 1.0f);
        biomeAlphaTintingFactor.put("minecraft:desert", 1.0f);
        biomeAlphaTintingFactor.put("minecraft:badlands", 1.0f);
        biomeAlphaTintingFactor.put("minecraft:eroded_badlands", 1.0f);
        biomeAlphaTintingFactor.put("minecraft:snowy_plains", 1.0f);

        // Default block factors — leaves get less overlay than grass
        blockAlphaTintingFactor.put("minecraft:grass_block", 1.0f);
        blockAlphaTintingFactor.put("minecraft:grass", 1.0f);
        blockAlphaTintingFactor.put("minecraft:tall_grass", 1.0f);
        blockAlphaTintingFactor.put("minecraft:fern", 1.0f);
        blockAlphaTintingFactor.put("minecraft:large_fern", 1.0f);
        blockAlphaTintingFactor.put("minecraft:potted_fern", 1.0f);
        blockAlphaTintingFactor.put("minecraft:vines", 1.0f);
        blockAlphaTintingFactor.put("minecraft:oak_leaves", 0.0f);
        blockAlphaTintingFactor.put("minecraft:birch_leaves", 0.0f);
        blockAlphaTintingFactor.put("minecraft:spruce_leaves", 0.0f);
        blockAlphaTintingFactor.put("minecraft:jungle_leaves", 0.0f);
        blockAlphaTintingFactor.put("minecraft:acacia_leaves", 0.0f);
        blockAlphaTintingFactor.put("minecraft:dark_oak_leaves", 0.0f);
        blockAlphaTintingFactor.put("minecraft:mangrove_leaves", 0.0f);
        blockAlphaTintingFactor.put("minecraft:cherry_leaves", 0.0f);
        blockAlphaTintingFactor.put("minecraft:azalea_leaves", 0.0f);
        blockAlphaTintingFactor.put("minecraft:flowering_azalea_leaves", 0.0f);
    }

    public float getDefaultBiomeAlphaTintingStrength() {
        return defaultBiomeAlphaTintingFactor;
    }

    public Map<String, Float> getBiomeAlphaTintingFactor() {
        return biomeAlphaTintingFactor;
    }

    public float getDefaultBlockAlphaTintingStrength() {
        return defaultBlockAlphaTintingFactor;
    }

    public Map<String, Float> getBlockAlphaTintingFactor() {
        return blockAlphaTintingFactor;
    }
}