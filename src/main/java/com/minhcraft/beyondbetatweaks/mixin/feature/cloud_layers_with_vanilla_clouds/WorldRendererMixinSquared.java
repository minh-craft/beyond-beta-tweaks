package com.minhcraft.beyondbetatweaks.mixin.feature.cloud_layers_with_vanilla_clouds;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.lwhrvw.cloud_layers.CloudLayers;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 1500)
public abstract class WorldRendererMixinSquared {

    @TargetHandler(
            mixin = "mod.lwhrvw.cloud_layers.mixin.WorldRendererMixin",
            name = "renderClouds"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beyond_beta_tweaks$alsoRenderVanillaCloudsWithCloudLayersClouds(LevelRenderer instance, PoseStack matrices, Matrix4f matrix4f2, float tickDelta, double cameraX, double cameraY, double cameraZ, Operation<Void> original, CallbackInfo ci) {
        if (ModConfig.allowVanillaCloudsToCoexistWithCloudLayersClouds) {
            // always call vanilla cloud rendering
            original.call(instance, matrices, matrix4f2, tickDelta, cameraX, cameraY, cameraZ);
            // also render cloud layers clouds if enabled
            if (CloudLayers.CONFIG.enableMod && CloudLayers.isFancyEnabled()) {
                CloudLayers.renderClouds(instance, matrices, null, matrix4f2, tickDelta, cameraX, cameraY, cameraZ);
            }
            ci.cancel();
        }
    }
}
