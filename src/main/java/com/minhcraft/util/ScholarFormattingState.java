package com.minhcraft.util;

import io.github.mortuusars.scholar.client.gui.widget.textbox.text.Formatting;
import org.jetbrains.annotations.Nullable;

public class ScholarFormattingState {
    @Nullable
    private static Formatting pendingFormatting = null;

    @Nullable
    public static Formatting getPendingFormatting() {
        return pendingFormatting;
    }

    public static void setPendingFormatting(@Nullable Formatting formatting) {
        pendingFormatting = formatting;
    }

    public static void clear() {
        pendingFormatting = null;
    }
}
