package com.minhcraft.beyondbetatweaks.mixin.feature.end_dimension;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.components.BossHealthOverlay;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BossHealthOverlay.class)
public abstract class BossHealthOverlayMixin {

    // Disable boss world fog - the ender dragon still spawns for a second before being despawned and creates temporary world fog
    @WrapMethod(
            method = "shouldCreateWorldFog"
    )
    private boolean beyond_beta_tweaks$disableBossFog(Operation<Boolean> original) {
        return false;
    }
}
