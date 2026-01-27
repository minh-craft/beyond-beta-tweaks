package com.minhcraft.mixin.compat_better_climbing;

import com.bawnorton.mixinsquared.TargetHandler;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = LivingEntity.class, priority = 1500)
public abstract class LivingEntityMixinSquared {

    @TargetHandler(
            mixin = "artemis.better_climbing.mixin.LivingEntityMixin",
            name = "better_climbing_modifyVerticalMovementWhenClimbing"
    )
    @ModifyConstant(
            method = "@MixinSquared:Handler",
            constant = @Constant(doubleValue = -0.4)
    )
    private double crt$modifyMaximumDownwardClimbingSpeed(double constant) {
        return -0.3;
    }

    @TargetHandler(
            mixin = "artemis.better_climbing.mixin.LivingEntityMixin",
            name = "better_climbing_allowJumpingInLadderAndSpeedUpClimbing"
    )
    @ModifyConstant(
            method = "@MixinSquared:Handler",
            constant = @Constant(doubleValue = 1.25)
    )
    private double crt$modifyMaximumUpwardClimbingSpeedModifer(double constant) {
        return 1.08;
    }
}
