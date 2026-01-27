package com.minhcraft.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SaveBookFormattingPacket {
    private static final String NBT_KEY = "PendingFormatting";
    private static final String NBT_COLOR_KEY = "Color";
    private static final String NBT_FORMAT_KEY = "Format";

    private final InteractionHand hand;
    @Nullable
    private final String color;
    @Nullable
    private final String formats;

    public SaveBookFormattingPacket(InteractionHand hand, @Nullable String color, @Nullable String formats) {
        this.hand = hand;
        this.color = color;
        this.formats = formats;
    }

    public FriendlyByteBuf toBuffer() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeEnum(hand);
        buf.writeBoolean(color != null);
        if (color != null) {
            buf.writeUtf(color);
        }
        buf.writeBoolean(formats != null);
        if (formats != null) {
            buf.writeUtf(formats);
        }
        return buf;
    }

    public static SaveBookFormattingPacket fromBuffer(FriendlyByteBuf buf) {
        InteractionHand hand = buf.readEnum(InteractionHand.class);
        String color = buf.readBoolean() ? buf.readUtf() : null;
        String formats = buf.readBoolean() ? buf.readUtf() : null;
        return new SaveBookFormattingPacket(hand, color, formats);
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler,
                              FriendlyByteBuf buf, PacketSender responseSender) {
        SaveBookFormattingPacket packet = fromBuffer(buf);

        server.execute(() -> {
            ItemStack bookStack = player.getItemInHand(packet.hand);
            if (bookStack.isEmpty()) {
                return;
            }

            if (packet.color == null && packet.formats == null) {
                // Clear formatting
                CompoundTag tag = bookStack.getTag();
                if (tag != null) {
                    tag.remove(NBT_KEY);
                }
            } else {
                // Save formatting
                CompoundTag formattingTag = new CompoundTag();
                if (packet.color != null) {
                    formattingTag.putString(NBT_COLOR_KEY, packet.color);
                }
                if (packet.formats != null) {
                    formattingTag.putString(NBT_FORMAT_KEY, packet.formats);
                }
                bookStack.getOrCreateTag().put(NBT_KEY, formattingTag);
            }
        });
    }
}
