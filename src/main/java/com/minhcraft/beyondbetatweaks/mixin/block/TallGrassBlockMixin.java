package com.minhcraft.beyondbetatweaks.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TallGrassBlock.class)
public abstract class TallGrassBlockMixin {

    // Disable bonemealing short grass into tall grass, still allow fern into tall fern
    // performBonemeal needs to be overriden because bonemealing grass blocks bypasses the checks for isValidBonemealTarget

    @Inject(
            method = "isValidBonemealTarget",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beyond_beta_tweaks$setValidBonemealTargetToFalse(LevelReader level, BlockPos pos, BlockState state, boolean isClient, CallbackInfoReturnable<Boolean> cir) {
        if (!state.is(Blocks.FERN)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "isBonemealSuccess",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beyond_beta_tweaks$setBonemealSuccessToFalse(Level level, RandomSource random, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!state.is(Blocks.FERN)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "performBonemeal",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beyond_beta_tweaks$disablePerformBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (!state.is(Blocks.FERN)) {
            ci.cancel();
        }
    }
}
