package com.minhcraft.beyondbetatweaks.mixin.entity;

import net.minecraft.world.entity.projectile.EyeOfEnder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EyeOfEnder.class)
public abstract class EyeOfEnderMixin {

    @Shadow private boolean surviveAfterDeath;

    // Disable eye of ender destruction after use
    @Inject(
            method = "signalTo",
            at = @At("RETURN")
    )
    private void beyond_beta_tweaks$disableDestructionAfterUse(CallbackInfo ci) {
        this.surviveAfterDeath = true;
    }
}
