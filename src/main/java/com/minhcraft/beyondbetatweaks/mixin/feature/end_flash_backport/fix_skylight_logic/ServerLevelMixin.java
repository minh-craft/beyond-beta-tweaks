package com.minhcraft.beyondbetatweaks.mixin.feature.end_flash_backport.fix_skylight_logic;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    // Make HasSkyLight check for the dimension not having fixed time as well, since the End has skylight now
    @WrapOperation(
            method = "advanceWeatherCycle",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;hasSkyLight()Z")
    )
    private boolean beyond_beta_tweaks$modifyHasSkyLightLogic(DimensionType instance, Operation<Boolean> original) {
        return original.call(instance) && !instance.hasFixedTime();
    }
}
