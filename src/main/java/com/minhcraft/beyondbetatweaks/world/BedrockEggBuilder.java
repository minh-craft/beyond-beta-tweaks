package com.minhcraft.beyondbetatweaks.world;

import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;

public class BedrockEggBuilder {

    public static final int SCAN_RADIUS = 3;

    public static int calculateEggY(ServerLevel level) {
        int maxHeight = Integer.MIN_VALUE;
        int eggYScanRadius = ModConfig.bedrockEggHeightScanningRadius; // find the highest point within a radius of 0,0
        for (int dx = -eggYScanRadius; dx <= eggYScanRadius; dx++) {
            for (int dz = -eggYScanRadius; dz <= eggYScanRadius; dz++) {
                int height = getGroundHeight(level, dx, dz);
                if (height > maxHeight) {
                    maxHeight = height;
                }
            }
        }
        return maxHeight;
    }

    // scan downwards from MOTION_BLOCKING_NO_LEAVES until non vegetation block is found
    private static int getGroundHeight(ServerLevel level, int x, int z) {
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        while (y > level.getMinBuildHeight()) {
            BlockPos pos = new BlockPos(x, y - 1, z);
            net.minecraft.world.level.block.state.BlockState state = level.getBlockState(pos);
            if (isVegetation(state)) {
                y--;
            } else {
                break;
            }
        }
        return y;
    }

    private static boolean isVegetation(net.minecraft.world.level.block.state.BlockState state) {
        return state.is(net.minecraft.tags.BlockTags.LOGS)
                || state.is(net.minecraft.tags.BlockTags.LEAVES)
                || state.is(net.minecraft.tags.BlockTags.FLOWERS)
                || state.is(net.minecraft.tags.BlockTags.SAPLINGS)
                || state.is(Blocks.VINE)
                || state.is(Blocks.TALL_GRASS)
                || state.is(Blocks.GRASS)
                || state.is(Blocks.FERN)
                || state.is(Blocks.LARGE_FERN)
                || state.is(Blocks.DEAD_BUSH)
//                || state.is(Blocks.BEE_NEST)
//                || state.is(Blocks.MOSS_CARPET)
//                || state.is(Blocks.HANGING_ROOTS)
//                || state.is(Blocks.MANGROVE_ROOTS)
//                || state.is(Blocks.SPORE_BLOSSOM)
//                || state.is(Blocks.AZALEA)
//                || state.is(Blocks.FLOWERING_AZALEA)
                || state.is(Blocks.COCOA)
                || state.is(Blocks.MUSHROOM_STEM)
                || state.is(Blocks.RED_MUSHROOM_BLOCK)
                || state.is(Blocks.BROWN_MUSHROOM_BLOCK)
                || state.is(Blocks.RED_MUSHROOM)
                || state.is(Blocks.BROWN_MUSHROOM);
    }

    public static void clearVegetation(ServerLevel level, int baseY) {
        int clearVegetationScanRadius = 4;
        for (int dx = -clearVegetationScanRadius; dx <= clearVegetationScanRadius; dx++) {
            for (int dz = -clearVegetationScanRadius; dz <= clearVegetationScanRadius; dz++) {
                for (int dy = baseY - 1; dy <= baseY + ModConfig.bedrockEggVegetationClearingHeight; dy++) {
                    BlockPos pos = new BlockPos(dx, dy, dz);
                    net.minecraft.world.level.block.state.BlockState existing = level.getBlockState(pos);
                    // Remove logs, leaves, and other vegetation
                    if (isVegetation(existing)) {
                        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
    }


    // Build the vanilla exit portal structure,
    // then seal it with a bedrock cap on top.
    public static void buildSealedEgg(ServerLevel level, int baseY) {
        BeyondBetaTweaks.LOGGER.info("Building sealed bedrock egg at y={}", baseY);

        // Step 1: Build inactive exit portal structure
        buildInactiveExitPortal(level, baseY);

        // Step 2: Cap the top with bedrock to seal the portal
        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx++) {
            for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz++) {
                double distSq = dx * dx + dz * dz;

                // Cap layer 1 at baseY+1: cover the full bowl (radius 3.5)
                // This replaces the torches and bottom of pillar with bedrock
                if (distSq <= 3.5 * 3.5) {
                    level.setBlockAndUpdate(new BlockPos(dx, baseY + 1, dz), Blocks.BEDROCK.defaultBlockState());
                }

                // Cap layer 2 at baseY+2: slightly smaller dome
                if (distSq <= 3.0 * 3.0) {
                    level.setBlockAndUpdate(new BlockPos(dx, baseY + 2, dz), Blocks.BEDROCK.defaultBlockState());
                }

                // Cap layer 3 at baseY+3: smaller still
                if (distSq <= 2.0 * 2.0) {
                    level.setBlockAndUpdate(new BlockPos(dx, baseY + 3, dz), Blocks.BEDROCK.defaultBlockState());
                }

                // Cap layer 4 at baseY+4: just the very top
                if (distSq <= 1.0) {
                    level.setBlockAndUpdate(new BlockPos(dx, baseY + 4, dz), Blocks.BEDROCK.defaultBlockState());
                }
            }
        }
    }


    // based on the End exit portal structure, only place bedrock blocks
    private static void buildInactiveExitPortal(ServerLevel level, int baseY) {
        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx++) {
            for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz++) {
                double distSq = dx * dx + dz * dz;

                // Inner circle: portal area (radius <= 2.5)
                if (distSq <= 2.5 * 2.5) {
                    // Floor below portal
                    level.setBlockAndUpdate(
                            new BlockPos(dx, baseY - 1, dz),
                            Blocks.BEDROCK.defaultBlockState()
                    );
                    // Portal level
                    level.setBlockAndUpdate(
                            new BlockPos(dx, baseY, dz),
                            Blocks.BEDROCK.defaultBlockState()
                    );
                }

                // Middle ring: bedrock rim (2.5 < radius <= 3.5)
                if (distSq > 2.5 * 2.5 && distSq <= 3.5 * 3.5) {
                    // Rim at portal level
                    level.setBlockAndUpdate(
                            new BlockPos(dx, baseY, dz),
                            Blocks.BEDROCK.defaultBlockState()
                    );
                }
            }
        }
    }

