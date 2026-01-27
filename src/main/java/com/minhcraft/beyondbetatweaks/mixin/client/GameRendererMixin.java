package com.minhcraft.beyondbetatweaks.mixin.client;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    // Allow modifying how bright night vision is
    @Inject(method = "getNightVisionScale", at = @At("RETURN"), cancellable = true)
    private static void modifyNightVisionScale(LivingEntity livingEntity, float nanoTime, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(cir.getReturnValue() * ModConfig.nightVisionModifier);
    }
}
