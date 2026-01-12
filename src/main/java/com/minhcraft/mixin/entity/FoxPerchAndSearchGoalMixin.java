package com.minhcraft.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.world.entity.animal.Fox$PerchAndSearchGoal")
public abstract class FoxPerchAndSearchGoalMixin {

    @ModifyConstant(
            method = "resetLook",
            constant = @Constant(intValue = 80)
    )
    private int crt$modifyBaseLookTime(int constant) {
        return 400;
    }
}
