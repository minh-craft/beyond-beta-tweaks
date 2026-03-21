package com.minhcraft.beyondbetatweaks.interfaces;

import com.minhcraft.beyondbetatweaks.util.EndFlashState;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;

// End flash backport code from https://github.com/Smallinger/Copper-Age-Backport by [Smallinger](https://github.com/Smallinger)
/**
 * Interface to access EndFlashState from ClientLevel.
 * Use EndFlashAccessor.get(clientLevel) to get the EndFlashState.
 */
public interface EndFlashAccessor {

    @Nullable
    EndFlashState beyond_beta_tweaks$getEndFlashState();

    /**
     * Utility method to get EndFlashState from a ClientLevel
     */
    @Nullable
    static EndFlashState get(ClientLevel level) {
        return ((EndFlashAccessor) level).beyond_beta_tweaks$getEndFlashState();
    }
}