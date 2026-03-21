package com.minhcraft.beyondbetatweaks.world.tree;

import com.minhcraft.beyondbetatweaks.register.BeyondBetaTweaksFoliagePlacers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import org.jetbrains.annotations.NotNull;

public class MessySpruceFoliagePlacer extends FoliagePlacer {

    public static final Codec<MessySpruceFoliagePlacer> CODEC = RecordCodecBuilder.create(instance ->
            foliagePlacerParts(instance)
                    .and(instance.group(
                            // increase max trunk height from 24 to 32
                            IntProvider.codec(0, 32).fieldOf("trunk_height").forGetter(p -> p.trunkHeight),
                            Codec.floatRange(0.0F, 1.0F).fieldOf("skip_chance").forGetter(p -> p.skipChance),
                            Codec.floatRange(0.0F, 1.0F).fieldOf("corner_increased_skip_chance").forGetter(p -> p.cornerIncreasedSkipChance)
                    ))
                    .apply(instance, MessySpruceFoliagePlacer::new)
    );

    private final IntProvider trunkHeight;
    private final float skipChance;
    private final float cornerIncreasedSkipChance;
    private int maxRadius;

    public MessySpruceFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider trunkHeight, float skipChance, float cornerIncreasedSkipChance) {
        super(radius, offset);
        this.trunkHeight = trunkHeight;
        this.skipChance = skipChance;
        this.cornerIncreasedSkipChance = cornerIncreasedSkipChance;
    }

    @Override
    protected @NotNull FoliagePlacerType<?> type() {
        return BeyondBetaTweaksFoliagePlacers.MESSY_SPRUCE_FOLIAGE_PLACER;
    }

    // copy spruce foliage placer
    @Override
    protected void createFoliage(
            @NotNull LevelSimulatedReader level,
            @NotNull FoliageSetter setter,
            RandomSource random,
            @NotNull TreeConfiguration config,
            int maxFreeTreeHeight,
            FoliageAttachment attachment,
            int foliageHeight,
            int foliageRadius,
            int offset
    ) {
        BlockPos blockPos = attachment.pos();
        this.maxRadius = foliageRadius + attachment.radiusOffset();

        int i = random.nextInt(2);
        int j = 1;
        int k = 0;

        for (int l = offset; l >= -foliageHeight; l--) {
            this.placeLeavesRow(level, setter, random, config, blockPos, i, l, attachment.doubleTrunk());
            if (i >= j) {
                i = k;
                k = 1;
                j = Math.min(j + 1, this.maxRadius);
            } else {
                i++;
            }
        }
    }

    @Override
    public int foliageHeight(@NotNull RandomSource random, int height, @NotNull TreeConfiguration config) {
        return Math.max(4, height - this.trunkHeight.sample(random));
    }

    @Override
    protected boolean shouldSkipLocation(@NotNull RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        // Vanilla corner skip — always remove the exact corner
        if (localX == range && localZ == range && range > 0) {
            return true;
        }
        // Only apply messy skip when this layer is at the max radius
        if (range >= this.maxRadius && range > 0 && (localX == range || localZ == range)) {
            int distanceFromTrunkEdge = Math.min(localX, localZ);

            float cornerBonus = 0.0F;
            if (range > 1) {
                // Increase removal chance the further away the block is from the edge (closer to the corner)
                cornerBonus = this.cornerIncreasedSkipChance * ((float) distanceFromTrunkEdge / (float) (range - 1));
            }

            return random.nextFloat() < this.skipChance + cornerBonus;
        }
        return false;
    }
}