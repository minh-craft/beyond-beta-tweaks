package com.minhcraft.beyondbetatweaks.mixin.feature.end_flash_backport;

import com.minhcraft.beyondbetatweaks.interfaces.EndFlashAccessor;
import com.minhcraft.beyondbetatweaks.register.BeyondBetaTweaksSounds;
import com.minhcraft.beyondbetatweaks.util.DirectionalSoundInstance;
import com.minhcraft.beyondbetatweaks.util.EndFlashState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

// End flash backport code from https://github.com/Smallinger/Copper-Age-Backport by [Smallinger](https://github.com/Smallinger)
/**
 * Mixin to add End Flash functionality to ClientLevel.
 * Ported from Minecraft 1.21.10 to 1.21.1
 */
@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level implements EndFlashAccessor {

    protected ClientLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Shadow
    public abstract DimensionSpecialEffects effects();

    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private EndFlashState beyond_beta_tweaks$endFlashState;

    /**
     * Initialize EndFlashState for End dimension
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void beyond_beta_tweaks$onInit(CallbackInfo ci) {
        if (this.effects().skyType() == DimensionSpecialEffects.SkyType.END) {
            this.beyond_beta_tweaks$endFlashState = new EndFlashState();
        }
    }

    /**
     * Tick the EndFlashState and play sound when flash starts
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void beyond_beta_tweaks$onTick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        if (this.beyond_beta_tweaks$endFlashState != null) {
            this.beyond_beta_tweaks$endFlashState.tick(this.getGameTime());

            // Play directional sound when flash starts
            if (this.beyond_beta_tweaks$endFlashState.flashStartedThisTick()) {
                this.minecraft.getSoundManager().playDelayed(
                        new DirectionalSoundInstance(
                                BeyondBetaTweaksSounds.WEATHER_END_FLASH,
                                SoundSource.WEATHER,
                                this.random,
                                this.minecraft.gameRenderer.getMainCamera(),
                                this.beyond_beta_tweaks$endFlashState.getXAngle(),
                                this.beyond_beta_tweaks$endFlashState.getYAngle()
                        ),
                        EndFlashState.SOUND_DELAY_IN_TICKS
                );
            }
        }
    }

    /**
     * Accessor for the EndFlashState
     */
    @Override
    @Unique
    public EndFlashState beyond_beta_tweaks$getEndFlashState() {
        return this.beyond_beta_tweaks$endFlashState;
    }
}