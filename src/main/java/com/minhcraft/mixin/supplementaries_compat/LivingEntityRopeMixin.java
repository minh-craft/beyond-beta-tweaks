package com.minhcraft.mixin.supplementaries_compat;

import com.minhcraft.interfaces.IRopeDescentTracker;
import net.mehvahdjukaar.supplementaries.common.block.blocks.RopeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityRopeMixin extends Entity implements IRopeDescentTracker {

    @Unique
    private boolean classic_reintegrated_tweaks$descendingThroughRope = false;

    @Unique
    private boolean classic_reintegrated_tweaks$wasCrouching = false;

    public LivingEntityRopeMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void classic_reintegrated_tweaks$setDescendingThroughRope(boolean descending) {
        this.classic_reintegrated_tweaks$descendingThroughRope = descending;
    }

    @Override
    public boolean classic_reintegrated_tweaks$isDescendingThroughRope() {
        return this.classic_reintegrated_tweaks$descendingThroughRope;
    }

    @Override
    public void classic_reintegrated_tweaks$setWasCrouching(boolean crouching) {
        this.classic_reintegrated_tweaks$wasCrouching = crouching;
    }

    @Override
    public boolean classic_reintegrated_tweaks$wasCrouching() {
        return this.classic_reintegrated_tweaks$wasCrouching;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void classic_reintegrated_tweaks$trackRopeDescent(CallbackInfo ci) {
        boolean currentlyCrouching = this.isShiftKeyDown();

        if (!currentlyCrouching) {
            this.classic_reintegrated_tweaks$descendingThroughRope = false;
        } else if (!this.classic_reintegrated_tweaks$wasCrouching && currentlyCrouching) {
            if (classic_reintegrated_tweaks$isOnRopeWithRopeBelow()) {
                this.classic_reintegrated_tweaks$descendingThroughRope = true;
            }
        }

        if (this.classic_reintegrated_tweaks$descendingThroughRope && !classic_reintegrated_tweaks$isInOrOnRope()) {
            this.classic_reintegrated_tweaks$descendingThroughRope = false;
        }

        this.classic_reintegrated_tweaks$wasCrouching = currentlyCrouching;
    }

    @Unique
    private boolean classic_reintegrated_tweaks$isOnRopeWithRopeBelow() {
        BlockPos feetPos = this.blockPosition();
        BlockState stateAtFeet = this.level().getBlockState(feetPos);

        if (!(stateAtFeet.getBlock() instanceof RopeBlock)) {
            BlockPos belowFeet = feetPos.below();
            stateAtFeet = this.level().getBlockState(belowFeet);
            if (!(stateAtFeet.getBlock() instanceof RopeBlock)) {
                return false;
            }
            feetPos = belowFeet;
        }

        if (!stateAtFeet.getValue(RopeBlock.DOWN)) {
            return false;
        }

        BlockPos belowPos = feetPos.below();
        BlockState belowState = this.level().getBlockState(belowPos);
        return belowState.getBlock() instanceof RopeBlock;
    }

    @Unique
    private boolean classic_reintegrated_tweaks$isInOrOnRope() {
        BlockPos feetPos = this.blockPosition();
        if (this.level().getBlockState(feetPos).getBlock() instanceof RopeBlock) {
            return true;
        }
        BlockPos belowFeet = feetPos.below();
        return this.level().getBlockState(belowFeet).getBlock() instanceof RopeBlock;
    }
}
