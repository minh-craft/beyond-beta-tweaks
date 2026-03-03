package com.minhcraft.beyondbetatweaks.mixin.feature.bedrock_egg;

import com.minhcraft.beyondbetatweaks.world.BedrockEggBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "fudgeSpawnLocation", at = @At("RETURN"))
    private void bedrockegg$avoidEggSpawn(ServerLevel level, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        double px = self.getX();
        double pz = self.getZ();

        // Check if the player is within the egg's footprint
        int eggRadius = BedrockEggBuilder.SCAN_RADIUS + 1; // +1 for safety margin
        if (Math.abs(px) > eggRadius || Math.abs(pz) > eggRadius) {
            return; // Already outside the egg area
        }

        // Search outward for a safe spot outside the egg
        for (int radius = eggRadius; radius <= 32; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) continue;

                    int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, dx, dz);

                    BlockPos belowPos = new BlockPos(dx, y - 1, dz);
                    BlockPos feetPos = new BlockPos(dx, y, dz);
                    BlockPos headPos = new BlockPos(dx, y + 1, dz);

                    if (!level.getBlockState(belowPos).is(Blocks.BEDROCK)
                            && level.getBlockState(belowPos).isSolid()
                            && !level.getBlockState(feetPos).isSolid()
                            && !level.getBlockState(headPos).isSolid()) {
                        self.moveTo(dx + 0.5, y, dz + 0.5, 0.0F, 0.0F);
                        return;
                    }
                }
            }
        }
    }
}
