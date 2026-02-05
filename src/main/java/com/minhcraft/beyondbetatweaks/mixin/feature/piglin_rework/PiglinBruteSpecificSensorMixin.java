package com.minhcraft.beyondbetatweaks.mixin.feature.piglin_rework;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.PiglinBruteSpecificSensor;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mixin(PiglinBruteSpecificSensor.class)
public abstract class PiglinBruteSpecificSensorMixin {

    // Add nearest player not wearing gold to piglin brute memory
    @Inject(
            method = "requires",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beyond_beta_tweaks$requiresAddTrackingNearestPlayerNotWearingGold(CallbackInfoReturnable<Set<MemoryModuleType<?>>> cir) {
        cir.setReturnValue(
                ImmutableSet.of(
                        MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
                        MemoryModuleType.NEAREST_VISIBLE_NEMESIS,
                        MemoryModuleType.NEARBY_ADULT_PIGLINS,
                        MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD
                )
        );
    }

    // Add nearest player not wearing gold to piglin brute memory
    @Inject(
            method = "doTick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beyond_beta_tweaks$doTickAddTrackingNearestPlayerNotWearingGold(ServerLevel level, LivingEntity entity, CallbackInfo ci) {
        Brain<?> brain = entity.getBrain();
        List<AbstractPiglin> list = Lists.newArrayList();
        NearestVisibleLivingEntities nearestVisibleLivingEntities = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                .orElse(NearestVisibleLivingEntities.empty());
        Optional<Mob> nemesis = nearestVisibleLivingEntities.findClosest(
                        livingEntityx -> livingEntityx instanceof WitherSkeleton || livingEntityx instanceof WitherBoss
                )
                .map(Mob.class::cast);
        Optional<Player> nearestPlayerNotWearingGold = Optional.empty();


        for (LivingEntity livingEntity : brain.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).orElse(ImmutableList.of())) {
            if (livingEntity instanceof AbstractPiglin && ((AbstractPiglin)livingEntity).isAdult()) {
                list.add((AbstractPiglin)livingEntity);
            } else if (livingEntity instanceof Player player) {
                if (nearestPlayerNotWearingGold.isEmpty() && !PiglinAi.isWearingGold(player) && entity.canAttack(livingEntity)) {
                    nearestPlayerNotWearingGold = Optional.of(player);
                }
            }

        }

        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, nemesis);
        brain.setMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS, list);
        brain.setMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, nearestPlayerNotWearingGold);

        ci.cancel();
    }
}
