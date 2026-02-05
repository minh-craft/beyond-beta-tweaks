package com.minhcraft.beyondbetatweaks.mixin.feature.piglin_rework;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ChestBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChestBoat.class)
public abstract class ChestBoatMixin {

    @WrapWithCondition(
            method = "interact",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;angerNearbyPiglins(Lnet/minecraft/world/entity/player/Player;Z)V")
    )
    private boolean beyond_beta_tweaks$interactDisablePiglinAnger(Player player, boolean angerOnlyIfCanSee) {
        return false;
    }

    @WrapWithCondition(
            method = "openCustomInventoryScreen",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;angerNearbyPiglins(Lnet/minecraft/world/entity/player/Player;Z)V")
    )
    private boolean beyond_beta_tweaks$openInventoryDisablePiglinAnger(Player player, boolean angerOnlyIfCanSee) {
        return false;
    }
}
