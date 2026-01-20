package com.minhcraft.mixin.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;

@Mixin(ShovelItem.class)
public abstract class ShovelItemMixin {

    @Unique
    private static final Map<Block, BlockState> FLATTENABLES = Maps.<Block, BlockState>newHashMap(
            new ImmutableMap.Builder()
                    .put(Blocks.GRASS_BLOCK, Blocks.COARSE_DIRT.defaultBlockState())
                    .put(Blocks.DIRT, Blocks.COARSE_DIRT.defaultBlockState())
                    .put(Blocks.PODZOL, Blocks.COARSE_DIRT.defaultBlockState())
                    .put(Blocks.MYCELIUM, Blocks.COARSE_DIRT.defaultBlockState())
                    .put(Blocks.ROOTED_DIRT, Blocks.COARSE_DIRT.defaultBlockState())
                    .build()
    );

    // Shovel converts dirt/grass into coarse dirt instead of path blocks
    @ModifyVariable(method = "useOn", at = @At("STORE"), ordinal = 1)
    private BlockState crt$overrideFlattenables(BlockState x, @Local(ordinal = 0) BlockState blockState) {
        return FLATTENABLES.get(blockState.getBlock());
    }
}
