package com.minhcraft.beyondbetatweaks.mixin.entity;

import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Creeper.class)
public abstract class CreeperMixin {

    @Inject(
            method = "canDropMobsSkull",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beyond_beta_tweaks$disableChargedCreeperDroppingSkulls(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
