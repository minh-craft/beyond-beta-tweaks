package com.minhcraft.beyondbetatweaks.mixin.feature.bedrock_egg;

import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import com.minhcraft.beyondbetatweaks.world.bedrock_egg.BedrockEggBuilder;
import com.minhcraft.beyondbetatweaks.world.bedrock_egg.BedrockEggState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndPortalBlock.class)
public abstract class EndPortalBlockMixin {

    // The very first time the end exit portal is used in the world:
    // 1) open the bedrock egg
    // 2) fast-forward time to sunrise, if in a single player context
    // 3) play a global end portal opening sound
    @Inject(method = "entityInside", at = @At("HEAD"))
    private void beyond_beta_tweaks$onFirstTimeEndExitPortalUsedInWorld(BlockState state, net.minecraft.world.level.Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (level.isClientSide()) return;
        if (!(entity instanceof ServerPlayer player)) return;
        if (level.dimension() != Level.END) return;

        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
        if (overworld == null) return;

        BedrockEggState eggState = BedrockEggState.get(overworld);

        // Open the egg if it hasn't been opened yet
        if (eggState.isEggPlaced() && !eggState.isEggOpened()) {
            int eggY = eggState.getEggY();
            BedrockEggBuilder.openEgg(overworld, eggY);
            eggState.setEggOpened(true);
            eggState.setPlayBedrockEggOpeningSoundPending(true);
            BeyondBetaTweaks.LOGGER.debug("Bedrock egg opened by player {} using end portal", player.getName().getString());

            // If only one player on the server, fast-forward to sunrise
            if (player.server.getPlayerList().getPlayerCount() <= 1) {
                long currentTime = overworld.getDayTime();
                long ticksToAdd = getTicksToAdd(currentTime);

                if (ticksToAdd > 0) {
                    overworld.setDayTime(currentTime + ticksToAdd);
                    BeyondBetaTweaks.LOGGER.debug("Fast forwarded time by {} ticks to sunrise", ticksToAdd);
                }
            }
        }
    }

    @Unique
    private static long getTicksToAdd(long currentTime) {
        long targetTime = ModConfig.firstTimeEndExitPortalFastForwardToTargetTime;

        // Calculate how many ticks to add to reach the next target time
        long currentDayTime = currentTime % 24000L;
        long ticksToAdd;
        if (currentDayTime <= targetTime) {
            ticksToAdd = targetTime - currentDayTime;
        } else {
            // Past target time in current day, advance to target time of next day
            ticksToAdd = (24000L - currentDayTime) + targetTime;
        }
        return ticksToAdd;
    }
}
