package com.minhcraft.beyondbetatweaks.mixin.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldCarver.class)
public abstract class WorldCarverMixin {

    @Inject(
            method = "getCarveState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/Aquifer;computeSubstance(Lnet/minecraft/world/level/levelgen/DensityFunction$FunctionContext;D)Lnet/minecraft/world/level/block/state/BlockState;"),
            cancellable = true
    )
    private void beyond_beta_tweaks$disableCaveAquiferAboveYZero(CarvingContext context, CarverConfiguration config, BlockPos pos, Aquifer aquifer, CallbackInfoReturnable<BlockState> cir) {
        // Don't affect canyon world carvers
        if (((Object) this instanceof CanyonWorldCarver)) {
            return;
        }

        // cave based water aquifers can only exist below y=0
        if (pos.getY() >= 0) {
            cir.setReturnValue(Blocks.AIR.defaultBlockState());
            return;
        }
    }
}
