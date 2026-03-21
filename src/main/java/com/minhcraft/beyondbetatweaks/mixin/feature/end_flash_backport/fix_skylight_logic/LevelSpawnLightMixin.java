package com.minhcraft.beyondbetatweaks.mixin.feature.end_flash_backport.fix_skylight_logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelReader.class)
public interface LevelSpawnLightMixin {

    // End skylight is enabled only for end flash lighting calculations.
    // It shouldn't affect monster spawn rates or anything else - so just return blocklight for calculations
    @Inject(method = "getMaxLocalRawBrightness(Lnet/minecraft/core/BlockPos;I)I",
            at = @At("HEAD"),
            cancellable = true)
    private void beyond_beta_tweaks$ignoreEndSkyLight(BlockPos pos, int skyDarken,
                                          CallbackInfoReturnable<Integer> cir) {
        if (!((Object) this instanceof Level level)) return;
        if (level.dimension() == Level.END) {
            int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
            cir.setReturnValue(blockLight);
        }
    }
}