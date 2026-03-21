package com.minhcraft.beyondbetatweaks.mixin.feature.bedrock_egg;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EndPodiumFeature.class)
public abstract class EndPodiumFeatureMixin {

    @Shadow @Final private boolean active;

    // only place torches if the podium is active
    // fixes a bug where torches from the inactive podium get knocked off when the active podium gets placed and fall through the portal down to the overworld at 0,0
    @WrapWithCondition(
            method = "place",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/feature/EndPodiumFeature;setBlock(Lnet/minecraft/world/level/LevelWriter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V")
    )
    private boolean beyond_beta_tweaks$skipInactivePodiumTorchPlacement(EndPodiumFeature instance, LevelWriter levelWriter, BlockPos blockPos, BlockState blockState) {
        if (blockState.is(Blocks.WALL_TORCH)) {
            return this.active;
        }
        return true;
    }
}
