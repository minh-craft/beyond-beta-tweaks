package com.minhcraft.beyondbetatweaks.util;

import com.minhcraft.beyondbetatweaks.network.ModNetworking;
import com.minhcraft.beyondbetatweaks.network.SaveBookFormattingPacket;
import io.github.mortuusars.scholar.client.gui.widget.textbox.text.Formatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class ScholarFormattingState {
    private static final String NBT_KEY = "PendingFormatting";
    private static final String NBT_COLOR_KEY = "Color";
    private static final String NBT_FORMAT_KEY = "Format";

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

    public static void loadFromBook(ItemStack bookStack) {
        CompoundTag tag = bookStack.getTag();
        if (tag != null && tag.contains(NBT_KEY)) {
            CompoundTag formattingTag = tag.getCompound(NBT_KEY);

            Formatting.Color color = null;
            if (formattingTag.contains(NBT_COLOR_KEY)) {
                String colorStr = formattingTag.getString(NBT_COLOR_KEY);
                if (!colorStr.isEmpty()) {
                    color = Formatting.Color.fromChar(colorStr.charAt(0));
                }
            }

            EnumSet<Formatting.Format> formats = EnumSet.noneOf(Formatting.Format.class);
            if (formattingTag.contains(NBT_FORMAT_KEY)) {
                String formatStr = formattingTag.getString(NBT_FORMAT_KEY);
                for (char c : formatStr.toCharArray()) {
                    Formatting.Format format = Formatting.Format.fromChar(c);
                    if (format != null) {
                        formats.add(format);
                    }
                }
            }

            pendingFormatting = new Formatting(color, formats);
        } else {
            pendingFormatting = null;
        }
    }

    public static void saveToServer(InteractionHand hand) {
        String color = null;
        String formats = null;

        if (pendingFormatting != null && !pendingFormatting.isEmpty()) {
            if (pendingFormatting.color() != null) {
                color = String.valueOf(pendingFormatting.color().getChar());
            }
            if (!pendingFormatting.format().isEmpty()) {
                StringBuilder formatStr = new StringBuilder();
                for (Formatting.Format format : pendingFormatting.format()) {
                    formatStr.append(format.getChar());
                }
                formats = formatStr.toString();
            }
        }

        ModNetworking.sendToServer(new SaveBookFormattingPacket(hand, color, formats));
    }
}
