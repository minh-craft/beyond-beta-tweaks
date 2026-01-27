package com.minhcraft.beyondbetatweaks.mixin.entity;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin {

    @Shadow @Final private MobEffect effect;

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;tickDownDuration()I")
    )
    // Backport 1.20.2's absorption change where the absorption effect is removed once all absorption hearts are gone.
    private void beyond_beta_tweaks$removeAbsorptionEffectIfNoAbsorptionHearts(LivingEntity entity, Runnable onExpirationRunnable, CallbackInfoReturnable<Boolean> cir) {
        if (this.effect == MobEffects.ABSORPTION && entity.getAbsorptionAmount() <= 0.0F)
        {
            entity.removeEffect(this.effect);
        }
    }
}
