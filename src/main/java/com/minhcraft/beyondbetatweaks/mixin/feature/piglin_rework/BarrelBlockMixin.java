package com.minhcraft.beyondbetatweaks.mixin.feature.piglin_rework;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BarrelBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BarrelBlock.class)
public abstract class BarrelBlockMixin {

    @WrapWithCondition(
            method = "use",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;angerNearbyPiglins(Lnet/minecraft/world/entity/player/Player;Z)V")
    )
    private boolean beyond_beta_tweaks$disablePiglinAnger(Player player, boolean angerOnlyIfCanSee) {
        return false;
    }
}
