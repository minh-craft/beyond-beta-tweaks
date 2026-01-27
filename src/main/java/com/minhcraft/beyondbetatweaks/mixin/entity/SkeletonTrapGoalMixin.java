package com.minhcraft.beyondbetatweaks.mixin.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.SkeletonTrapGoal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkeletonTrapGoal.class)
public abstract class SkeletonTrapGoalMixin {

    // Make sure trap skeleton doesn't spawn with enchanted equipment
    @Inject(
            method = "createSkeleton",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beyond_beta_tweaks$overrideCreateSkeleton(DifficultyInstance difficulty, AbstractHorse horse, CallbackInfoReturnable<Skeleton> cir) {
        Skeleton skeleton = EntityType.SKELETON.create(horse.level());
        if (skeleton != null) {
            skeleton.finalizeSpawn((ServerLevel)horse.level(), difficulty, MobSpawnType.TRIGGERED, null, null);
            skeleton.setPos(horse.getX(), horse.getY(), horse.getZ());
            skeleton.invulnerableTime = 60;
            skeleton.setPersistenceRequired();
            if (skeleton.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
            }

            // EDIT: no enchanted bow or enchanted helmet
//            skeleton.setItemSlot(
//                    EquipmentSlot.MAINHAND,
//                    EnchantmentHelper.enchantItem(
//                            skeleton.getRandom(),
//                            this.disenchant(skeleton.getMainHandItem()),
//                            (int)(5.0F + difficulty.getSpecialMultiplier() * skeleton.getRandom().nextInt(18)),
//                            false
//                    )
//            );
//            skeleton.setItemSlot(
//                    EquipmentSlot.HEAD,
//                    EnchantmentHelper.enchantItem(
//                            skeleton.getRandom(),
//                            this.disenchant(skeleton.getItemBySlot(EquipmentSlot.HEAD)),
//                            (int)(5.0F + difficulty.getSpecialMultiplier() * skeleton.getRandom().nextInt(18)),
//                            false
//                    )
//            );
        }

        cir.setReturnValue(skeleton);
    }
}