    // open the bedrock egg by removing the bedrock cap layers above the portal
    // re-place the pillar and torches
    // should look like the vanilla end exit portal fountain once done
    public static void openEgg(ServerLevel level, int baseY) {
        BeyondBetaTweaks.LOGGER.debug("Opening bedrock egg at y={}", baseY);

        // Remove all cap layers
        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx++) {
            for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz++) {
                double distSq = dx * dx + dz * dz;

                if (distSq <= 3.5 * 3.5) {
                    level.setBlockAndUpdate(new BlockPos(dx, baseY + 1, dz), Blocks.AIR.defaultBlockState());
                }

                if (distSq <= 3.0 * 3.0) {
                    level.setBlockAndUpdate(new BlockPos(dx, baseY + 2, dz), Blocks.AIR.defaultBlockState());
                }

                if (distSq <= 2.0 * 2.0) {
                    level.setBlockAndUpdate(new BlockPos(dx, baseY + 3, dz), Blocks.AIR.defaultBlockState());
                }

                if (distSq <= 1.0) {
                    level.setBlockAndUpdate(new BlockPos(dx, baseY + 4, dz), Blocks.AIR.defaultBlockState());
                }
            }
        }

        // Add the central pillar
        for (int py = baseY + 1; py <= baseY + 3; py++) {
            level.setBlockAndUpdate(new BlockPos(0, py, 0), Blocks.BEDROCK.defaultBlockState());
        }

        // Add 4 torches to the central pillar
        level.setBlockAndUpdate(new BlockPos(-1, baseY + 2, 0),
                Blocks.WALL_TORCH.defaultBlockState().setValue(
                        net.minecraft.world.level.block.WallTorchBlock.FACING,
                        net.minecraft.core.Direction.WEST));
        level.setBlockAndUpdate(new BlockPos(1, baseY + 2, 0),
                Blocks.WALL_TORCH.defaultBlockState().setValue(
                        net.minecraft.world.level.block.WallTorchBlock.FACING,
                        net.minecraft.core.Direction.EAST));
        level.setBlockAndUpdate(new BlockPos(0, baseY + 2, -1),
                Blocks.WALL_TORCH.defaultBlockState().setValue(
                        net.minecraft.world.level.block.WallTorchBlock.FACING,
                        net.minecraft.core.Direction.NORTH));
        level.setBlockAndUpdate(new BlockPos(0, baseY + 2, 1),
                Blocks.WALL_TORCH.defaultBlockState().setValue(
                        net.minecraft.world.level.block.WallTorchBlock.FACING,
                        net.minecraft.core.Direction.SOUTH));

        // Place the end portal blocks in the inner circle (radius <= 2.5)
        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx++) {
            for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz++) {
                double distSq = dx * dx + dz * dz;
                if (distSq <= 2.5 * 2.5) {
                    level.setBlockAndUpdate(
                            new BlockPos(dx, baseY, dz),
                            Blocks.END_PORTAL.defaultBlockState()
                    );
                }
            }
        }
    }

}
