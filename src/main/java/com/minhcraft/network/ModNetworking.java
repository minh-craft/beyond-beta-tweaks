package com.minhcraft.network;

import com.minhcraft.ClassicReintegratedTweaks;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class ModNetworking {
    public static final ResourceLocation SAVE_BOOK_FORMATTING = ClassicReintegratedTweaks.id("save_book_formatting");

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(SAVE_BOOK_FORMATTING, SaveBookFormattingPacket::handle);
    }

    public static void registerClientReceivers() {
        // No client-bound packets yet
    }

    public static void sendToServer(SaveBookFormattingPacket packet) {
        ClientPlayNetworking.send(SAVE_BOOK_FORMATTING, packet.toBuffer());
    }
}
