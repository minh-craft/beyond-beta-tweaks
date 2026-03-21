package com.minhcraft.beyondbetatweaks.mixin.feature.charm_inventory_tidying_blacklist;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.gui.components.Button;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.feature.inventory_tidying.InventoryTidyingClient;

@Mixin(InventoryTidyingClient.class)
public abstract class InventoryTidyingClientMixin {

    @Inject(
            method = "handleScreenSetup",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void beyond_beta_tweaks$removeTrainScheduleSortButton(Screen screen, CallbackInfo ci) {
        if (screen.getClass().getName().equals("com.simibubi.create.content.trains.schedule.ScheduleScreen")) {
            ci.cancel();
        }
    }

    @WrapWithCondition(
            method = "handleScreenSetup",
            at = @At(value = "INVOKE", target = "Lsvenhjol/charm/feature/inventory_tidying/InventoryTidyingClient;addSortingButton(Lnet/minecraft/client/gui/screens/Screen;IILnet/minecraft/client/gui/components/Button$OnPress;)V", ordinal = 1)
    )
    private boolean beyond_beta_tweaks$removePlayerSortButton(InventoryTidyingClient instance, Screen screen, int x, int y, Button.OnPress callback) {
        return false;
    }
}
