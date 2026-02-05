package com.minhcraft.beyondbetatweaks.mixin.feature.piglin_rework;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PiglinAi.class)
public abstract class PiglinAiMixin {

    @Shadow
    private static void setAngerTargetToNearestTargetablePlayerIfFound(AbstractPiglin piglin, LivingEntity currentTarget) {
    }

    @Shadow
    protected static void setAngerTarget(AbstractPiglin piglin, LivingEntity target) {
    }

    @Shadow
    protected static boolean isIdle(AbstractPiglin piglin) {
        return false;
    }

    @Inject(
            method = "angerNearbyPiglins",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void beyond_beta_tweaks$angerPiglinBrutesToo(Player player, boolean angerOnlyIfCanSee, CallbackInfo ci) {
        List<AbstractPiglin> list = player.level().getEntitiesOfClass(AbstractPiglin.class, player.getBoundingBox().inflate(16.0));
        list.stream().filter(PiglinAiMixin::isIdle).filter(piglin -> !angerOnlyIfCanSee || BehaviorUtils.canSee(piglin, player)).forEach(piglin -> {
            if (piglin.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                setAngerTargetToNearestTargetablePlayerIfFound(piglin, player);
            } else {
                setAngerTarget(piglin, player);
            }
        });
        ci.cancel();
    }
}
