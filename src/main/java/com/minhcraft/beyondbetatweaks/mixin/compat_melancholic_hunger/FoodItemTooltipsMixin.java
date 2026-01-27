package com.minhcraft.beyondbetatweaks.mixin.compat_melancholic_hunger;

import antigers.melancholic_hunger.FoodItemTooltips;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(FoodItemTooltips.class)
public abstract class FoodItemTooltipsMixin {

    @WrapWithCondition(
            method = "appendTooltip",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 1)
    )
    private static <E> boolean beyond_beta_tweaks$disableFoodRegenerationSpeedTooltip(List instance, E e) {
        return !ModConfig.disableMelancholicHungerFoodRegenerationSpeedTooltip;
    }
}
