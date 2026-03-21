package com.minhcraft.beyondbetatweaks.mixin.feature.fix_fall_damage_sound_login_bug;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Unique
    private int noLoginFallSound$graceTicksRemaining = ModConfig.noFallDamageSoundLoginGracePeriodInTicks;

    // Reset grace period on login
    @Inject(method = "<init>", at = @At("RETURN"))
    private void beyond_beta_tweaks$setupFallDamageSoundLoginGracePeriod(CallbackInfo ci) {
        noLoginFallSound$graceTicksRemaining = ModConfig.noFallDamageSoundLoginGracePeriodInTicks;
        ((LocalPlayer) (Object) this).fallDistance = 0.0F;
    }

    // There is a bug in Vanilla Minecraft 1.20.1 where the fall damage sound sometimes plays when the player logs in.
    // Something about my modpack seems to make that happen more often, possibly some of the optimization mods
    // The fall damage sound is much louder in the modpack as well, so it's very noticeable
    // Hacky fix: Every tick during the login grace period, force fallDistance back to 0 to prevent fall damage sound from playing
    @Inject(method = "tick", at = @At("TAIL"))
    private void beyond_beta_tweaks$disableFallDamageSoundDuringLoginGracePeriod(CallbackInfo ci) {
        if (noLoginFallSound$graceTicksRemaining > 0) {
            ((LocalPlayer) (Object) this).fallDistance = 0.0F;
            noLoginFallSound$graceTicksRemaining--;
        }
    }
}
