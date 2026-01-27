package com.minhcraft.beyondbetatweaks.mixin.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minhcraft.beyondbetatweaks.interfaces.ILocalMobCapCalculator;
import com.minhcraft.beyondbetatweaks.interfaces.ILocalMobCapCalculatorMobCounts;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LocalMobCapCalculator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalMobCapCalculator.class)
public abstract class LocalMobCapCalculatorMixin implements ILocalMobCapCalculator {
    @Unique
    private ResourceKey<Level> dimension = null;

    @WrapOperation(
            method = "addMob",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/LocalMobCapCalculator$MobCounts;add(Lnet/minecraft/world/entity/MobCategory;)V"
            )
    )
    private void beyond_beta_tweaks$addMobCounts(LocalMobCapCalculator.MobCounts instance, MobCategory category, Operation<Void> original) {
        ((ILocalMobCapCalculatorMobCounts) instance).beyond_beta_tweaks$setDimension(this.dimension);

        original.call(instance, category);
    }

    @Override
    public void beyond_beta_tweaks$setDimension(ResourceKey<Level> dimension) {
        this.dimension = dimension;
    }
}
