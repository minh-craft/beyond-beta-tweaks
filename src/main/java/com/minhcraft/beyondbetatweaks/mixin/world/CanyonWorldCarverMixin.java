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
    private static boolean isExposedToAir(ChunkAccess chunk, BlockPos pos, CarvingContext context) {
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
        int maxY = context.getMinGenY() + context.getGenDepth() - 1;

        for (int y = pos.getY() + 1; y <= maxY; y++) {
            checkPos.set(pos.getX(), y, pos.getZ());
            BlockState state = chunk.getBlockState(checkPos);

            if (state.isAir()) {
                return true;
            }
            if (state.is(Blocks.WATER)) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("ConstantValue")
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

        boolean exposed = isExposedToAir(chunk, pos, context);
        IS_EXPOSED.set(exposed);

        if (!exposed) {
            return;
        }

        BlockState currentState = chunk.getBlockState(pos);
        if (currentState.is(Blocks.WATER)) {
            cir.setReturnValue(false);
        }

        // don't carve below y=2
        if (pos.getY() < 3) {
            cir.setReturnValue(false);
            return;
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
