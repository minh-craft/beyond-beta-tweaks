package com.minhcraft.beyondbetatweaks.mixin.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Husk.class)
public abstract class HuskMixin extends Monster {

    protected HuskMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "isSunSensitive",
            at = @At("HEAD"),
            cancellable = true
    )
    // Make husks burn in daylight
    private void beyond_beta_tweaks$isSunSensitive(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
        cir.cancel();
    }

    @Inject(
            method = "convertsInWater",
            at = @At("HEAD"),
            cancellable = true
    )
    // Disable drowned conversion
    private void beyond_beta_tweaks$convertsInWater(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
        cir.cancel();
    }
}
