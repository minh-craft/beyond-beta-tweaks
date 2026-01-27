package com.minhcraft.beyondbetatweaks.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobCategory.class)
public abstract class MobCategoryMixin {

    @Shadow @Final private String name;

    // Overrides the number of ambient water mobs that can spawn in a single chunk.
    // Default value is 20
    // Code adapted from https://github.com/Nostalgica-Reverie/Nostalgic-Tweaks by @Adrenix
    @ModifyReturnValue(
            method = "getMaxInstancesPerChunk",
            at = @At("RETURN")
    )
    private int beyond_beta_tweaks$modifyWaterAmbientMobCap(int maxInstancesPerChunk)
    {
        if (this.name.equals(MobCategory.WATER_AMBIENT.getName())) {
            return ModConfig.ambientWaterMobCap;
        }
        return maxInstancesPerChunk;
    }
}
