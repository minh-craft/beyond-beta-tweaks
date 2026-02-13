package com.minhcraft.beyondbetatweaks.mixin.feature.responsive_knockback;

import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

// Responsive knockback code from https://github.com/Revvilo/responsive-knockback by @Revvilo
@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    public void beyond_beta_tweaks$runSyncDelayed(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        BeyondBetaTweaks.TRACKER_TICK.getAndSet(BeyondBetaTweaks.DO_NOTHING).run();
    }
}
