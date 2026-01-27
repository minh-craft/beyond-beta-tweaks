package com.minhcraft.beyondbetatweaks.mixin.entity;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.world.entity.animal.goat.GoatAi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GoatAi.class)
public abstract class GoatAiMixin {

    @ModifyConstant(
            method = "<clinit>",
            constant = @Constant(intValue = 600, ordinal = 1)
    )
    private static int beyond_beta_tweaks$modifyMinimumWaitTimeBetweenRams(int constant) {
        return ModConfig.regularGoatMinimumRamWaitTimeSeconds * 20;
    }

    @ModifyConstant(
            method = "<clinit>",
            constant = @Constant(intValue = 6000)
    )
    private static int beyond_beta_tweaks$modifyMaxWaitTimeBetweenRams(int constant) {
        return ModConfig.regularGoatMaximumRamWaitTimeSeconds * 20;
    }

    @ModifyConstant(
            method = "<clinit>",
            constant = @Constant(intValue = 100)
    )
    private static int beyond_beta_tweaks$modifyScreamingGoatMinimumWaitTimeBetweenRams(int constant) {
        return ModConfig.screamingGoatMinimumRamWaitTimeSeconds * 20;
    }

    @ModifyConstant(
            method = "<clinit>",
            constant = @Constant(intValue = 300)
    )
    private static int beyond_beta_tweaks$modifyScreamingGoatMaxWaitTimeBetweenRams(int constant) {
        return ModConfig.screamingGoatMaximumRamWaitTimeSeconds * 20;
    }
}
