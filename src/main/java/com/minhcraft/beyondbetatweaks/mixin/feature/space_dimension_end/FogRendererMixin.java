package com.minhcraft.beyondbetatweaks.mixin.feature.space_dimension_end;


import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.level.Level;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @Unique private static final float END_FOG_R;
    @Unique private static final float END_FOG_G;
    @Unique private static final float END_FOG_B;

    static {
        int fogColor = Integer.parseInt(ModConfig.endDimensionFogColor.replace("#", ""), 16);
        END_FOG_R = ((fogColor >> 16) & 0xFF) / 255.0f;
        END_FOG_G = ((fogColor >> 8) & 0xFF) / 255.0f;
        END_FOG_B = (fogColor & 0xFF) / 255.0f;
    }

    @Shadow
    private static float fogRed;
    @Shadow
    private static float fogGreen;
    @Shadow
    private static float fogBlue;

    @Inject(method = "setupColor", at = @At("TAIL"))
    private static void beyond_beta_tweaks$overrideEndFogColor(Camera camera, float partialTick,
                                                net.minecraft.client.multiplayer.ClientLevel level,
                                                int renderDistance, float darkenWorldAmount,
                                                CallbackInfo ci) {
        if (level != null && level.dimension() == Level.END) {
            fogRed = END_FOG_R;
            fogGreen = END_FOG_G;
            fogBlue = END_FOG_B;
        }
    }
}
