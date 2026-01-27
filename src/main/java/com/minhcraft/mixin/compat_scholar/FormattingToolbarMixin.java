package com.minhcraft.mixin.compat_scholar;

import com.minhcraft.config.ModConfig;
import com.minhcraft.util.ScholarFormattingState;
import io.github.mortuusars.scholar.client.gui.screen.edit.SpreadBookEditScreen;
import io.github.mortuusars.scholar.client.gui.widget.textbox.TextBox;
import io.github.mortuusars.scholar.client.gui.widget.textbox.display.FormattingToolbar;
import io.github.mortuusars.scholar.client.gui.widget.textbox.text.Formatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FormattingToolbar.class)
public abstract class FormattingToolbarMixin {

    @Shadow(remap = false)
    protected int x;

    @Shadow(remap = false)
    protected int y;

    @Shadow(remap = false)
    public abstract TextBox getTextBox();

    @Shadow(remap = false)
    public abstract int getHeight();

    @Shadow(remap = false)
    protected List<FormattingToolbar.FormattingButton> buttons;

    @Inject(method = "shouldShow", at = @At("RETURN"), cancellable = true, remap = false)
    private void alwaysShowWhenFocused(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && ModConfig.alwaysShowScholarFormattingToolbar) {
            if (getTextBox().isFocused()) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "update", at = @At("TAIL"), remap = false)
    private void updateButtonHighlightsAndPosition(CallbackInfo ci) {
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

            Formatting pendingFormatting = ScholarFormattingState.getPendingFormatting();
            if (pendingFormatting == null) {
                pendingFormatting = getTextBox().getEditor().getFormattingAtCursor();
                ScholarFormattingState.setPendingFormatting(pendingFormatting);
            }
            for (FormattingToolbar.FormattingButton button : buttons) {
                if (button.formatting() == Formatting.RESET) continue;
                boolean highlighted = false;
                Formatting.Type type = button.formatting();
                if (type.isColor()) {
                    highlighted = type.equals(pendingFormatting.color());
                } else if (type.isFormat()) {
                    highlighted = pendingFormatting.format().contains((Formatting.Format) type);
                }
                ((FormattingButtonAccessor) button).setHighlighted(highlighted);
            }
        }
    }
}
