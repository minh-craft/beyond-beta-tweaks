package com.minhcraft.beyondbetatweaks.mixin.feature.animal_breeding;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.world.entity.animal.Fox$FoxBreedGoal")
public abstract class FoxBreedGoalMixin {

    // Configurable breeding cooldown time code adapted from https://github.com/antio789/customspeed by [@antio789](https://github.com/antio789)
    @ModifyConstant(
            method = "breed",
            constant = @Constant(intValue = 6000)
    )
    private int beyond_beta_tweaks$modifyBreedCooldown(int constant) {
        return ModConfig.animalBreedingCooldownInSeconds*20;
    }

    // Configurable baby animal grow up time code adapted from https://github.com/antio789/customspeed by [@antio789](https://github.com/antio789)
    @ModifyConstant(
            method = "breed",
            constant = @Constant(intValue = -24000)
    )
    private int beyond_beta_tweaks$modifyBabyFoxGrowUpTime(int constant) {
        return ModConfig.babyAnimalGrowUpTimeInSeconds*-20;
    }
}
