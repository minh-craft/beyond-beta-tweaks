package com.minhcraft.beyondbetatweaks.mixin.feature.animal_breeding;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Animal.class)
public abstract class AnimalMixin extends AgeableMob {
    protected AnimalMixin(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    // Configurable breeding cooldown time code adapted from https://github.com/antio789/customspeed by [@antio789](https://github.com/antio789)
    @ModifyConstant(
            method = "finalizeSpawnChildFromBreeding",
            constant = @Constant(intValue = 6000)
    )
    private int beyond_beta_tweaks$modifyBreedingCooldown(int constant) {
        if ((Object) this instanceof Chicken) {
            return ModConfig.chickenLayEggsCooldownInSeconds * 20;
        }
        return ModConfig.animalBreedingCooldownInSeconds * 20;
    }


    // Spawn eggs instead of chicken code adapted from https://gitlab.com/supersaiyansubtlety/chicken_nerf/ by [@supersaiyansubtlety](https://gitlab.com/supersaiyansubtlety)
    @WrapWithCondition(
            method = "spawnChildFromBreeding",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V")
    )
    private boolean beyond_beta_tweaks$spawnEggInsteadOfBabyChicken(ServerLevel instance, Entity entity) {
        if ((Object) this instanceof Chicken) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.spawnAtLocation(new ItemStack(Items.EGG, Mth.nextInt(this.random, 1, ModConfig.maxEggsPerLay)));
            return false;
        }
        return true;
    }
}
