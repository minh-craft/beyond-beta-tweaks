package com.minhcraft.beyondbetatweaks.mixin.feature.respawning_animals;

import com.bawnorton.mixinsquared.TargetHandler;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Animal.class, priority = 1500)
public abstract class AnimalMixinSquared {

    @TargetHandler(
            mixin = "mod.adrenix.nostalgic.mixin.tweak.gameplay.animal_spawn.AnimalMixin",
            name = "nt_animal_spawn$modifyRemoveWhenFarAway"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beyond_beta_tweaks$makeRespawnableAnimalsPersistent(boolean removeWhenFarAway, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.makeRespawningAnimalsPersistent) {
            cir.setReturnValue(false);
        }
    }
}
