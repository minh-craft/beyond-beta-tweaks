package com.minhcraft.beyondbetatweaks.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Hoglin.class)
public abstract class HoglinMixin {
    /**
     * Prevents hoglins from spawning as babies.
     * Hoglins will spawn if this function returns a value less than 0.2F
     */
    @ModifyExpressionValue(
            method = "finalizeSpawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextFloat()F"
            )
    )
    private float beyond_beta_tweaks$disableBabyHoglinOdds(float nextFloat)
    {
        return 1.0F;
    }
}
