package com.minhcraft.mixin.world;

import com.minhcraft.config.ModConfig;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(TheEndGatewayBlockEntity.class)
public abstract class TheEndGatewayBlockEntityMixin {

    @ModifyConstant(
            method = "findExitPortalXZPosTentative",
            constant = @Constant(doubleValue = 1024.0)
    )
    private static double crt$modifyEndGatewayTeleportDistance(double constant) {
        return ModConfig.endGatewayTeleportDistance;
    }
}
