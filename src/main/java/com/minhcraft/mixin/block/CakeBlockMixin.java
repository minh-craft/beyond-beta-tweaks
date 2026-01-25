package com.minhcraft.mixin.block;

import antigers.melancholic_hunger.components.PlayerComponents;
import com.minhcraft.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import static net.minecraft.world.level.block.CakeBlock.BITES;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin {

    @Unique
    private static final FoodProperties CAKE_FOOD_PROPERTIES = new FoodProperties.Builder().nutrition(2).saturationMod(10F).build();


    @Unique
    private static final int CAKE_ABSORPTION_DURATION = 24000; // 20:00 minutes
    @Unique
    private static final int CAKE_ABSORPTION_REFRESH_WAIT_TIME = 1200; // 1:00 minute

    @Inject(
            method = "eat",
            at = @At("HEAD"),
            cancellable = true
    )
    // Allow overeating of cakes. If the player is at max health, they can continue eating cake to add temporary absorption hearts
    private static void eat(LevelAccessor level, BlockPos pos, BlockState state, Player player, CallbackInfoReturnable<InteractionResult> cir) {
        float absorptionHearts = player.getAbsorptionAmount();
        // allow eating to refresh absorption duration even if absorption hearts are at max
        boolean absorptionCanRefresh = (
            player.hasEffect(MobEffects.ABSORPTION)
            && player.getEffect(MobEffects.ABSORPTION).getDuration() < (CAKE_ABSORPTION_DURATION - CAKE_ABSORPTION_REFRESH_WAIT_TIME)
            && absorptionHearts <= ModConfig.cakeMaxAbsorptionHearts
        );
        if (!(player.canEat(false) || absorptionHearts < ModConfig.cakeMaxAbsorptionHearts || absorptionCanRefresh)) {
            cir.setReturnValue(InteractionResult.PASS);
        } else {
            if (player.getHealth() == player.getMaxHealth()) {
                if (absorptionCanRefresh) {
                    // remove potential higher tier absorption from enchanted golden apples so that it can be overridden with lower tier but longer duration
                    player.removeEffect(MobEffects.ABSORPTION);
                }
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, CAKE_ABSORPTION_DURATION, 1, false, false, false));
                player.setAbsorptionAmount(Math.min(absorptionHearts+1, ModConfig.cakeMaxAbsorptionHearts));
                level.playSound(player, player.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 1.0F, 1.0F);
                level.playSound(player, player.blockPosition(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 1.0F, 1.0F);
            } else {
                player.awardStat(Stats.EAT_CAKE_SLICE);
                PlayerComponents.HEALTH_REGENERATION.get(player).eat(Items.CAKE.getDefaultInstance(), CAKE_FOOD_PROPERTIES);
                level.playSound(player, player.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 1.0F, 1.0F);

            }

            int i = state.getValue(BITES);
            level.gameEvent(player, GameEvent.EAT, pos);
            if (i < 6) {
                level.setBlock(pos, state.setValue(BITES, i + 1), 3);
            } else {
                level.removeBlock(pos, false);
                level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
            }

            cir.setReturnValue(InteractionResult.SUCCESS);
        }

        cir.cancel();
    }
}
