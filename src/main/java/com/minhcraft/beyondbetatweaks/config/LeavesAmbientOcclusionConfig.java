package com.minhcraft.beyondbetatweaks.config;

import org.spongepowered.include.com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;
import java.util.Map;

public final class LeavesAmbientOcclusionConfig {

    @SerializedName("defaultLeavesAmbientOcclusionReductionFactor")
    private float defaultLeavesAmbientOcclusionReductionFactor = 1.0f;

    @SerializedName("leavesAmbientOcclusionReductionFactor")
    private final Map<String, Float> leavesAmbientOcclusionReductionFactor = new LinkedHashMap<>();

    public LeavesAmbientOcclusionConfig() {
        leavesAmbientOcclusionReductionFactor.put("minecraft:oak_leaves", 0.5f);
        leavesAmbientOcclusionReductionFactor.put("minecraft:birch_leaves", 0.5f);
        leavesAmbientOcclusionReductionFactor.put("minecraft:spruce_leaves", 0.5f);
        leavesAmbientOcclusionReductionFactor.put("minecraft:jungle_leaves", 0.5f);
        leavesAmbientOcclusionReductionFactor.put("minecraft:acacia_leaves", 0.5f);
        leavesAmbientOcclusionReductionFactor.put("minecraft:dark_oak_leaves", 0.5f);
        leavesAmbientOcclusionReductionFactor.put("minecraft:mangrove_leaves", 0.5f);
        leavesAmbientOcclusionReductionFactor.put("minecraft:cherry_leaves", 0.5f);
        leavesAmbientOcclusionReductionFactor.put("minecraft:azalea_leaves", 0.5f);
        leavesAmbientOcclusionReductionFactor.put("minecraft:flowering_azalea_leaves", 0.5f);
    }

    public float getDefaultLeavesAmbientOcclusionReductionFactor() {
        return defaultLeavesAmbientOcclusionReductionFactor;
    }

    public Map<String, Float> getLeavesAmbientOcclusionReductionFactor() {
        return leavesAmbientOcclusionReductionFactor;
    }
}