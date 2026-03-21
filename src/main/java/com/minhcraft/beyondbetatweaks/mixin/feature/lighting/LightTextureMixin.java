package com.minhcraft.beyondbetatweaks.mixin.feature.lighting;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
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
    private void beyond_beta_tweaks$brightenAndFixLightmap(float partialTick, CallbackInfo ci) {
        if (this.lightPixels == null) return;

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        // Night sky brightening (overworld only) ---
        if (level.dimension() == Level.OVERWORLD && ModConfig.enableNightSkylightLightTextureBoost) {
            float skyDarken = level.getSkyDarken(1.0f);
            float nightAmount = 1.0f - skyDarken;

            if (nightAmount >= 0.05f) {
                int moonPhase = level.getMoonPhase();
                float moonMultiplier = MOON_PHASE_BRIGHTNESS_BOOST_MULTIPLIER[moonPhase];

                if (moonMultiplier >= 0.01f) {
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

            // --- Overworld lightmap smoothing ---
            if (ModConfig.enableOverworldLightmapSmoothing) {
                for (int sky = 0; sky < 16; sky++) {
                    int darkPixel = this.lightPixels.getPixelRGBA(0, sky);
                    int brightPixel = this.lightPixels.getPixelRGBA(15, sky);

                    int darkR = darkPixel & 0xFF;
                    int darkG = (darkPixel >> 8) & 0xFF;
                    int darkB = (darkPixel >> 16) & 0xFF;

                    int brightR = brightPixel & 0xFF;
                    int brightG = (brightPixel >> 8) & 0xFF;
                    int brightB = (brightPixel >> 16) & 0xFF;

                    for (int block = 1; block < 15; block++) {
                        int curr = this.lightPixels.getPixelRGBA(block, sky);
                        int currR = curr & 0xFF;
                        int currG = (curr >> 8) & 0xFF;
                        int currB = (curr >> 16) & 0xFF;

                        float t = block / 15.0f;
                        t = t * t;

                        int lerpR = (int)(darkR + (brightR - darkR) * t);
                        int lerpG = (int)(darkG + (brightG - darkG) * t);
                        int lerpB = (int)(darkB + (brightB - darkB) * t);

                        int newR = Math.round(currR * (1 - ModConfig.overworldLightmapGradientSmoothingFactor) + lerpR * ModConfig.overworldLightmapGradientSmoothingFactor);
                        int newG = Math.round(currG * (1 - ModConfig.overworldLightmapGradientSmoothingFactor) + lerpG * ModConfig.overworldLightmapGradientSmoothingFactor);
                        int newB = Math.round(currB * (1 - ModConfig.overworldLightmapGradientSmoothingFactor) + lerpB * ModConfig.overworldLightmapGradientSmoothingFactor);

                        newR = Mth.clamp(newR, 0, 255);
                        newG = Mth.clamp(newG, 0, 255);
                        newB = Mth.clamp(newB, 0, 255);

                        this.lightPixels.setPixelRGBA(block, sky,
                                0xFF000000 | (newB << 16) | (newG << 8) | newR);
                    }
                }
            }

        }
        // End dimension lightmap smoothing fix ---
        else if (level.dimension() == Level.END && ModConfig.enableEndDimensionLightmapGradientFix) {
            // The End has a constant ambient sky light that TD darkens.
            // The interaction with NT's grayscale creates a flat band at mid block light.
            // Fix: ensure each block light step produces a visible brightness change
            // by lerping the lightmap toward a smooth gradient.

            for (int sky = 0; sky < 16; sky++) {
                // Sample the darkest (block=0) and brightest (block=15) pixels
                int darkPixel = this.lightPixels.getPixelRGBA(0, sky);
                int brightPixel = this.lightPixels.getPixelRGBA(15, sky);

                int darkR = darkPixel & 0xFF;
                int darkG = (darkPixel >> 8) & 0xFF;
                int darkB = (darkPixel >> 16) & 0xFF;

                int brightR = brightPixel & 0xFF;
                int brightG = (brightPixel >> 8) & 0xFF;
                int brightB = (brightPixel >> 16) & 0xFF;

                for (int block = 1; block < 15; block++) {
                    int curr = this.lightPixels.getPixelRGBA(block, sky);
                    int currR = curr & 0xFF;
                    int currG = (curr >> 8) & 0xFF;
                    int currB = (curr >> 16) & 0xFF;

                    // Compute a smooth linear interpolation between dark and bright
                    float t = block / 15.0f;
                    // Use a curve to match Minecraft's light falloff (not linear)
                    t = t * t;

                    int lerpR = (int) (darkR + (brightR - darkR) * t);
                    int lerpG = (int) (darkG + (brightG - darkG) * t);
                    int lerpB = (int) (darkB + (brightB - darkB) * t);

                    // Blend between the original pixel and the smooth gradient
                    // 0.0 = all original, 1.0 = all smoothed
                    int newR = Math.round(currR * (1 - ModConfig.endDimensionLightmapGradientSmoothingFactor) + lerpR * ModConfig.endDimensionLightmapGradientSmoothingFactor);
                    int newG = Math.round(currG * (1 - ModConfig.endDimensionLightmapGradientSmoothingFactor) + lerpG * ModConfig.endDimensionLightmapGradientSmoothingFactor);
                    int newB = Math.round(currB * (1 - ModConfig.endDimensionLightmapGradientSmoothingFactor) + lerpB * ModConfig.endDimensionLightmapGradientSmoothingFactor);

                    // Never go below zero or above 255
                    newR = Mth.clamp(newR, 0, 255);
                    newG = Mth.clamp(newG, 0, 255);
                    newB = Mth.clamp(newB, 0, 255);

                    this.lightPixels.setPixelRGBA(block, sky,
                            0xFF000000 | (newB << 16) | (newG << 8) | newR);
                }
            }
        }
        // Nether dimension lightmap fix and true darkness scaling
        else if (level.dimension() == Level.NETHER && ModConfig.enableTrueDarknessNetherLightingAdjustments) {
            for (int sky = 0; sky < 16; sky++) {
                int darkPixel = this.lightPixels.getPixelRGBA(0, sky);
                int brightPixel = this.lightPixels.getPixelRGBA(15, sky);

                int darkR = darkPixel & 0xFF;
                int darkG = (darkPixel >> 8) & 0xFF;
                int darkB = (darkPixel >> 16) & 0xFF;

                int brightR = brightPixel & 0xFF;
                int brightG = (brightPixel >> 8) & 0xFF;
                int brightB = (brightPixel >> 16) & 0xFF;

                int ambientLevel = Math.round(ModConfig.netherDimensionTrueDarknessLevel * 40.0f);
                darkR = Math.max(darkR, ambientLevel);
                darkG = Math.max(darkG, ambientLevel);
                darkB = Math.max(darkB, ambientLevel);

                this.lightPixels.setPixelRGBA(0, sky,
                        0xFF000000 | (darkB << 16) | (darkG << 8) | darkR);

                for (int block = 1; block < 15; block++) {
                    int curr = this.lightPixels.getPixelRGBA(block, sky);
                    int currR = curr & 0xFF;
                    int currG = (curr >> 8) & 0xFF;
                    int currB = (curr >> 16) & 0xFF;

                    if (ModConfig.netherLightmapSmoothingFactor > 0.0f) {
                        float t = block / 15.0f;
                        t = t * t;

                        int lerpR = (int)(darkR + (brightR - darkR) * t);
                        int lerpG = (int)(darkG + (brightG - darkG) * t);
                        int lerpB = (int)(darkB + (brightB - darkB) * t);

                        currR = Math.round(currR * (1 - ModConfig.netherLightmapSmoothingFactor) + lerpR * ModConfig.netherLightmapSmoothingFactor);
                        currG = Math.round(currG * (1 - ModConfig.netherLightmapSmoothingFactor) + lerpG * ModConfig.netherLightmapSmoothingFactor);
                        currB = Math.round(currB * (1 - ModConfig.netherLightmapSmoothingFactor) + lerpB * ModConfig.netherLightmapSmoothingFactor);
                    }

                    currR = Math.max(currR, darkR);
                    currG = Math.max(currG, darkG);
                    currB = Math.max(currB, darkB);

                    currR = Mth.clamp(currR, 0, 255);
                    currG = Mth.clamp(currG, 0, 255);
                    currB = Mth.clamp(currB, 0, 255);

                    this.lightPixels.setPixelRGBA(block, sky,
                            0xFF000000 | (currB << 16) | (currG << 8) | currR);
                }
            }
        }
    }
}
