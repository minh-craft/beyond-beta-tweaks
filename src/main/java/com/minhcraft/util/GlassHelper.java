package com.minhcraft.util;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;

public class GlassHelper {

    public static boolean isGlassPaneOrBlock(ItemStack stack) {
        if (stack.is(Items.GLASS_PANE)) {
            return true;
        }
        if (stack.getItem() instanceof BlockItem blockItem) {
            var block = blockItem.getBlock();
            if (block instanceof IronBarsBlock && !block.defaultBlockState().is(Blocks.IRON_BARS) && !block.defaultBlockState().is(Blocks.IRON_BARS)) {
                return true;
            }
            return block instanceof AbstractGlassBlock;
        }
        return false;
    }
}
