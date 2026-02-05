package com.minhcraft.beyondbetatweaks.mixin.feature.piglin_rework;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.piglin.PiglinBruteAi;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PiglinBruteAi.class)
public abstract class PiglinBruteAiMixin {

    @Shadow
    private static Optional<? extends LivingEntity> getTargetIfWithinRange(AbstractPiglin piglinBrute, MemoryModuleType<? extends LivingEntity> memoryType) {
        return Optional.empty();
    }

    @Inject(
            method = "findNearestValidAttackTarget",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void beyond_beta_tweaks$adjustAttackTargets(AbstractPiglin piglinBrute, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
        Brain<PiglinBrute> brain = (Brain<PiglinBrute>) piglinBrute.getBrain();

        Optional<LivingEntity> optional = BehaviorUtils.getLivingEntityFromUUIDMemory(piglinBrute, MemoryModuleType.ANGRY_AT);
        if (optional.isPresent() && Sensor.isEntityAttackableIgnoringLineOfSight(piglinBrute, (LivingEntity)optional.get())) {
            cir.setReturnValue(optional);
            return;
        } else {
            if (brain.hasMemoryValue(MemoryModuleType.UNIVERSAL_ANGER)) {
                Optional<Player> optional2 = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
                if (optional2.isPresent()) {
                    cir.setReturnValue(optional2);
                    return;
                }
            }

            Optional<Mob> optional2 = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
            if (optional2.isPresent()) {
                cir.setReturnValue(optional2);
            } else {
                Optional<Player> optional3 = brain.getMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
                cir.setReturnValue(optional3.isPresent() && Sensor.isEntityAttackable(piglinBrute, (LivingEntity)optional3.get()) ? optional3 : Optional.empty());
                return;
            }

//            Optional<? extends LivingEntity> optional2 = getTargetIfWithinRange(piglinBrute, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
//            cir.setReturnValue(optional2.isPresent() ? optional2 : piglinBrute.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS));
//            return;

        }
        cir.cancel();
    }

}
