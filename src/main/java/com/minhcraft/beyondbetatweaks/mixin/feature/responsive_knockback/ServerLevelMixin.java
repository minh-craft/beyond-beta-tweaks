package com.minhcraft.beyondbetatweaks.mixin.feature.responsive_knockback;

import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

// Responsive knockback code adapted from https://github.com/Revvilo/responsive-knockback by @Revvilo
@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    // Moving tracker tick to this point as compared to the tail of the method seems to preserve responsive knockback behavior
    // while slightly reducing visual weirdness with arrows hitting entities and appearing to be stuck inside of them. it still happens, but it's less pronounced
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tickBlockEntities()V"))
    public void beyond_beta_tweaks$runSyncDelayed(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        BeyondBetaTweaks.TRACKER_TICK.getAndSet(BeyondBetaTweaks.DO_NOTHING).run();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void beyond_beta_tweaks$runSyncFallback(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        // If the early injection ran, this is a no-op since TRACKER_TICK was already reset to DO_NOTHING
        BeyondBetaTweaks.TRACKER_TICK.getAndSet(BeyondBetaTweaks.DO_NOTHING).run();
    }
}
