package com.minhcraft.beyondbetatweaks.mixin.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minhcraft.beyondbetatweaks.interfaces.ILocalMobCapCalculator;
import com.minhcraft.beyondbetatweaks.interfaces.INaturalSpawnerSpawnState;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin {
    @Shadow @Final ServerLevel level;

    @WrapOperation(
            method = "tickChunks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/NaturalSpawner;createState(ILjava/lang/Iterable;Lnet/minecraft/world/level/NaturalSpawner$ChunkGetter;Lnet/minecraft/world/level/LocalMobCapCalculator;)Lnet/minecraft/world/level/NaturalSpawner$SpawnState;"
            )
    )
    private NaturalSpawner.SpawnState tickChunksNaturalSpawner(int spawnableChunkCount, Iterable<Entity> entities, NaturalSpawner.ChunkGetter chunkGetter, LocalMobCapCalculator calculator, Operation<NaturalSpawner.SpawnState> original) {
        ((ILocalMobCapCalculator) calculator).beyond_beta_tweaks$setDimension(this.level.dimension());

        NaturalSpawner.SpawnState spawnState = original.call(spawnableChunkCount, entities, chunkGetter, calculator);

        ((INaturalSpawnerSpawnState) spawnState).beyond_beta_tweaks$setDimension(this.level.dimension());

        return spawnState;
    }
}
