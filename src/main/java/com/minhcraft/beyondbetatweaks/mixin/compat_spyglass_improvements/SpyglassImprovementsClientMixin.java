package com.minhcraft.beyondbetatweaks.mixin.compat_spyglass_improvements;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import me.juancarloscp52.spyglass_improvements.client.SpyglassImprovementsClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SpyglassImprovementsClient.class)
public abstract class SpyglassImprovementsClientMixin {
    @ModifyConstant(
            method = "<clinit>",
            constant = @Constant(floatValue = 0.1F)
    )
    private static float beyond_beta_tweaks$modifyDefaultZoom(float constant) {
        return ModConfig.spyglassDefaultZoom;
    }
}
