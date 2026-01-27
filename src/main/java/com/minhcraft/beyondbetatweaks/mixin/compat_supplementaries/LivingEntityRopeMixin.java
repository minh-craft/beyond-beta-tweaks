package com.minhcraft.beyondbetatweaks.mixin.compat_supplementaries;

import com.minhcraft.beyondbetatweaks.interfaces.IRopeDescentTracker;
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
    private boolean beyond_beta_tweaks$descendingThroughRope = false;

    @Unique
    private boolean beyond_beta_tweaks$wasCrouching = false;

    public LivingEntityRopeMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void beyond_beta_tweaks$setDescendingThroughRope(boolean descending) {
        this.beyond_beta_tweaks$descendingThroughRope = descending;
    }

    @Override
    public boolean beyond_beta_tweaks$isDescendingThroughRope() {
        return this.beyond_beta_tweaks$descendingThroughRope;
    }

    @Override
    public void beyond_beta_tweaks$setWasCrouching(boolean crouching) {
        this.beyond_beta_tweaks$wasCrouching = crouching;
    }

    @Override
    public boolean beyond_beta_tweaks$wasCrouching() {
        return this.beyond_beta_tweaks$wasCrouching;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void beyond_beta_tweaks$trackRopeDescent(CallbackInfo ci) {
        boolean currentlyCrouching = this.isShiftKeyDown();

        if (!currentlyCrouching) {
            this.beyond_beta_tweaks$descendingThroughRope = false;
        } else if (!this.beyond_beta_tweaks$wasCrouching && currentlyCrouching) {
            if (beyond_beta_tweaks$isOnRopeWithRopeBelow()) {
                this.beyond_beta_tweaks$descendingThroughRope = true;
            }
        }

        if (this.beyond_beta_tweaks$descendingThroughRope && !beyond_beta_tweaks$isInOrOnRope()) {
            this.beyond_beta_tweaks$descendingThroughRope = false;
        }

        this.beyond_beta_tweaks$wasCrouching = currentlyCrouching;
    }

    @Unique
    private boolean beyond_beta_tweaks$isOnRopeWithRopeBelow() {
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
    private boolean beyond_beta_tweaks$isInOrOnRope() {
        BlockPos feetPos = this.blockPosition();
        if (this.level().getBlockState(feetPos).getBlock() instanceof RopeBlock) {
            return true;
        }
        BlockPos belowFeet = feetPos.below();
        return this.level().getBlockState(belowFeet).getBlock() instanceof RopeBlock;
    }
}
