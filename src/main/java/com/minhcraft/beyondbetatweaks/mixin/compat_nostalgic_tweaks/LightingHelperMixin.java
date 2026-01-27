package com.minhcraft.beyondbetatweaks.mixin.compat_nostalgic_tweaks;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import mod.adrenix.nostalgic.helper.candy.light.LightingHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LightingHelper.class)
public class LightingHelperMixin {

    // Change maximum light level that can be subtracted from lightmap
    // Lower value makes nighttime brighter
    // Affects Nostalgic Tweak's round robin lighting
    @ModifyConstant(method = "getCombinedLight", constant = @Constant(intValue = 11), remap = false)
    private static int beyond_beta_tweaks$overrideMaximumLightLevelSubtraction(int constant) {
        return ModConfig.roundRobinMaximumDeductedLightLevel;
    }
}
