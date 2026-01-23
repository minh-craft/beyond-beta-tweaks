package com.minhcraft.mixin.betterdays_compat;

import betterdays.config.ConfigHandler;
import betterdays.time.Time;
import betterdays.time.TimeServiceManager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static betterdays.time.TimeServiceManager.service;

@Mixin(TimeServiceManager.class)
public abstract class TimeServiceManagerMixin {

    // Earliest allowed sleep time is set to just a bit before midnight
    @Unique
    private final static Time SLEEP_EARlIEST_ALLOWED_TIME = new Time(17500.0);

    // Sleep wakeup time is set to early sunrise
    @Unique
    private final static Time SLEEP_WAKEUP_TIME = new Time(23000.0);

    @Inject(
            method = "onSleepingCheckEvent",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private static void crt$overrideSleepingCheckTime(Level level, CallbackInfoReturnable<Boolean> cir) {
        if (service != null && (service.level.get()).equals(level)) {
            Time time = service.getDayTime().timeOfDay();
            if (ConfigHandler.Common.enableSleepFeature()
                    && time.compareTo(SLEEP_EARlIEST_ALLOWED_TIME) >= 0
                    && time.compareTo(SLEEP_WAKEUP_TIME) < 0
            ) {
                cir.setReturnValue(true);
                return;
            }
        }

        cir.setReturnValue(false);
    }
}
