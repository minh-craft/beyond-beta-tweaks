package com.minhcraft.beyondbetatweaks.mixin.feature.less_wavy_banners;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BannerRenderer.class)
public abstract class BannerRendererMixin {

    @Redirect(
            method = "render(Lnet/minecraft/world/level/block/entity/BannerBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;cos(F)F")
    )
    private float beyond_beta_tweaks$modifyBannerWaviness(float value) {
        return Mth.cos(value) * ModConfig.bannerWaviness;
    }
}
