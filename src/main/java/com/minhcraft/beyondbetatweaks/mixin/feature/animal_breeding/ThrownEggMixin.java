package com.minhcraft.beyondbetatweaks.mixin.feature.animal_breeding;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownEgg.class)
public abstract class ThrownEggMixin extends ThrowableItemProjectile {

    public ThrownEggMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    // Make chicken from egg chance configurable
    // Only one chicken max per egg
    @Inject(
            method = "onHit",
            at = @At("HEAD"),
            cancellable = true)
    private void beyond_beta_tweaks$modifyEggHatchBehaviorAndTimeToGrowToAdult(HitResult result, CallbackInfo ci) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            if (this.random.nextFloat() < ModConfig.chickenFromEggChance) {
                Chicken chicken = EntityType.CHICKEN.create(this.level());
                if (chicken != null) {
                    chicken.setAge(ModConfig.babyAnimalGrowUpTimeInSeconds*-20);
                    chicken.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                    this.level().addFreshEntity(chicken);
                }
            }

            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
        ci.cancel();
    }
}
