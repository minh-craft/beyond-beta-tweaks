package com.minhcraft.beyondbetatweaks.world.tree;

import com.minhcraft.beyondbetatweaks.register.BeyondBetaTweaksTrunkPlacerTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class BaobabTrunkPlacer extends TrunkPlacer {

    public static final Codec<BaobabTrunkPlacer> CODEC = RecordCodecBuilder.create(instance ->
            trunkPlacerParts(instance)
                    .and(Codec.intRange(1, 16).fieldOf("branch_min_height").forGetter(p -> p.branchMinHeight))
                    .and(Codec.intRange(1, 16).fieldOf("branch_max_height").forGetter(p -> p.branchMaxHeight))
                    .apply(instance, BaobabTrunkPlacer::new)
    );

    private final int branchMinHeight;
    private final int branchMaxHeight;

    public BaobabTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, int branchMinHeight, int branchMaxHeight) {
        super(baseHeight, heightRandA, heightRandB);
        this.branchMinHeight = branchMinHeight;
        this.branchMaxHeight = Math.max(branchMinHeight, branchMaxHeight);
    }

    @Override
    protected @NotNull TrunkPlacerType<?> type() {
        return BeyondBetaTweaksTrunkPlacerTypes.BAOBAB_TRUNK_PLACER;
    }

    @Override
    public @NotNull List<FoliagePlacer.FoliageAttachment> placeTrunk(
            @NotNull LevelSimulatedReader level,
            @NotNull BiConsumer<BlockPos, BlockState> blockSetter,
            @NotNull RandomSource random,
            int freeTreeHeight,
            BlockPos origin,
            @NotNull TreeConfiguration config
    ) {
        // Set dirt below all 4 trunk positions
        setDirtAt(level, blockSetter, random, origin.below(), config);
        setDirtAt(level, blockSetter, random, origin.below().east(), config);
        setDirtAt(level, blockSetter, random, origin.below().south(), config);
        setDirtAt(level, blockSetter, random, origin.below().east().south(), config);

        List<FoliagePlacer.FoliageAttachment> foliageAttachments = new ArrayList<>();

        // Corner positions of the 2x2 trunk
        BlockPos[] corners = {
                origin,                  // NW
                origin.east(),           // NE
                origin.south(),          // SW
                origin.east().south()    // SE
        };

        // Each corner's outward directions (diagonal away from trunk center)
        Direction[][] outwardDirs = {
                { Direction.NORTH, Direction.WEST },   // NW
                { Direction.NORTH, Direction.EAST },   // NE
                { Direction.SOUTH, Direction.WEST },   // SW
                { Direction.SOUTH, Direction.EAST }    // SE
        };

        // Pre-roll start offsets and branch heights for all 4 branches
        // so we can extend the 2x2 trunk to cover the highest start offset
        int[] startOffsets = new int[4];
        int[] branchHeights = new int[4];
        int maxStartOffset = 0;
        for (int i = 0; i < 4; i++) {
            branchHeights[i] = branchMinHeight + random.nextInt(branchMaxHeight - branchMinHeight + 1);
            // 0 = flush with trunk top, 1 = one block above
            startOffsets[i] = random.nextInt(2);
            if (startOffsets[i] > maxStartOffset) {
                maxStartOffset = startOffsets[i];
            }
        }

        // Place the 2x2 trunk, extending up through the highest branch start
        // so there are no gaps at the transition from trunk to branches
        int fullTrunkHeight = freeTreeHeight + maxStartOffset;
        for (int y = 0; y < fullTrunkHeight; y++) {
            placeLog(level, blockSetter, random, origin.above(y), config);
            placeLog(level, blockSetter, random, origin.above(y).east(), config);
            placeLog(level, blockSetter, random, origin.above(y).south(), config);
            placeLog(level, blockSetter, random, origin.above(y).east().south(), config);
        }

        for (int i = 0; i < 4; i++) {
            BlockPos branchBase = corners[i].above(freeTreeHeight + startOffsets[i]);

            // Randomly pick which of the two outward directions is "primary"
            // so branches don't all fork the same way
            Direction primaryDir, secondaryDir;
            if (random.nextBoolean()) {
                primaryDir = outwardDirs[i][0];
                secondaryDir = outwardDirs[i][1];
            } else {
                primaryDir = outwardDirs[i][1];
                secondaryDir = outwardDirs[i][0];
            }

            FoliagePlacer.FoliageAttachment attachment = placeForkingBranch(
                    level, blockSetter, random, branchBase,
                    primaryDir, secondaryDir,
                    branchHeights[i], config
            );
            foliageAttachments.add(attachment);
        }

        return foliageAttachments;
    }

    /**
     * Places an acacia-style forking branch that moves in straight cardinal
     * lines away from the trunk. Each lateral shift is a single cardinal
     * direction, never diagonal, so logs stay face-connected.
     * The branch picks one outward direction and sticks with it, occasionally
     * inserting horizontal-only steps to spread wider.
     */
    private FoliagePlacer.FoliageAttachment placeForkingBranch(
            LevelSimulatedReader level,
            BiConsumer<BlockPos, BlockState> blockSetter,
            RandomSource random,
            BlockPos base,
            Direction primaryDir,
            Direction secondaryDir,
            int branchHeight,
            TreeConfiguration config
    ) {
        BlockPos current = base;

        // Pick one cardinal direction for this branch to go outward in
        Direction outwardDir = random.nextBoolean() ? primaryDir : secondaryDir;

        // First outward shift happens after 0-1 vertical logs
        int firstShiftAt = random.nextInt(2);

        for (int y = 0; y < branchHeight; y++) {
            placeLog(level, blockSetter, random, current, config);

            // First lateral shift — move one block outward
            if (y == firstShiftAt) {
                current = current.relative(outwardDir);
                placeLog(level, blockSetter, random, current, config);
            }

            // On subsequent steps, chance of a horizontal-only step
            // (move outward without going up) for wider spread
            if (y > firstShiftAt && random.nextInt(4) < 2) {
                current = current.relative(outwardDir);
                placeLog(level, blockSetter, random, current, config);
            }

            // Move up
            current = current.above();
        }

        // Final top log
        placeLog(level, blockSetter, random, current, config);

        return new FoliagePlacer.FoliageAttachment(current.above(), 0, false);
    }

    @Override
    public int getTreeHeight(@NotNull RandomSource random) {
        return super.getTreeHeight(random);
    }
}