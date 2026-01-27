package com.minhcraft.beyondbetatweaks.mixin.entity;

import net.minecraft.world.entity.monster.Shulker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Shulker.class)
public abstract class ShulkerMixin {

    // Prevent shulker farms.
    // Code adapted from https://github.com/pajicadvance/misctweaks by @pajicadvance
    @Inject(
            method = "hitByShulkerBullet",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beyond_beta_tweaks$preventDuplication(CallbackInfo ci) {
        ci.cancel();
    }
}
