package com.minhcraft.mixin.scholar_compat;

import com.minhcraft.util.ScholarFormattingState;
import io.github.mortuusars.scholar.client.gui.screen.edit.SpreadBookEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpreadBookEditScreen.class)
public abstract class SpreadBookEditScreenMixin {

    @Inject(method = "onClose", at = @At("HEAD"), remap = true)
    private void clearFormattingStateOnClose(CallbackInfo ci) {
        ScholarFormattingState.clear();
    }
}
