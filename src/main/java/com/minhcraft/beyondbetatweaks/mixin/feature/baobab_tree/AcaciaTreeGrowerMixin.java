package com.minhcraft.beyondbetatweaks.mixin.feature.baobab_tree;

import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.grower.AcaciaTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AcaciaTreeGrower.class)
public abstract class AcaciaTreeGrowerMixin extends AbstractTreeGrower {

    @Unique
    private static final ResourceKey<ConfiguredFeature<?, ?>> BAOBAB_TREE = ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            new ResourceLocation(BeyondBetaTweaks.MOD_ID, "baobab_tree")
    );

    // Copy of AbstractMegaTreeGrower.growTree
    @Override
    public boolean growTree(@NotNull ServerLevel level, @NotNull ChunkGenerator generator, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull RandomSource random) {
        for (int i = 0; i >= -1; i--) {
            for (int j = 0; j >= -1; j--) {
                if (AbstractMegaTreeGrower.isTwoByTwoSapling(state, level, pos, i, j)) {
                    return beyond_beta_tweaks$placeMega(level, generator, pos, state, random, i, j);
                }
            }
        }

        return super.growTree(level, generator, pos, state, random);
    }

    // Copy of AbstractMegaTreeGrower.placeMega
    @Unique
    private boolean beyond_beta_tweaks$placeMega(
            ServerLevel level, ChunkGenerator generator, BlockPos pos,
            BlockState state, RandomSource random, int offsetX, int offsetZ
    ) {
        Holder<ConfiguredFeature<?, ?>> holder = level.registryAccess()
                .registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getHolder(BAOBAB_TREE)
                .orElse(null);

        if (holder == null) {
            return false;
        }

        BlockState air = Blocks.AIR.defaultBlockState();
        level.setBlock(pos.offset(offsetX, 0, offsetZ), air, 4);
        level.setBlock(pos.offset(offsetX + 1, 0, offsetZ), air, 4);
        level.setBlock(pos.offset(offsetX, 0, offsetZ + 1), air, 4);
        level.setBlock(pos.offset(offsetX + 1, 0, offsetZ + 1), air, 4);

        if (holder.value().place(level, generator, random, pos.offset(offsetX, 0, offsetZ))) {
            return true;
        } else {
            // Restore saplings on failure
            level.setBlock(pos.offset(offsetX, 0, offsetZ), state, 4);
            level.setBlock(pos.offset(offsetX + 1, 0, offsetZ), state, 4);
            level.setBlock(pos.offset(offsetX, 0, offsetZ + 1), state, 4);
            level.setBlock(pos.offset(offsetX + 1, 0, offsetZ + 1), state, 4);
            return false;
        }
    }
}