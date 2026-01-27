package com.minhcraft.beyondbetatweaks.mixin.entity;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public abstract class MobMixin {

    /**
     * Prevents custom mob death loot (equipment) from being spawned.
     * Code taken from https://github.com/Nostalgica-Reverie/Nostalgic-Tweaks
     */
    @WrapWithCondition(
            method = "dropCustomDeathLoot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Mob;spawnAtLocation(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"
            )
    )
    private boolean beyond_beta_tweaks$disableDropCustomDeathLoot(Mob mob, ItemStack itemStack)
    {
        boolean witherSkeleton = mob.getType() == EntityType.WITHER_SKELETON;
        boolean piglin = mob.getType() == EntityType.PIGLIN;
        boolean piglinBrute = mob.getType() == EntityType.PIGLIN_BRUTE;
        boolean zombifiedPiglin = mob.getType() == EntityType.ZOMBIFIED_PIGLIN;

        return !witherSkeleton && !piglin && !piglinBrute && !zombifiedPiglin;
    }
}
