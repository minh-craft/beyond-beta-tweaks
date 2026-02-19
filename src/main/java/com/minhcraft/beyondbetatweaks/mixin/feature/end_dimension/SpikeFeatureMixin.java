package com.minhcraft.beyondbetatweaks.mixin.feature.end_dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpikeFeature.class)
public abstract class SpikeFeatureMixin {

    // Remove iron bars from obsidian spikes
    @Redirect(method = "placeSpike", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/SpikeFeature$EndSpike;isGuarded()Z"))
    private boolean beyond_beta_tweaks$isGuardedOverride(SpikeFeature.EndSpike instance) {
        return false;
    }

    // Remove end crystals from obsidian spikes
    @ModifyVariable(method = "placeSpike", at = @At("STORE"), ordinal = 0)
    private EndCrystal beyond_beta_tweaks$injected(EndCrystal x) {
        return null;
    }

    // Place fire on top of each spike
    @Inject(method = "placeSpike", at = @At("TAIL"))
    private void beyond_beta_tweaks$placeFireOnTop(ServerLevelAccessor level, RandomSource random, SpikeConfiguration config, SpikeFeature.EndSpike spike, CallbackInfo ci) {
        BlockPos firePos = new BlockPos(spike.getCenterX(), spike.getHeight(), spike.getCenterZ());
        level.setBlock(firePos, Blocks.FIRE.defaultBlockState(), 3);
    }
}
