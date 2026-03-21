package com.minhcraft.beyondbetatweaks.mixin.feature.cave_biomes_no_lava;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

    @Inject(method = "getLightEmission", at = @At("HEAD"), cancellable = true)
    private void beyond_beta_tweaks$brighterUnderwaterGlowLichen(CallbackInfoReturnable<Integer> cir) {
        BlockState state = (BlockState) (Object) this;
        if (state.is(Blocks.GLOW_LICHEN)) {
            if (state.getValue(BlockStateProperties.WATERLOGGED)) {
                cir.setReturnValue(ModConfig.glowLichenUnderwaterLight);
            } else {
                cir.setReturnValue(ModConfig.glowLichenAirExposedLight);
            }
        }
    }
}