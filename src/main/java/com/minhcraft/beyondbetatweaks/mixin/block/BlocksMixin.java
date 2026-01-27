package com.minhcraft.beyondbetatweaks.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public abstract class BlocksMixin {

    // Add light level to Amethyst Block
    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;requiresCorrectToolForDrops()Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;"),
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=amethyst_block"),
                    to = @At(value = "CONSTANT", args = "stringValue=budding_amethyst")
            )
    )
    private static BlockBehaviour.Properties beyond_beta_tweaks$addLightLevel(BlockBehaviour.Properties original) {
        return original.lightLevel((blockStateX) -> 15);
    }
}
