package com.minhcraft.beyondbetatweaks.util;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

// End flash backport code from https://github.com/Smallinger/Copper-Age-Backport by [Smallinger](https://github.com/Smallinger)
/**
 * Manages the state of the End dimension flash effect.
 * Ported from Minecraft 1.21.10 to 1.21.1
 *
 * The End flash makes the sky periodically emit purple glow and play various sounds.
 * - There is a 30 second delay between flashes (600 ticks)
 * - Each flash lasts between 5 and 19 seconds (100-380 ticks)
 * - Block lighting becomes purple tinted during the flash
 */
public class EndFlashState {
    public static final int SOUND_DELAY_IN_TICKS = 30;
    private static final int MIN_FLASH_DURATION_IN_TICKS = 100;
    private static final int MAX_FLASH_DURATION_IN_TICKS = 380;

    private long nextFlashStart = -1;
    private int duration;
    private float intensity;
    private float oldIntensity;
    private float xAngle;
    private float yAngle;

    public void tick(long gametime) {
        this.calculateFlashParameters(gametime);
        this.oldIntensity = this.intensity;
        this.intensity = this.calculateIntensity(gametime);
    }

    private void calculateFlashParameters(long gametime) {
        // Initialize on first tick or schedule next flash after current one ends
        if (this.nextFlashStart == -1) {
            RandomSource random = RandomSource.create(gametime);
            this.scheduleNextFlash(random, gametime);
        } else if (gametime >= this.nextFlashStart + this.duration) {
            // Current flash is over, schedule the next one
            RandomSource random = RandomSource.create(gametime * 31L + this.nextFlashStart);
            long delay = Mth.randomBetweenInclusive(random,
                    ModConfig.endFlashMinimumDelayInTicks,
                    ModConfig.endFlashMaximumDelayInTicks);
            this.nextFlashStart = gametime + delay;
            this.duration = Mth.randomBetweenInclusive(random,
                    MIN_FLASH_DURATION_IN_TICKS, MAX_FLASH_DURATION_IN_TICKS);
            this.xAngle = Mth.randomBetween(random,
                    ModConfig.endFlashMinimumElevationAngle, // Vanilla = -60.0F
                    ModConfig.endFlashMaximumElevationAngle); // Vanilla = 10.0F
            this.yAngle = Mth.randomBetween(random, -180.0F, 180.0F);
        }
    }

    private void scheduleNextFlash(RandomSource random, long gametime) {
        long delay = Mth.randomBetweenInclusive(random,
                ModConfig.endFlashMinimumDelayInTicks,
                ModConfig.endFlashMaximumDelayInTicks);
        this.nextFlashStart = gametime + delay;
        this.duration = Mth.randomBetweenInclusive(random,
                MIN_FLASH_DURATION_IN_TICKS, MAX_FLASH_DURATION_IN_TICKS);
        this.xAngle = Mth.randomBetween(random,
                ModConfig.endFlashMinimumElevationAngle, // Vanilla = -60.0F
                ModConfig.endFlashMaximumElevationAngle); // Vanilla = 10.0F
        this.yAngle = Mth.randomBetween(random, -180.0F, 180.0F);
    }

    private float calculateIntensity(long gametime) {
        if (gametime < this.nextFlashStart || gametime > this.nextFlashStart + this.duration) {
            return 0.0F;
        }
        long elapsed = gametime - this.nextFlashStart;
        return Mth.sin((float) elapsed * (float) Math.PI / this.duration);
    }

    public float getXAngle() { return this.xAngle; }
    public float getYAngle() { return this.yAngle; }

    public float getIntensity(float partialTick) {
        return Mth.lerp(partialTick, this.oldIntensity, this.intensity);
    }

    public boolean flashStartedThisTick() {
        return this.intensity > 0.0F && this.oldIntensity <= 0.0F;
    }
}