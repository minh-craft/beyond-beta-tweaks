package com.minhcraft.beyondbetatweaks.mixin.entity;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.CaveSpider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.monster.Spider$SpiderAttackGoal")
public abstract class SpiderAttackGoalMixin extends MeleeAttackGoal {
//    @Shadow protected abstract double getAttackReachSqr(LivingEntity attackTarget);

    public SpiderAttackGoalMixin(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
    }

    @Inject(
            method = "getAttackReachSqr",
            at = @At("RETURN"),
            cancellable = true
    )
    // Change formula of spider attack range to match other mobs
    // Original was 4.0 + attackTarget.getBbWidth() - which applied to both spiders and cave spiders
    // Default attack range without range increase:
    // Spider width = 1.4 -> 1.4*2 * 1.4*2 = 7.84 + attackTarget.getBbWidth()
    // Cave spider width = 0.7 -> 0.7*2 * 0.7*2 = 1.96 + attackTarget.getBbWidth()
    private void beyond_beta_tweaks$injectGetAttackReachSqr(LivingEntity attackTarget, CallbackInfoReturnable<Double> cir) {
        double attackRangeBbIncrease = (this.mob instanceof CaveSpider ? ModConfig.caveSpiderBoundingBoxAttackRangeIncrease : ModConfig.spiderBoundingBoxAttackRangeIncrease);
        cir.setReturnValue(
                (this.mob.getBbWidth() + attackRangeBbIncrease) * 2.0D
                        * (((this.mob.getBbWidth() + attackRangeBbIncrease) * 2.0D))
                        + attackTarget.getBbWidth()
        );
    }
}
