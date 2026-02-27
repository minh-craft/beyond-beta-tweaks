package com.minhcraft.beyondbetatweaks.mixin.feature.bedrock_egg;

import com.minhcraft.beyondbetatweaks.world.BedrockEggState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Unique
    private static final Map<UUID, long[]> bedrockegg$savedRespawns = new ConcurrentHashMap<>();

    // force the end exit portal to always teleport players to world spawn, regardless of any respawn points set
    @Inject(method = "respawn", at = @At("HEAD"))
    private void beyond_beta_tweaks$forceEndExitPortalToUseWorldSpawn(ServerPlayer player, boolean wonGame, CallbackInfoReturnable<ServerPlayer> cir) {
        if (wonGame) {
            // Player is returning from The End.
            // Save their respawn data and clear it so respawn() uses world spawn.
            BlockPos respawnPos = player.getRespawnPosition();
            if (respawnPos != null) {
                // Pack respawn data into a long array:
                // [0] = pos as long, [1] = dimension hash, [2] = angle bits | forced flag
                ResourceKey<Level> dim = player.getRespawnDimension();
                float angle = player.getRespawnAngle();
                boolean forced = player.isRespawnForced();

                long[] data = new long[4];
                data[0] = respawnPos.asLong();
                data[1] = Float.floatToRawIntBits(angle);
                data[2] = forced ? 1L : 0L;
                // Store dimension key hash - we'll match it back in afterRespawn
                // Actually, just store the full info we need to reconstruct
                bedrockegg$savedRespawns.put(player.getUUID(), data);

                // Also store the dimension resource location string via a secondary map
                bedrockegg$savedDimensions.put(player.getUUID(), dim);

                // Clear the respawn position so respawn() falls through to world spawn
                player.setRespawnPosition(Level.OVERWORLD, null, 0f, false, false);
            }
        }
    }

    @Unique
    private static final Map<UUID, ResourceKey<Level>> bedrockegg$savedDimensions = new ConcurrentHashMap<>();

    @Inject(method = "respawn", at = @At("RETURN"))
    private void beyond_beta_tweaks$afterEndExitPortalRespawn(ServerPlayer oldPlayer, boolean wonGame, CallbackInfoReturnable<ServerPlayer> cir) {
        if (wonGame) {
            ServerPlayer newPlayer = cir.getReturnValue();
            if (newPlayer == null) return;

            // restore the player's saved respawn point
            UUID uuid = newPlayer.getUUID();
            long[] data = bedrockegg$savedRespawns.remove(uuid);
            ResourceKey<Level> dim = bedrockegg$savedDimensions.remove(uuid);

            if (data != null && dim != null) {
                BlockPos pos = BlockPos.of(data[0]);
                float angle = Float.intBitsToFloat((int) data[1]);
                boolean forced = data[2] == 1L;

                newPlayer.setRespawnPosition(dim, pos, angle, forced, false);
            }

            moveOffBedrock(newPlayer);
            playBedrockEggOpenSound(newPlayer);
        }
    }

    // make sure the player doesn't spawn on the respawn egg
    @Unique
    private static void moveOffBedrock(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        BlockPos feetPos = player.blockPosition();
        BlockPos below = feetPos.below();

        boolean onBedrock = level.getBlockState(below).is(Blocks.BEDROCK)
                || level.getBlockState(feetPos).is(Blocks.BEDROCK)
                || level.getBlockState(below).is(Blocks.END_PORTAL)
                || level.getBlockState(feetPos).is(Blocks.END_PORTAL);

        if (!onBedrock) return;

        // Search outward in expanding rings for a safe non-bedrock position
        for (int radius = 1; radius <= 32; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    // Only check the outer ring of this radius
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) continue;

                    int x = feetPos.getX() + dx;
                    int z = feetPos.getZ() + dz;
                    int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);

                    BlockPos candidateBelow = new BlockPos(x, y - 1, z);
                    BlockPos candidateFeet = new BlockPos(x, y, z);
                    BlockPos candidateHead = new BlockPos(x, y + 1, z);

                    // Must not be bedrock/end_portal below,
                    // must be solid ground, must have space for the player
                    if (!level.getBlockState(candidateBelow).is(Blocks.BEDROCK)
                            && !level.getBlockState(candidateBelow).is(Blocks.END_PORTAL)
                            && !level.getBlockState(candidateFeet).is(Blocks.END_PORTAL)
                            && level.getBlockState(candidateBelow).isSolid()
                            && !level.getBlockState(candidateFeet).isSolid()
                            && !level.getBlockState(candidateHead).isSolid()) {
                        player.moveTo(x + 0.5, y, z + 0.5, player.getYRot(), player.getXRot());
                        player.connection.teleport(x + 0.5, y, z + 0.5, player.getYRot(), player.getXRot());
                        return;
                    }
                }
            }
        }
    }

    @Unique
    private static void playBedrockEggOpenSound(ServerPlayer player) {
        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
        if (overworld == null) return;

        BedrockEggState eggState = BedrockEggState.get(overworld);
        if (!eggState.isPlayBedrockEggOpeningSoundPending()) return;
        eggState.setPlayBedrockEggOpeningSoundPending(false);

        overworld.globalLevelEvent(1038, new BlockPos(0, eggState.getEggY(), 0), 0);
    }
}