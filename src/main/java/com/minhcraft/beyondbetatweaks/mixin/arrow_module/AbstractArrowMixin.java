package com.minhcraft.beyondbetatweaks.mixin.arrow_module;

import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

    // Always set crit arrow to false
    @ModifyArg(
            method = "setCritArrow",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setFlag(IZ)V"),
            index = 1
    )
    private static boolean beyond_beta_tweaks$setCritArrowFalse(boolean value) {
        return false;
    }
}
