package com.minhcraft.beyondbetatweaks.mixin.world;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
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
    private static double beyond_beta_tweaks$modifyEndGatewayTeleportDistance(double constant) {
        return ModConfig.endGatewayTeleportDistance;
    }
}
