package com.minhcraft.beyondbetatweaks.mixin.feature.moon_phase_affect_fog_color;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import com.minhcraft.beyondbetatweaks.util.CloudColorHelper;
import dev.imb11.fog.api.FogColors;
import dev.imb11.fog.client.FogManager;
import dev.imb11.fog.client.util.color.Color;
import dev.imb11.fog.client.util.math.InterpolatedValue;
import dev.imb11.fog.config.FogConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogManager.class)
public abstract class FogManagerMixin {

    @Shadow @Final public InterpolatedValue fogStart;
    @Shadow @Final public InterpolatedValue undergroundness;
    @Shadow @Final public InterpolatedValue darkness;
    @Shadow @Final public InterpolatedValue fogEnd;
    @Shadow @Final public InterpolatedValue fogColorRed;
    @Shadow @Final public InterpolatedValue fogColorGreen;
    @Shadow @Final public InterpolatedValue fogColorBlue;
    @Shadow @Final public InterpolatedValue raininess;
    @Shadow @Final public InterpolatedValue currentStartMultiplier;
    @Shadow @Final public InterpolatedValue currentEndMultiplier;
    @Unique
    private static final Color NightFogColorFullMoon = Color.parse(ModConfig.nightFogColorFullMoon);

    @Unique
    private static final Color NightFogColorNewMoon = Color.parse(ModConfig.nightFogColorNewMoon);

    // Calculate the final night color based on moon phase if new moon night color is defined
    @Unique
    private static Color getMoonPhaseAffectedNightColor(@NotNull ClientLevel level) {
        return NightFogColorFullMoon.lerp(
                NightFogColorNewMoon,
                CloudColorHelper.getMoonPhaseBlendFactor(level.getMoonPhase()));
    }

    @WrapOperation(
            method = "onEndTick",
            at = @At(value = "INVOKE", target = "Ldev/imb11/fog/api/FogColors;getNightColor()Ldev/imb11/fog/client/util/color/Color;")
    )
    private Color beyond_beta_tweaks$changeNightFogColorBasedOnMoonPhase(FogColors fogColors, Operation<Color> original, ClientLevel level) {
        if (ModConfig.enableMoonPhaseAffectFogColor) {
            return getMoonPhaseAffectedNightColor(level);
        } else {
            return original.call(fogColors);
        }
    }

    // Modify getFogSettings
    // Changes:
    // - Lerp raininess fog changes for gradual transition
    @Inject(
            method = "getFogSettings",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beyond_beta_tweaks$modifyGetFogSettings(float tickDelta, float viewDistance, CallbackInfoReturnable<FogManager.FogSettings> cir) {
        float fogStartValue = this.fogStart.get(tickDelta) * viewDistance;
        float undergroundFogMultiplier = 1.0F;
        if (!FogConfig.getInstance().disableUndergroundFogMultiplier) {
            undergroundFogMultiplier = this.undergroundness.get(tickDelta);
            undergroundFogMultiplier = Mth.lerp(this.darkness.get(tickDelta), undergroundFogMultiplier, 1.0F);
        }

        float fogEndValue = viewDistance * this.fogEnd.get(tickDelta);
        if (undergroundFogMultiplier > 0.0F) {
            fogEndValue /= 1.0F + undergroundFogMultiplier;
//            fogStartValue *= Math.max(0.1F, 0.5F - undergroundFogMultiplier);
            // Multiply by a value that is 1.0 when underGroundFogMultiplier is 0 and 0.1 when underGroundFogMultiplier is 0.4 or greater
            // Fixes an issue in the old logic where undergroundFogMultiplier never actually goes back down to 0
            // which causes fogStartValue to nearly always be halved
            fogStartValue *= (float) (1 - 0.9 * Math.min(1, undergroundFogMultiplier / 0.4));
        }

        float fogRed = this.fogColorRed.get(tickDelta);
        float fogGreen = this.fogColorGreen.get(tickDelta);
        float fogBlue = this.fogColorBlue.get(tickDelta);
        float raininessValue = this.raininess.get(tickDelta);
        if (!FogConfig.getInstance().disableRaininessEffect && raininessValue > 0.0F) {
            fogEndValue /= 1.0F + raininessValue;
            // EDIT: lerp raininess fog effect change
            fogRed = Mth.lerp(raininessValue, fogRed, Math.max(0.1F, fogRed - 0.5F * raininessValue));
            fogGreen = Mth.lerp(raininessValue, fogGreen, Math.max(0.1F, fogGreen - 0.5F * raininessValue));
            fogBlue = Mth.lerp(raininessValue, fogBlue, Math.max(0.1F, fogBlue - 0.5F * raininessValue));
        }

        float darknessValue = this.darkness.get(tickDelta);
        fogRed *= 1.0F - darknessValue;
        fogGreen *= 1.0F - darknessValue;
        fogBlue *= 1.0F - darknessValue;
        fogStartValue *= this.currentStartMultiplier.get(tickDelta);
        fogEndValue *= this.currentEndMultiplier.get(tickDelta);
        cir.setReturnValue(new FogManager.FogSettings(fogStartValue, fogEndValue, fogRed, fogGreen, fogBlue));
    }
}
