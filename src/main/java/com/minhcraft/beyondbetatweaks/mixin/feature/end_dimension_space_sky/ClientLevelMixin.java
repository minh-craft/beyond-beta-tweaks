package com.minhcraft.beyondbetatweaks.mixin.feature.end_dimension_space_sky;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    // set cloud color to white so that Cloud Layer's cloud color is the only thing affecting cloud color
    @Inject(method = "getCloudColor", at = @At("HEAD"), cancellable = true)
    private void beyond_beta_tweaks$overrideEndCloudColor(float tickDelta, CallbackInfoReturnable<Vec3> cir) {
        if (((Level)(Object)this).dimension() == Level.END) {
            cir.setReturnValue(new Vec3(1.0, 1.0, 1.0));
        }
    }
}
