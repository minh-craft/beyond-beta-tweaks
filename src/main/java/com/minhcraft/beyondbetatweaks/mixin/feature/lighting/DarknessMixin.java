package com.minhcraft.beyondbetatweaks.mixin.feature.lighting;

import com.llamalad7.mixinextras.sugar.Local;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import grondag.darkness.Darkness;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Darkness.class)
public abstract class DarknessMixin {

    @Shadow @Final private static float[][] LUMINANCE;

    @Unique
    private static final float[] CUSTOM_MOON_BRIGHTNESS_BY_PHASE = new float[] {
            ModConfig.fullMoonBrightness,
            ModConfig.threeQuartersMoonBrightness,
            ModConfig.halfMoonBrightness,
            ModConfig.oneQuarterMoonBrightness,
            ModConfig.newMoonBrightness,
            ModConfig.oneQuarterMoonBrightness,
            ModConfig.halfMoonBrightness,
            ModConfig.threeQuartersMoonBrightness
    };

    // Override moon phase darkness setting
    @ModifyArg(method = "skyFactor",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F"),
            index = 1)
    private static float beyond_beta_tweaks$overrideBrightnessPercentage(float brightnessPercentage, @Local(argsOnly = true) Level world) {
        return CUSTOM_MOON_BRIGHTNESS_BY_PHASE[world.getMoonPhase()];
    }

    @Inject(
            method = "updateLuminance",
            at = @At("TAIL"),
            remap = false
    )
    private static void beyond_beta_tweaks$increaseEndLuminance(
            float tickDelta, Minecraft client, GameRenderer worldRenderer, float prevFlicker, CallbackInfo ci) {
        if (client.level == null || client.level.dimension() != Level.END || !Darkness.enabled)
            return;

        float brightnessScale = ModConfig.endDimensionTrueDarknessBrightnessScaling;

        for (int sky = 0; sky < 16; sky++) {
            for (int block = 0; block < 16; block++) {
                LUMINANCE[block][sky] = Math.min(1.0f, LUMINANCE[block][sky] * brightnessScale);
            }
        }
    }


    // Override gamma settings
    // Using this as a hack to increase the maximum brightness level of True Darkness at night
    @Redirect(
            method = "updateLuminance",
            at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F"))
    private static float beyond_beta_tweaks$modifyGammaSettings(Double instance) {
        // dampen effect of gamma above 40% so that 1.0 gamma = 0.6 gamma and 0.4 gamma = 0.4 gamma
        float gamma = (instance.floatValue() > 0.4F)
                ? (((instance.floatValue() - 0.4F) / 3.0F) + 0.4F)
                : instance.floatValue();
        return gamma * ModConfig.scaleTrueDarknessGamma;
    }


    // Override luminance value
    // Using this to modify the minimum level of True Darkness
    @Inject(
            method = "luminance",
            at = @At("RETURN"),
            cancellable = true,
            remap = false)
    private static void beyond_beta_tweaks$overrideLuminance(float r, float g, float b, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(Math.max(ModConfig.trueDarknessMinimumLightLevel, cir.getReturnValue()));
        cir.cancel();
    }
}
