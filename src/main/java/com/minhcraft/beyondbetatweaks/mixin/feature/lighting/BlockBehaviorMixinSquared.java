package com.minhcraft.beyondbetatweaks.mixin.feature.lighting;

import com.bawnorton.mixinsquared.TargetHandler;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = BlockBehaviour.class, priority = 1500)
public abstract class BlockBehaviorMixinSquared {

    @TargetHandler(
            mixin = "mod.adrenix.nostalgic.mixin.tweak.candy.world_lighting.BlockBehaviourMixin",
            name = "nt_world_lighting$modifyShadeDarkness"
    )
    @ModifyConstant(
            method = "@MixinSquared:Handler",
            constant = @Constant(floatValue = 0.0F)
    )
    private float beyond_beta_tweaks$modifyMinimumAmbientOcclusionShadeDarkness(float constant) {
        return ModConfig.oldSmoothLightingMinimumShadeDarkness;
    }
}
