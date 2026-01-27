package com.minhcraft.beyondbetatweaks.mixin.compat_betterdays;

import betterdays.time.Time;
import betterdays.time.TimeService;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TimeService.class)
public abstract class TimeServiceMixin {
    // Sleep wakeup time is set to early sunrise
    @Unique
    private final static double SLEEP_WAKEUP_TIME = 23000.0;

    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lbetterdays/time/Time;crossedMorning(Lbetterdays/time/Time;Lbetterdays/time/Time;)Z"),
            remap = false
    )
    private boolean beyond_beta_tweaks$overrideTimeCrossedMorningLogic(boolean crossedMorning, @Local(ordinal = 0) Time oldTime, @Local(ordinal = 2) Time time) {
        return Time.timeOfDay(time.longValue()) > SLEEP_WAKEUP_TIME || crossedMorning;
    }
}
