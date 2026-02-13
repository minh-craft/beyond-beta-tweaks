package com.minhcraft.beyondbetatweaks.mixin.feature.respawning_animals;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import mod.adrenix.nostalgic.helper.gameplay.AnimalSpawnHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalSpawnHelper.class)
public abstract class AnimalSpawnHelperMixin {

    @Inject(
            method = "tickChunk",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void test(LevelChunk chunk, ServerLevel level, boolean spawnFriendlies, CallbackInfo ci) {
        if (level.getGameTime() % ModConfig.oldAnimalRespawnTickInterval != 0) {
            ci.cancel();
        }
    }
}
