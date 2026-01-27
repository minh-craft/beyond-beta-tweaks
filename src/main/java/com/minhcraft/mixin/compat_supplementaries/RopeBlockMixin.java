package com.minhcraft.mixin.compat_supplementaries;

import com.minhcraft.interfaces.IRopeDescentTracker;
import net.mehvahdjukaar.supplementaries.common.block.blocks.RopeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RopeBlock.class)
public abstract class RopeBlockMixin extends Block {

    public RopeBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        RopeBlock self = (RopeBlock) (Object) this;

        if (context instanceof EntityCollisionContext ec && ec.getEntity() instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof IRopeDescentTracker tracker
                    && tracker.classic_reintegrated_tweaks$isDescendingThroughRope()
                    && state.getValue(RopeBlock.DOWN)
                    && context.isAbove(RopeBlock.COLLISION_SHAPE, pos, true)) {
                BlockPos belowPos = pos.below();
                BlockState belowState = worldIn.getBlockState(belowPos);
                if (belowState.getBlock() instanceof RopeBlock) {
                    return Shapes.empty();
                }
            }

            if (!state.getValue(RopeBlock.UP) && (context.isAbove(RopeBlock.COLLISION_SHAPE, pos, true) || !state.getValue(RopeBlock.DOWN))) {
                return self.getShape(state, worldIn, pos, context);
            }
            return Shapes.empty();
        }

        return self.getShape(state, worldIn, pos, context);
    }
}
