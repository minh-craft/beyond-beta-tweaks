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
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

// Places a straight trunk, with one-block long horizontal branches placed a random distance away from the top of the trunk
public class StraightBranchedTrunkPlacer extends TrunkPlacer {

    public static final Codec<StraightBranchedTrunkPlacer> CODEC = RecordCodecBuilder.create(instance ->
            trunkPlacerParts(instance)
                    .and(Codec.intRange(0, 4).fieldOf("min_branches").forGetter(p -> p.minBranches))
                    .and(Codec.intRange(0, 4).fieldOf("max_branches").forGetter(p -> p.maxBranches))
                    .and(Codec.intRange(0, 16).fieldOf("min_distance_from_top").forGetter(p -> p.minDistFromTop))
                    .and(Codec.intRange(0, 16).fieldOf("max_distance_from_top").forGetter(p -> p.maxDistFromTop))
                    .apply(instance, StraightBranchedTrunkPlacer::new)
    );

    private static final Direction[] HORIZONTAL_DIRS = {
            Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
    };

    private final int minBranches;
    private final int maxBranches;
    private final int minDistFromTop;
    private final int maxDistFromTop;

    public StraightBranchedTrunkPlacer(
            int baseHeight, int heightRandA, int heightRandB,
            int minBranches, int maxBranches,
            int minDistFromTop, int maxDistFromTop
    ) {
        super(baseHeight, heightRandA, heightRandB);
        this.minBranches = minBranches;
        this.maxBranches = Math.max(minBranches, maxBranches);
        this.minDistFromTop = minDistFromTop;
        this.maxDistFromTop = Math.max(minDistFromTop, maxDistFromTop);
    }

    @Override
    protected @NotNull TrunkPlacerType<?> type() {
        return BeyondBetaTweaksTrunkPlacerTypes.STRAIGHT_BRANCHED_TRUNK_PLACER;
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
        setDirtAt(level, blockSetter, random, origin.below(), config);

        List<FoliagePlacer.FoliageAttachment> foliageAttachments = new ArrayList<>();

        // Place straight trunk
        for (int y = 0; y < freeTreeHeight; y++) {
            placeLog(level, blockSetter, random, origin.above(y), config);
        }

        // Foliage at the top of the trunk
        foliageAttachments.add(new FoliagePlacer.FoliageAttachment(origin.above(freeTreeHeight), 0, false));

        // Determine branch count
        int branchCount = minBranches + random.nextInt(maxBranches - minBranches + 1);
        if (branchCount == 0) {
            return foliageAttachments;
        }

        // Shuffle the 4 cardinal directions, then pick one per branch
        List<Direction> availableDirs = new ArrayList<>(List.of(HORIZONTAL_DIRS));
        Collections.shuffle(availableDirs, new java.util.Random(random.nextLong()));

        int distRange = maxDistFromTop - minDistFromTop + 1;
        for (int i = 0; i < branchCount; i++) {
            Direction dir = availableDirs.get(i);
            int distFromTop = minDistFromTop + random.nextInt(distRange);
            int branchY = freeTreeHeight - 1 - distFromTop;
            if (branchY < 0) continue;

            BlockPos branchPos = origin.above(branchY).relative(dir);
            placeLog(level, blockSetter, random, branchPos, config);
        }

        return foliageAttachments;
    }

    @Override
    public int getTreeHeight(@NotNull RandomSource random) {
        return super.getTreeHeight(random);
    }
}