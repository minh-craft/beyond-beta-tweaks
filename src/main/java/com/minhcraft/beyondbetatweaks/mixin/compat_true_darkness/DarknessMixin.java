package com.minhcraft.beyondbetatweaks.mixin.compat_true_darkness;

import com.llamalad7.mixinextras.sugar.Local;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import grondag.darkness.Darkness;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Darkness.class)
public abstract class DarknessMixin {

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
    private static float overrideBrightnessPercentage(float brightnessPercentage, @Local(argsOnly = true) Level world) {
        return CUSTOM_MOON_BRIGHTNESS_BY_PHASE[world.getMoonPhase()];
    }


    // Override gamma settings
    // Using this as a hack to increase the maximum brightness level of True Darkness at night
    @Redirect(
            method = "updateLuminance",
            at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F"))
    private static float modifyGammaSettings(Double instance) {
        return instance.floatValue() * ModConfig.scaleTrueDarknessGamma;
    }


    // Override luminance value
    // Using this to modify the minimum level of True Darkness
    @Inject(
            method = "luminance",
            at = @At("RETURN"),
            cancellable = true,
            remap = false)
    private static void overrideLuminance(float r, float g, float b, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(Math.max(ModConfig.trueDarknessMinimumLightLevel, cir.getReturnValue()));
        cir.cancel();
    }
}
