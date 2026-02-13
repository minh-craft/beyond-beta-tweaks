package com.minhcraft.beyondbetatweaks.mixin.feature.responsive_knockback;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// Responsive knockback code from https://github.com/Revvilo/responsive-knockback by @Revvilo
@Mixin(value = ServerChunkCache.class, priority = 1)
public abstract class ServerChunkCacheMixin {

    @WrapOperation(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkMap;tick()V"))
    private void beyond_beta_tweaks$removeCall(ChunkMap instance, Operation<Void> original) {
        BeyondBetaTweaks.TRACKER_TICK.set(() -> original.call(instance));
    }
}
