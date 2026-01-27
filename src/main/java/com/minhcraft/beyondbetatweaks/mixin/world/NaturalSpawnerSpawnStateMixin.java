package com.minhcraft.beyondbetatweaks.mixin.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import com.minhcraft.beyondbetatweaks.interfaces.INaturalSpawnerSpawnState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NaturalSpawner.SpawnState.class)
public abstract class NaturalSpawnerSpawnStateMixin implements INaturalSpawnerSpawnState {
    @Unique
    private ResourceKey<Level> dimension = null;

    @WrapOperation(
            method = "canSpawnForCategory",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/MobCategory;getMaxInstancesPerChunk()I"
            )
    )
    private int canSpawnForCategoryGetMaxInstancesPerChunk(MobCategory instance, Operation<Integer> original) {
        if (instance == MobCategory.MONSTER) {
            if (this.dimension == Level.OVERWORLD) {
                return ModConfig.monsterMobCapOverworld;
            } else if (this.dimension == Level.NETHER) {
                return ModConfig.monsterMobCapNether;
            } else if (this.dimension == Level.END) {
                return ModConfig.monsterMobCapTheEnd;
            }
        }

        return original.call(instance);
    }

    @Override
    public void beyond_beta_tweaks$setDimension(ResourceKey<Level> dimension) {
        this.dimension = dimension;
    }
}
