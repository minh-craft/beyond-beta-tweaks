package com.minhcraft.beyondbetatweaks.mixin.block;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PointedDripstoneBlock.class)
public abstract class PointedDripstoneBlockMixin {

    @WrapWithCondition(
            method = "spawnDripParticle(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/Fluid;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V")
    )
    private static boolean beyond_beta_tweaks$disableLavaDripParticle(Level instance, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return particleData != ParticleTypes.DRIPPING_DRIPSTONE_LAVA;
    }

    @Inject(
            method = "canFillCauldron",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void beyond_beta_tweaks$removeLavaAsFillCauldronFluid(Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(fluid == Fluids.WATER);
    }
}
