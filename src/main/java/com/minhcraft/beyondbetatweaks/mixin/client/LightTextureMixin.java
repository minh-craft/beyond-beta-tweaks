package com.minhcraft.beyondbetatweaks.mixin.client;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LightTexture.class)
public abstract class LightTextureMixin {

    // Override forceBrightLightmap lerp value, which is only used by the End dimension
    // This allows darkening or brightening the lightmap of the end
    @ModifyConstant(method = "updateLightTexture", constant = @Constant(floatValue = 0.25F))
    private float overrideForceBrightLightmapLerp(float lightmapLerp) {
        return lightmapLerp * ModConfig.endDimensionLightMapBrightnessModifier;
    }
}
