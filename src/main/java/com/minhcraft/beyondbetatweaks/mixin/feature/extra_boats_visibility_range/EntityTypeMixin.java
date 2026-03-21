package com.minhcraft.beyondbetatweaks.mixin.feature.extra_boats_visibility_range;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public class EntityTypeMixin {

    @Shadow
    @Final
    private int clientTrackingRange;

    // Fixes tracking range for https://github.com/Anxietie/Extra-Boats boats, which have a tracking range of 10 blocks rather than 10 chunks
    @Inject(method = "clientTrackingRange", at = @At("HEAD"), cancellable = true)
    private void beyond_beta_tweaks$fixExtraBoatsTrackingRange(CallbackInfoReturnable<Integer> cir) {
        if (this.clientTrackingRange < 10) {
            EntityType<?> self = (EntityType<?>) (Object) this;
            String id = BuiltInRegistries.ENTITY_TYPE.getKey(self).toString();
            if (id.startsWith("anx:")) { // Extra Boats MODID
                cir.setReturnValue(10);
            }
        }
    }
}