package com.minhcraft.beyondbetatweaks.mixin.client;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.client.model.HoglinModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HoglinModel.class)
public abstract class HoglinModelMixin {

    @Shadow
    private ModelPart head;

    // Custom model for hoglin in Beyond Beta modpack causes baby hoglin to have adult sized head - this fixes that.
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/Mob;FFFFF)V",
            at = @At("TAIL"))
    private void scaleBabyHead(@Coerce Object entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (ModConfig.shrinkBabyHoglinHead && ((Hoglin)entity).isBaby()) {
            // Vanilla baby head scaling
            this.head.xScale = 0.5F;
            this.head.yScale = 0.5F;
            this.head.zScale = 0.5F;
        } else {
            // Reset to normal for adults
            this.head.xScale = 1.0F;
            this.head.yScale = 1.0F;
            this.head.zScale = 1.0F;
        }
    }
}
