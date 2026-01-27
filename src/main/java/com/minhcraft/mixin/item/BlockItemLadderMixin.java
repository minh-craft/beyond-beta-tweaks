package com.minhcraft.mixin.item;

import com.minhcraft.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemLadderMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void crt$scaffoldingLikeLadderPlacement(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!ModConfig.enableScaffoldingLikeLadderPlacement) {
            return;
        }

        BlockItem self = (BlockItem) (Object) this;
        if (self.getBlock() != Blocks.LADDER) {
            return;
        }

        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        if (!clickedState.is(Blocks.LADDER)) {
            return;
        }

        Direction ladderFacing = clickedState.getValue(LadderBlock.FACING);

        float pitch = context.getPlayer() != null ? context.getPlayer().getXRot() : 0;
        boolean lookingDown = pitch > 0;
        boolean lookingUp = pitch < 0;

        if (!lookingDown && !lookingUp) {
            return;
        }

        BlockPos targetPos = crt$findLadderEndpoint(level, clickedPos, ladderFacing, lookingDown);
        if (targetPos == null) {
            return;
        }

        BlockPos newLadderPos = lookingDown ? targetPos.below() : targetPos.above();

        if (!crt$canPlaceLadderAt(level, newLadderPos, ladderFacing)) {
            return;
        }

        BlockState newLadderState = Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, ladderFacing);

        if (!level.isClientSide) {
            level.setBlock(newLadderPos, newLadderState, 3);

            SoundType soundType = Blocks.LADDER.getSoundType(newLadderState);
            level.playSound(null, newLadderPos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                    (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);

            ItemStack heldItem = context.getItemInHand();
            if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
                heldItem.shrink(1);
            }
        }

        if (context.getPlayer() != null) {
            context.getPlayer().swing(context.getHand());
        }
        cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
    }

    @Unique
    private BlockPos crt$findLadderEndpoint(Level level, BlockPos startPos, Direction ladderFacing, boolean searchDown) {
        int maxDistance = ModConfig.ladderScaffoldingMaxDistance;
        BlockPos currentPos = startPos;
        int distance = 0;
        Direction searchDirection = searchDown ? Direction.DOWN : Direction.UP;

        while (distance < maxDistance) {
            BlockPos nextPos = currentPos.relative(searchDirection);
            BlockState nextState = level.getBlockState(nextPos);

            if (nextState.is(Blocks.LADDER) && nextState.getValue(LadderBlock.FACING) == ladderFacing) {
                currentPos = nextPos;
                distance++;
            } else {
                return currentPos;
            }
        }

        return null;
    }

    @Unique
    private boolean crt$canPlaceLadderAt(Level level, BlockPos pos, Direction ladderFacing) {
        if (!level.isInWorldBounds(pos)) {
            return false;
        }

        BlockState existingState = level.getBlockState(pos);
        if (!existingState.canBeReplaced()) {
            return false;
        }

        BlockPos supportPos = pos.relative(ladderFacing.getOpposite());
        BlockState supportState = level.getBlockState(supportPos);

        return supportState.isFaceSturdy(level, supportPos, ladderFacing);
    }
}
