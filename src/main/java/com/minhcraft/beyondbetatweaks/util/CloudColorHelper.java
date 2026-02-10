package com.minhcraft.beyondbetatweaks.util;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Unique;

public abstract class CloudColorHelper {

    @Unique
    public static float getCloudColor(long worldTime, int moonPhase) {
        // Define the color values
        float dayColor = 1.0f;
        float nightColor = Mth.lerp(
                getMoonPhaseBlendFactor(moonPhase),
                ModConfig.whitenedCloudFullMoonBrightness,
                ModConfig.whitenedCloudNewMoonBrightness);
        float cloudColor;

        if (worldTime < 10000) {
            // Daytime
            cloudColor = dayColor;
        } else if (worldTime < 11000) {
            // Transition starts just after 5:00 PM but still dayColor
            cloudColor = dayColor;
        } else if (worldTime < 13000) {
            // Blend from dayColor to nightColor
            float t = (float) (worldTime - 11000) / 2000;
            cloudColor = Mth.lerp(t, dayColor, nightColor);
        } else if (worldTime < 22000) {
            // Nighttime
            cloudColor = nightColor;
        } else if (worldTime < 23000) {
            // Blend from nightColor to dayColor
            float t = (float) (worldTime - 22000) / 1000;
            cloudColor = Mth.lerp(t, nightColor, dayColor);
        } else {
            // Constant dayColor from 23000 to 24000 ticks
            cloudColor = dayColor;
        }

        return cloudColor;
    }

    @Unique
    public static float getMoonPhaseBlendFactor(int moonPhase) {
        return switch (moonPhase) {
            case 0    -> 0.0f;  // new moon

            case 1, 7 -> 0.25f; // 1/4 moon
            case 2, 6 -> 0.5f;  // 1/2 moon
            case 3, 5 -> 0.75f; // 3/4 moon
            case 4    ->  1.0f; // full moon

            default -> 1.0f;
        };
    }
}
