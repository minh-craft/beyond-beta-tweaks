package com.minhcraft.mixin.scholar_compat;

import com.minhcraft.config.ModConfig;
import io.github.mortuusars.scholar.client.gui.screen.edit.SpreadBookEditScreen;
import io.github.mortuusars.scholar.client.gui.widget.textbox.TextBox;
import io.github.mortuusars.scholar.client.gui.widget.textbox.display.FormattingToolbar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FormattingToolbar.class)
public abstract class FormattingToolbarMixin {

    @Shadow
    protected int x;

    @Shadow
    protected int y;

    @Shadow
    public abstract TextBox getTextBox();

    @Shadow
    public abstract int getHeight();

    @Inject(method = "shouldShow", at = @At("RETURN"), cancellable = true, remap = false)
    private void alwaysShowWhenFocused(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && ModConfig.alwaysShowScholarFormattingToolbar) {
            if (getTextBox().isFocused()) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "update", at = @At("TAIL"), remap = false)
    private void repositionWhenNotSelecting(CallbackInfo ci) {
        if (!ModConfig.alwaysShowScholarFormattingToolbar) {
            return;
        }

        if (!getTextBox().getEditor().isSelecting()) {
            Screen currentScreen = Minecraft.getInstance().screen;

            int targetX;
            int targetY;

            if (currentScreen instanceof SpreadBookEditScreen) {
                TextBox leftTextBox = ((SpreadBookEditScreenAccessor) currentScreen).getLeftPageTextBox();
                targetX = leftTextBox.getX();
                targetY = leftTextBox.getY() - leftTextBox.getFont().lineHeight - getHeight();
            } else {
                targetX = getTextBox().getX();
                targetY = getTextBox().getY() - getTextBox().getFont().lineHeight - getHeight();
            }

            this.x = targetX;
            this.y = targetY;
        }
    }
}
