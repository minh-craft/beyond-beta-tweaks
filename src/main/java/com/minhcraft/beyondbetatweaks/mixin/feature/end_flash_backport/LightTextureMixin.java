package com.minhcraft.beyondbetatweaks.mixin.feature.end_flash_backport;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import com.minhcraft.beyondbetatweaks.interfaces.EndFlashAccessor;
import com.minhcraft.beyondbetatweaks.util.EndFlashState;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// End flash backport code adapted from https://github.com/Smallinger/Copper-Age-Backport by [Smallinger](https://github.com/Smallinger)
/**
 * Recreates the purple End flash tint by modifying the lightmap just before upload.
 * Mirrors the vanilla 1.21.10 behaviour without depending on the new renderer backend.
 */
@Mixin(value = LightTexture.class, priority = 1500)
public abstract class LightTextureMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private NativeImage lightPixels;

    @Unique
    private float beyond_beta_tweaks$endFlashIntensity;

    /**
     * Calculate End flash intensity before updating light texture
     */
    @Inject(method = "updateLightTexture", at = @At("HEAD"))
    private void beyond_beta_tweaks$captureEndFlashIntensity(float partialTicks, CallbackInfo ci) {
        this.beyond_beta_tweaks$endFlashIntensity = 0.0F;
        ClientLevel level = this.minecraft.level;
        if (level == null || level.effects().skyType() != DimensionSpecialEffects.SkyType.END) {
            return;
        }

        EndFlashState state = EndFlashAccessor.get(level);
        if (state == null || this.minecraft.options.hideLightningFlash().get()) {
            return;
        }

        float intensity = state.getIntensity(partialTicks);
        if (intensity <= 0.0F) {
            return;
        }

        if (this.minecraft.gui.getBossOverlay().shouldCreateWorldFog()) {
            intensity /= 3.0F;
        }

        this.beyond_beta_tweaks$endFlashIntensity = intensity;
    }

    /**
     * After the lightmap is computed but before upload:
     * 1. Flatten sky light contribution (copy sky=0 row to all rows). Skylight is only for calculating flash lighting, shouldn't affect regular lighting in the End
     * 2. If flash is active, add purple light scaled by sky light level
     */
    @Inject(method = "updateLightTexture",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;upload()V"))
    private void endsky$flattenSkyLightAndApplyFlash(float partialTicks, CallbackInfo ci) {
        ClientLevel level = this.minecraft.level;
        if (level == null || level.effects().skyType() != DimensionSpecialEffects.SkyType.END) {
            return;
        }

        float intensity = this.beyond_beta_tweaks$endFlashIntensity;
        float brightnessBoost = 1.0F + intensity * ModConfig.endFlashLightingIntensity;

        for (int sky = 0; sky < 16; sky++) {
            float skyFactor = sky / 15.0F;

            for (int block = 0; block < 16; block++) {
                // Start from the sky=0 baseline (flattened — no sky light effect)
                int pixel = this.lightPixels.getPixelRGBA(block, 0); // baseRow[block];
                int alpha = (pixel >>> 24) & 0xFF;
                int blue  = (pixel >>> 16) & 0xFF;
                int green = (pixel >>>  8) & 0xFF;
                int red   =  pixel         & 0xFF;

                // During a flash, add purple tint scaled by sky exposure
                if (intensity > 0.0F) {
                    float scaledIntensity = intensity * skyFactor;
                    red   = (int) Math.min(255.0F, (red   + ModConfig.endFlashLightRed * scaledIntensity) * brightnessBoost);
                    green = (int) Math.min(255.0F, (green + ModConfig.endFlashLightGreen * scaledIntensity) * brightnessBoost);
                    blue  = (int) Math.min(255.0F, (blue  + ModConfig.endFlashLightBlue * scaledIntensity) * brightnessBoost);
                }

                this.lightPixels.setPixelRGBA(block, sky,
                        (alpha << 24) | (blue << 16) | (green << 8) | red);
            }
        }
    }
}