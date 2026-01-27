package com.minhcraft.beyondbetatweaks.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.monster.Silverfish$SilverfishMergeWithStoneGoal")
public abstract class SilverfishMergeWithStoneGoalMixin {

    // Disable silverfish infest stone behavior
    @Inject(
            method = "canUse",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beyond_beta_tweaks$disableSilverfishMergeWithStoneGoal(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
        cir.cancel();
    }
}
