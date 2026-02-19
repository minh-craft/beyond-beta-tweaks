package com.minhcraft.beyondbetatweaks.mixin.feature.end_dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.EndGatewayFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndGatewayFeature.class)
public abstract class EndGatewayFeatureMixin {

    @Inject(method = "place", at = @At("TAIL"))
    private void beyond_beta_tweaks$replaceBedrockWithObsidian(FeaturePlaceContext<EndGatewayConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();

        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-1, -2, -1), origin.offset(1, 2, 1))) {
            // Skip the block directly below the portal
            if (pos.getX() == origin.getX() && pos.getY() == origin.getY() - 1 && pos.getZ() == origin.getZ()) {
                continue;
            }
            if (level.getBlockState(pos).is(Blocks.BEDROCK)) {
                level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
            }
        }
    }
}
