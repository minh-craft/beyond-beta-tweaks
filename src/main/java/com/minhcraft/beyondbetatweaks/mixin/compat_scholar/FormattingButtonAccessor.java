package com.minhcraft.beyondbetatweaks.mixin.compat_scholar;

import io.github.mortuusars.scholar.client.gui.widget.textbox.display.FormattingToolbar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FormattingToolbar.FormattingButton.class)
public interface FormattingButtonAccessor {
    @Accessor(value = "highlighted", remap = false)
    void beyond_beta_tweaks$setHighlighted(boolean highlighted);
}
