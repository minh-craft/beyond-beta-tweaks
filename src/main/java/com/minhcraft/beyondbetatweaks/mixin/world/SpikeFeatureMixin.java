package com.minhcraft.beyondbetatweaks.mixin.world;

import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpikeFeature.class)
public abstract class SpikeFeatureMixin {

    // Remove iron bars from obsidian spikes
    @Redirect(method = "placeSpike", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/SpikeFeature$EndSpike;isGuarded()Z"))
    private boolean beyond_beta_tweaks$isGuardedOverride(SpikeFeature.EndSpike instance) {
        return false;
    }

    // Remove end crystals from obsidian spikes
    @ModifyVariable(method = "placeSpike", at = @At("STORE"), ordinal = 0)
    private EndCrystal beyond_beta_tweaks$injected(EndCrystal x) {
        return null;
    }
}
