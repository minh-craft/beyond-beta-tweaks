package com.minhcraft.beyondbetatweaks.mixin.feature.moon_phase_affect_cloud_color;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import com.minhcraft.beyondbetatweaks.util.CloudColorHelper;
import mod.lwhrvw.cloud_layers.CloudLayers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CloudLayers.class)
public abstract class CloudLayersMixin {
    @WrapOperation(
            method = "renderClouds",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getCloudColor(F)Lnet/minecraft/world/phys/Vec3;")
    )
    private static Vec3 beyond_beta_tweaks$moonPhaseAffectCloudColorCloudLayers(ClientLevel instance, float partialTick, Operation<Vec3> original) {
        if (    !ModConfig.enableMoonPhaseAffectCloudColor
                || instance == null
                || instance.getRainLevel(partialTick) > 0
                || !(instance.effects() instanceof DimensionSpecialEffects.OverworldEffects)
                || instance.dimensionType().hasFixedTime()
        ) {
            return original.call(instance, partialTick);
        }
        float color = CloudColorHelper.getCloudColor(instance.getDayTime() % 24000, instance.getMoonPhase());
        return new Vec3(color, color, color);
    }
}
