package com.minhcraft.beyondbetatweaks.entity;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Giant;

public class GiantAttackGoal extends MeleeAttackGoal {
    // Copied from ZombieAttackGoal

    private final Giant giant;
    private int raiseArmTicks;

    public GiantAttackGoal(Giant giant, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(giant, speedModifier, followingTargetEvenIfNotSeen);
        this.giant = giant;
    }

    @Override
    public void start() {
        super.start();
        this.raiseArmTicks = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.giant.setAggressive(false);
    }

    @Override
    public void tick() {
        super.tick();
        this.raiseArmTicks++;
        if (this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < this.getAttackInterval() / 2) {
            this.giant.setAggressive(true);
        } else {
            this.giant.setAggressive(false);
        }
    }

    @Override
    protected double getAttackReachSqr(LivingEntity attackTarget) {
        return (this.mob.getBbWidth()+ ModConfig.giantBoundingBoxAttackRangeIncrease)* 2.0F
                * ((this.mob.getBbWidth()+ModConfig.giantBoundingBoxAttackRangeIncrease) * 2.0F)
                + attackTarget.getBbWidth();
    }
}
