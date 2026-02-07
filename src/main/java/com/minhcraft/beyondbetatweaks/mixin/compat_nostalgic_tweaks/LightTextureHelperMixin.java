package com.minhcraft.beyondbetatweaks.mixin.compat_nostalgic_tweaks;

import com.llamalad7.mixinextras.sugar.Local;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import mod.adrenix.nostalgic.helper.candy.light.LightTextureHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightTextureHelper.class)
public abstract class LightTextureHelperMixin {

    @Shadow
    public static float getLightmapBrightness(int i, boolean isSkyLight) {
        return 0;
    }

    @Inject(
            method = "getLightmapBrightness",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void beyond_beta_tweaks$allowDynamicLightBrightnessToAffectBlockLight(
            int i, boolean isSkyLight, CallbackInfoReturnable<Float> cir,
            @Local double gammaSetting,
            @Local(ordinal = 2) float lightmapBrightness) {
        if (gammaSetting > 0.0D && !isSkyLight && CandyTweak.DYNAMIC_LIGHT_BRIGHTNESS.get() && ModConfig.enableDynamicLightBrightnessAffectBlockLight)
        {
            // dampen effect of gamma above default setting so that 1.0 gamma = 0.66 gamma and 0.5 gamma = 0.5 gamma
            if (gammaSetting > 0.5D) {
                gammaSetting = ((gammaSetting - 0.5D) / 3.0D) + 0.5D;
            }

            float maxBrightness = getLightmapBrightness(15, true) - 0.05F;
            float shiftBrightness = lightmapBrightness + (float) (ModConfig.dynamicLightBrightnessBlockLightScale * gammaSetting);
            lightmapBrightness = Mth.clamp(shiftBrightness, lightmapBrightness, maxBrightness);
        }
        cir.setReturnValue(lightmapBrightness);
    }
}
