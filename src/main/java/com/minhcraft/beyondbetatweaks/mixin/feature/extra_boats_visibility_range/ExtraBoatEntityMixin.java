package com.minhcraft.beyondbetatweaks.mixin.feature.extra_boats_visibility_range;

import com.minhcraft.beyondbetatweaks.config.ModConfig;

import com.mod.entity.ExtraBoatEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExtraBoatEntity.class)
public abstract class ExtraBoatEntityMixin {

    @Shadow
    private float yawVelocity;

    @Inject(method = "updatePaddles", at = @At("TAIL"))
    private void beyond_beta_tweaks$clampTurnSpeed(CallbackInfo ci) {
        this.yawVelocity = Mth.clamp(this.yawVelocity, -ModConfig.lavaBoatMaxYawVelocity, ModConfig.lavaBoatMaxYawVelocity);
    }

    @Inject(method = "updateVelocity", at = @At("TAIL"))
    private void beyond_beta_tweaks$clampMaxVelocity(CallbackInfo ci) {
        ExtraBoatEntity self = (ExtraBoatEntity) (Object) this;
        Vec3 vel = self.getDeltaMovement();
        double horizontalSpeed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);

        if (horizontalSpeed > ModConfig.lavaBoatMaxHorizontalSpeed) {
            double scale = ModConfig.lavaBoatMaxHorizontalSpeed / horizontalSpeed;
            self.setDeltaMovement(vel.x * scale, vel.y, vel.z * scale);
        }
    }
}
