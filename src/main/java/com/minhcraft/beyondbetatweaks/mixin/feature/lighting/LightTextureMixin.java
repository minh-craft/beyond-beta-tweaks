package com.minhcraft.beyondbetatweaks.mixin.feature.lighting;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LightTexture.class, priority = 1500)
public abstract class LightTextureMixin {

    @Final
    @Shadow private NativeImage lightPixels;

    @Unique
    private static final float[] MOON_PHASE_BRIGHTNESS_BOOST_MULTIPLIER = new float[] {
            ModConfig.fullMoonBrightnessBoost, // Full moon
            ModConfig.threeQuartersMoonBrightnessBoost, // Three-quarters moon
            ModConfig.halfMoonBrightnessBoost, // Half moon
            ModConfig.oneQuarterMoonBrightnessBoost, // One-quarters moon
            ModConfig.newMoonBrightnessBoost, // New moon
            ModConfig.oneQuarterMoonBrightnessBoost, // One-quarters moon
            ModConfig.halfMoonBrightnessBoost, // Half moon
            ModConfig.threeQuartersMoonBrightnessBoost, // Three-quarters moon
    };

    @Inject(
            method = "updateLightTexture",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;upload()V"
            )
    )
    private void beyond_beta_tweaks$brightenNightSkyLight(float partialTick, CallbackInfo ci) {
        if (!ModConfig.enableNightSkylightLightTextureBoost || this.lightPixels == null) return;

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        float skyDarken = level.getSkyDarken(1.0f);
        float nightAmount = 1.0f - skyDarken;

        if (nightAmount < 0.05f) return;

        int moonPhase = level.getMoonPhase();
        float moonMultiplier = MOON_PHASE_BRIGHTNESS_BOOST_MULTIPLIER[moonPhase];

        if (moonMultiplier < 0.01f) return;

        float strength = ModConfig.boostNightBrightnessFactor;
        float effectiveStrength = strength * moonMultiplier;

        for (int skyLight = 1; skyLight < 16; skyLight++) {
            float skyFactor = skyLight / 15.0f;
            float addAmount = nightAmount * effectiveStrength * skyFactor * ModConfig.maxPossibleBoostedNightBrightness;

            for (int blockLight = 0; blockLight < 16; blockLight++) {
                int pixel = this.lightPixels.getPixelRGBA(blockLight, skyLight);
                int r = pixel & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = (pixel >> 16) & 0xFF;

                float currentBrightness = Math.max(r, Math.max(g, b)) / 255.0f;
                float fadeFactor = 1.0f - currentBrightness;
                int add = Math.round(addAmount * fadeFactor);

                int newR = Math.min(255, r + (int)(add * 0.85f));
                int newG = Math.min(255, g + (int)(add * 0.9f));
                int newB = Math.min(255, b + add);

                newR = Math.max(newR, r);
                newG = Math.max(newG, g);
                newB = Math.max(newB, b);

                this.lightPixels.setPixelRGBA(blockLight, skyLight,
                        0xFF000000 | (newB << 16) | (newG << 8) | newR);
            }
        }
    }
}
