package com.minhcraft.beyondbetatweaks.mixin.entity;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonMixin extends Monster {
    protected AbstractSkeletonMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "registerGoals",
            at = @At("HEAD")
    )
    private void beyond_beta_tweaks$registerGoals(CallbackInfo ci) {
        this.goalSelector.addGoal(1, new FloatGoal(this));
    }

    @Redirect(
            method = "performRangedAttack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;shoot(DDDFF)V")
    )
    private void beyond_beta_tweaks$performRangedAttack(AbstractArrow instance, double x, double y, double z, float velocity, float inaccuracy) {
        instance.shoot(x, y, z, velocity - ModConfig.skeletonArrowVelocityDecrease, inaccuracy);
    }
}
