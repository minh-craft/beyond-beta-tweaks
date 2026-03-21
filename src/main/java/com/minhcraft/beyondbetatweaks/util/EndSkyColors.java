package com.minhcraft.beyondbetatweaks.util;

import com.minhcraft.beyondbetatweaks.config.ModConfig;

public class EndSkyColors {
    public static float SKY_R, SKY_G, SKY_B;
    public static float FOG_R, FOG_G, FOG_B;
    public static float FOG_UPPER_R, FOG_UPPER_G, FOG_UPPER_B;

    public static void reload() {
        int skyColor = Integer.parseInt(ModConfig.endDimensionSkyColor.replace("#", ""), 16);
        SKY_R = ((skyColor >> 16) & 0xFF) / 255.0f;
        SKY_G = ((skyColor >> 8) & 0xFF) / 255.0f;
        SKY_B = (skyColor & 0xFF) / 255.0f;

        int fogColor = Integer.parseInt(ModConfig.endDimensionFogColor.replace("#", ""), 16);
        FOG_R = ((fogColor >> 16) & 0xFF) / 255.0f;
        FOG_G = ((fogColor >> 8) & 0xFF) / 255.0f;
        FOG_B = (fogColor & 0xFF) / 255.0f;

        int fogColor2 = Integer.parseInt(ModConfig.endDimensionFogUpperColor.replace("#", ""), 16);
        FOG_UPPER_R = ((fogColor2 >> 16) & 0xFF) / 255.0f;
        FOG_UPPER_G = ((fogColor2 >> 8) & 0xFF) / 255.0f;
        FOG_UPPER_B = (fogColor2 & 0xFF) / 255.0f;
    }

    static { reload(); }
}