package com.minhcraft.mixin.compat_scholar;

import com.minhcraft.util.ScholarFormattingState;
import io.github.mortuusars.scholar.client.gui.widget.textbox.text.Formatting;
import io.github.mortuusars.scholar.client.gui.widget.textbox.text.FormattedStringEditor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FormattedStringEditor.class)
public abstract class FormattedStringEditorMixin {

    @Shadow
    public abstract int getCursorPos();

    @Shadow
    public abstract int length();

    @Shadow
    public abstract boolean isSelecting();

    @Unique
    private Formatting classicReintegratedTweaks$getOriginalFormattingAtCursor() {
        FormattedStringEditor self = (FormattedStringEditor) (Object) this;
        int charBeforeCursor = getCursorPos() - 1;
        if (charBeforeCursor >= 0 && charBeforeCursor < length()) {
            return self.getString().get(charBeforeCursor).formatting();
        }
        return Formatting.EMPTY;
    }

    @Inject(method = "applyFormatting", at = @At("HEAD"), cancellable = true, remap = false)
    private void storePendingFormatting(Formatting formatting, CallbackInfo ci) {
        if (!isSelecting()) {
            if (formatting == Formatting.EMPTY) {
                ScholarFormattingState.setPendingFormatting(Formatting.EMPTY);
            } else {
                Formatting current = ScholarFormattingState.getPendingFormatting();
                if (current == null) {
                    current = classicReintegratedTweaks$getOriginalFormattingAtCursor().copy();
                }
                ScholarFormattingState.setPendingFormatting(current.flip(formatting));
            }
            ci.cancel();
        }
    }

    @Inject(method = "getFormattingAtCursor", at = @At("HEAD"), cancellable = true, remap = false)
    private void usePendingFormatting(CallbackInfoReturnable<Formatting> cir) {
        Formatting pending = ScholarFormattingState.getPendingFormatting();
        if (pending != null) {
            cir.setReturnValue(pending);
        }
    }
}
