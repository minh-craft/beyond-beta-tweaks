package com.minhcraft.beyondbetatweaks.mixin.entity;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.world.entity.animal.goat.Goat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Goat.class)
public abstract class GoatMixin {

    @ModifyConstant(
            method = "ageBoundaryReached",
            constant = @Constant(doubleValue = 2.0)
    )
    private double beyond_beta_tweaks$modifyRamDamage(double constant) {
        return 1.0;
    }

    @ModifyConstant(
            method = "finalizeSpawn",
            constant = @Constant(doubleValue = 0.02)
    )
    private double beyond_beta_tweaks$modifyScreamingGoatChance(double constant) {

        return ModConfig.screamingGoatChance;
    }
}
