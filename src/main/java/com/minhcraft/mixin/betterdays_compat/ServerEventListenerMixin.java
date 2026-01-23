package com.minhcraft.mixin.betterdays_compat;

import betterdays.event.ServerEventListener;
import betterdays.message.BetterDaysMessages;
import betterdays.time.TimeServiceManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerEventListener.class)
public abstract class ServerEventListenerMixin {

    @WrapOperation(
            method = "setup",
            at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/event/Event;register(Ljava/lang/Object;)V", ordinal = 1),
            remap = false
    )
    private static <T> void crt$sleepingCheckReturnsFailInsteadOfPass(Event instance, T t, Operation<Void> original) {
        EntitySleepEvents.ALLOW_SLEEP_TIME.register((player, sleepingPos, vanillaResult) -> {
            BetterDaysMessages.onSleepingCheckEvent(player);
            return TimeServiceManager.onSleepingCheckEvent(player.level()) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        });
    }
}
