package com.minhcraft.beyondbetatweaks.mixin.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(WorldCarver.class)
public class CanyonWorldCarverMixin {

    @Unique
    private static final ThreadLocal<Boolean> IS_EXPOSED = ThreadLocal.withInitial(() -> false);

    @Unique
    private static boolean isDirectlyBeneathWater(ChunkAccess chunk, BlockPos pos, CarvingContext context) {
        // Check this column AND immediate neighbors
        // If any nearby column has a direct water connection, this block should flood too
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
        int maxY = context.getMinGenY() + context.getGenDepth() - 1;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (checkColumnHasWater(chunk, pos.getX() + dx, pos.getY(), pos.getZ() + dz, maxY, checkPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Unique
    private static boolean checkColumnHasWater(ChunkAccess chunk, int x, int startY, int z, int maxY, BlockPos.MutableBlockPos checkPos) {
        // If the neighbor is outside this chunk, skip it
        if ((x >> 4) != (chunk.getPos().x) || (z >> 4) != (chunk.getPos().z)) {
            return false;
        }

        for (int y = startY + 1; y <= maxY; y++) {
            checkPos.set(x, y, z);
            BlockState state = chunk.getBlockState(checkPos);

            if (state.is(Blocks.WATER)) {
                return true;
            }
            if (!state.isAir() && !state.is(Blocks.CAVE_AIR)) {
                return false;
            }
        }
        return false;
    }

    @Inject(method = "carveBlock", at = @At("HEAD"), cancellable = true)
    private void beyond_beta_tweaks$onlyDryIfExposed(CarvingContext context, CarverConfiguration config, ChunkAccess chunk,
                                                     Function<BlockPos, Holder<Biome>> biomeGetter, CarvingMask carvingMask,
                                                     BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos checkPos,
                                                     Aquifer aquifer, MutableBoolean reachedSurface,
                                                     CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof CanyonWorldCarver)) {
            IS_EXPOSED.set(false);
            return;
        }

        boolean directlyBeneathWater = isDirectlyBeneathWater(chunk, pos, context);
        // "exposed" = NOT buried under solid rock with water above
        // We want to force dry for anything that ISN'T directly breached into water
        IS_EXPOSED.set(!directlyBeneathWater);

        // don't carve below y=2
        if (pos.getY() < 3) {
            cir.setReturnValue(false);
            return;
        }

        // If directly beneath water (river intersection or in the ocean or in a lake), let vanilla handle it
        // so water flows in naturally at the overlap
        if (directlyBeneathWater) {
            return;
        }

        // Otherwise, prevent water carving
        BlockState currentState = chunk.getBlockState(pos);
        if (currentState.is(Blocks.WATER)) {
            cir.setReturnValue(false);
        }
    }

    @SuppressWarnings("ConstantValue")
    @Inject(method = "getCarveState", at = @At("HEAD"), cancellable = true)
    private void beyond_beta_tweaks$forceAirIfExposed(CarvingContext context, CarverConfiguration config, BlockPos pos, Aquifer aquifer, CallbackInfoReturnable<BlockState> cir) {
        if (!((Object) this instanceof CanyonWorldCarver)) {
            return;
        }

        if (!IS_EXPOSED.get()) {
            return;
        }

        if (pos.getY() <= config.lavaLevel.resolveY(context)) {
            cir.setReturnValue(Blocks.LAVA.defaultBlockState());
            return;
        }

        cir.setReturnValue(Blocks.AIR.defaultBlockState());
    }
}
