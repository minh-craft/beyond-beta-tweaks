package com.minhcraft.beyondbetatweaks.mixin.feature.biome_alpha_tinting;

import com.minhcraft.beyondbetatweaks.interfaces.AlphaTintingFactorAccess;
import com.minhcraft.beyondbetatweaks.util.AlphaTintingFactorCache;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import me.jellysquid.mods.sodium.client.world.biome.BiomeSlice;
import me.jellysquid.mods.sodium.client.world.cloned.ChunkRenderContext;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldSlice.class, remap = false)
public abstract class WorldSliceMixin implements AlphaTintingFactorAccess {

    @Shadow
    @Final
    private BiomeSlice biomeSlice;

    @Unique
    private AlphaTintingFactorCache alphaTintingFactorCache;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void beyond_beta_tweaks$initializeAlphaTintingFactorCache(ClientLevel world, CallbackInfo ci) {
        this.alphaTintingFactorCache = new AlphaTintingFactorCache(this.biomeSlice);
    }

    @Inject(method = "copyData", at = @At("RETURN"))
    private void beyond_beta_tweaks$updateAlphaTintingFactorCache(ChunkRenderContext context, CallbackInfo ci) {
        this.alphaTintingFactorCache.update(
                context.getOrigin().minBlockX(),
                context.getOrigin().minBlockY(),
                context.getOrigin().minBlockZ()
        );
    }

    @Override
    public AlphaTintingFactorCache beyond_beta_tweaks$getOverlayFactorCache() {
        return this.alphaTintingFactorCache;
    }
}