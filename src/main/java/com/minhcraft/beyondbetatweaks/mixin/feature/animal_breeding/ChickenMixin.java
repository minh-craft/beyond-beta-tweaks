package com.minhcraft.beyondbetatweaks.mixin.feature.animal_breeding;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import com.minhcraft.beyondbetatweaks.register.BeyondBetaSounds;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chicken.class)
public abstract class ChickenMixin extends Animal {

    protected ChickenMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public int eggTime;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void beyond_beta_tweaks$setChickenShedFeathersInterval(EntityType entityType, Level level, CallbackInfo ci) {
        this.eggTime = this.random.nextInt(ModConfig.chickenShedFeathersWaitBaseTimeInSeconds*20) + ModConfig.chickenShedFeathersWaitMaxAdditionalTimeInSeconds*20;
    }

    @Inject(
         method = "aiStep",
         at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Chicken;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"),
         cancellable = true
    )
    private void beyond_beta_tweaks$chickensShedFeathersInsteadOfLayEggs(CallbackInfo ci) {
        this.playSound(BeyondBetaSounds.CHICKEN_SHED_FEATHERS, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        this.spawnAtLocation(new ItemStack(Items.FEATHER, this.random.nextIntBetweenInclusive(1, ModConfig.maxFeathersPerShed)));
        this.gameEvent(GameEvent.ENTITY_PLACE);
        this.eggTime = this.random.nextInt(ModConfig.chickenShedFeathersWaitBaseTimeInSeconds*20) + ModConfig.chickenShedFeathersWaitMaxAdditionalTimeInSeconds*20;
        ci.cancel();
    }
}
