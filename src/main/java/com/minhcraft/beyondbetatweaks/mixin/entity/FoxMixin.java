package com.minhcraft.beyondbetatweaks.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

@Mixin(Fox.class)
public abstract class FoxMixin extends Animal {
    protected FoxMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract boolean isFood(@NotNull ItemStack stack);

    @Shadow
    abstract void addTrustedUUID(@Nullable UUID uuid);

    @Shadow abstract List<UUID> getTrustedUUIDs();

    @Shadow
    protected abstract void usePlayerItem(@NotNull Player player, @NotNull InteractionHand hand, @NotNull ItemStack stack);


    // First time fox is fed it will gain trusted status - overrides breeding behavior
    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (this.isFood(itemStack) && getTrustedUUIDs().get(0) == null) {
            if (!this.level().isClientSide) {
                this.usePlayerItem(player, hand, itemStack);
                this.level().broadcastEntityEvent(this, EntityEvent.IN_LOVE_HEARTS);

                // set trusted status - exact UUID doesn't matter, as long as it's populated with any UUID
                this.addTrustedUUID(player.getUUID());
                // will not despawn
                this.setPersistenceRequired();
            }


            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return InteractionResult.PASS;
    }

    // Disable baby foxes from being spawned
    @Inject(
            method = "finalizeSpawn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Fox;setVariant(Lnet/minecraft/world/entity/animal/Fox$Type;)V")
    )
    private void beyond_beta_tweaks$disableBabyFoxes(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData spawnData, CompoundTag dataTag, CallbackInfoReturnable<SpawnGroupData> cir, @Local LocalBooleanRef isBabyFoxRef) {
        isBabyFoxRef.set(false);
    }

    // Override fox spawn items
    // 40% egg, 40% feather, 20% leather
    @Inject(
            method = "populateDefaultEquipmentSlots",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beyond_beta_tweaks$overrideFoxItems(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
        if (random.nextFloat() < 0.2F) {
            float f = random.nextFloat();
            ItemStack itemStack;
            if (f < 0.4F) {
                itemStack = new ItemStack(Items.EGG);
            } else if (f < 0.8F) {
                itemStack = new ItemStack(Items.FEATHER);
            } else {
                itemStack = new ItemStack(Items.LEATHER);
            }

            this.setItemSlot(EquipmentSlot.MAINHAND, itemStack);
        }
        ci.cancel();
    }

    // Fox will trust all players as long as it has been fed once by anyone
    @Inject(
            method = "trusts",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beyond_beta_tweaks$trustAllPlayersIfFedOnce(UUID uuid, CallbackInfoReturnable<Boolean> cir) {
        if (this.getTrustedUUIDs().get(0) != null) {
            cir.setReturnValue(true);
        }
        cir.setReturnValue(false);
    }
}
