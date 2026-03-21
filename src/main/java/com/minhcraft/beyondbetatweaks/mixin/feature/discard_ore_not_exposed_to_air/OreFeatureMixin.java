package com.minhcraft.beyondbetatweaks.mixin.feature.discard_ore_not_exposed_to_air;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.level.levelgen.feature.Feature.isAdjacentToAir;

@Mixin(OreFeature.class)
public abstract class OreFeatureMixin {

    // Handle negative discard_chance_on_air_exposure
    // This means, if the ore is NOT exposed to air, there is a chance to discard the ore
    // compared to the normal behavior of there being a chance to discard the ore if the ore IS exposed to air
    @Inject(
            method = "canPlaceOre",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void beyond_beta_tweaks$handleNegativeDiscardChanceOnAirExposure(
            BlockState blockState,
            java.util.function.Function<BlockPos, BlockState> adjacentStateAccessor,
            RandomSource random,
            OreConfiguration config,
            OreConfiguration.TargetBlockState targetBlockState,
            BlockPos.MutableBlockPos mutablePos,
            CallbackInfoReturnable<Boolean> cir
    ) {
        float discardChance = config.discardChanceOnAirExposure;

        if (discardChance < 0.0F) {
            // Check if the target rule test matches
            if (!targetBlockState.target.test(blockState, random)) {
                cir.setReturnValue(false);
                return;
            }

            boolean exposedToAir = isAdjacentToAir(adjacentStateAccessor, mutablePos);

            // Chance to discard if NOT exposed to air
            if (!exposedToAir) {
                if (random.nextFloat() < Math.abs(discardChance)) {
                    cir.setReturnValue(false);
                    return;
                }
            }

            cir.setReturnValue(true);
        }
        // If discardChance >= 0, let vanilla handle it
    }
}
