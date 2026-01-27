package com.minhcraft.beyondbetatweaks.mixin.compat_better_recipe_book;

import com.bawnorton.mixinsquared.TargetHandler;
import com.minhcraft.beyondbetatweaks.mixin.accessors.AbstractContainerScreenAccessor;
import com.mojang.blaze3d.platform.Window;
import marsh.town.brb.BetterRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;

@Mixin(value = MouseHandler.class, priority = 1500)
public abstract class MouseScrollHandlerMixinSquared {

    @Shadow @Final private Minecraft minecraft;

    @Shadow private double accumulatedDY;

    // Disables recipe book scrolling when hovering over a bundle
    // Contributed by https://github.com/Leclowndu93150 - https://github.com/minh-craft/BetterRecipeBook/pull/1
    @TargetHandler(
            mixin = "marsh.town.brb.mixins.MouseScrollHandler",
            name = "onMouseScroll"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beyond_beta_tweaks$disableRecipeBookScrollingWhenHoveringBundle(long window, double arg1, double vertical, CallbackInfo ci, CallbackInfo ci2) {
        if (BetterRecipeBook.queuedScroll == 0 && BetterRecipeBook.config.scrolling.enableScrolling) {
            if (beyond_beta_tweaks$$isHoveringBundle()) {
                ci2.cancel();
                return;
            }
            assert minecraft.player != null;

            double d = (this.minecraft.options.discreteMouseScroll().get() ? Math.signum(vertical) : vertical) * this.minecraft.options.mouseWheelSensitivity().get();
            BetterRecipeBook.queuedScroll = (int) -((int) this.accumulatedDY + d);
        }
        ci2.cancel();
    }

    @Unique
    private boolean beyond_beta_tweaks$$isHoveringBundle() {
        if (!(minecraft.screen instanceof AbstractContainerScreen<?> containerScreen)) {
            return false;
        }

        Window window = minecraft.getWindow();
        double mouseX = minecraft.mouseHandler.xpos() * window.getGuiScaledWidth() / window.getScreenWidth();
        double mouseY = minecraft.mouseHandler.ypos() * window.getGuiScaledHeight() / window.getScreenHeight();

        Slot slot = ((AbstractContainerScreenAccessor) containerScreen).invokeFindSlot(mouseX, mouseY);

        return slot != null && slot.getItem().getItem() instanceof BundleItem;
    }
}

