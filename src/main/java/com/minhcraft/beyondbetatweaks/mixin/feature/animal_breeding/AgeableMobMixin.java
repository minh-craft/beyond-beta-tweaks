package com.minhcraft.beyondbetatweaks.mixin.feature.animal_breeding;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.world.entity.AgeableMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AgeableMob.class)
public abstract class AgeableMobMixin {

    // Configurable baby animal grow up time code adapted from https://github.com/antio789/customspeed by [@antio789](https://github.com/antio789)
    @ModifyConstant(
            method = "setBaby",
            constant = @Constant(intValue = -24000)
    )
    private int beyond_beta_tweaks$modifyBabyGrowUpTime(int constant) {

        return ModConfig.babyAnimalGrowUpTimeInSeconds*-20;
    }
}
