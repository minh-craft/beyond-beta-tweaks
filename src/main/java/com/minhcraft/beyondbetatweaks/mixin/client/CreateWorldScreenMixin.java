package com.minhcraft.beyondbetatweaks.mixin.client;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SwitchGrid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$WorldTab")
public abstract class CreateWorldScreenMixin {

    @Inject(
            method = "<init>",
            at = @At("TAIL"),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    // World preset disable code from https://github.com/ChaoticTrials/DefaultWorldType by [@ChaoticTrials](https://github.com/ChaoticTrials)
    private void beyond_beta_tweaks$disableWorldPresetButton(CreateWorldScreen createWorldScreen, CallbackInfo ci, GridLayout.RowHelper rowHelper, CycleButton cycleButton, GridLayout.RowHelper rowHelper2, SwitchGrid.Builder builder, SwitchGrid switchGrid) {
        createWorldScreen.getUiState().addListener(state -> {
            cycleButton.active = false;
        });
    }
}
