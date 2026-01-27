package com.minhcraft.mixin.compat_scholar;

import io.github.mortuusars.scholar.client.gui.screen.edit.SpreadBookEditScreen;
import io.github.mortuusars.scholar.client.gui.widget.textbox.TextBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpreadBookEditScreen.class)
public interface SpreadBookEditScreenAccessor {
    @Accessor(value = "leftPageTextBox", remap = false)
    TextBox getLeftPageTextBox();
}
