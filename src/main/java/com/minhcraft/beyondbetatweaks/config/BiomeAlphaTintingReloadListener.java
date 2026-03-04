package com.minhcraft.beyondbetatweaks.config;

import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

// Reloads the biome alpha tinting config when resources are reloaded (F3+T or /reload).
public class BiomeAlphaTintingReloadListener implements SimpleSynchronousResourceReloadListener {

    public static final ResourceLocation ID = new ResourceLocation(BeyondBetaTweaks.MOD_ID, "biome_alpha_tinting");

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        BeyondBetaTweaks.LOGGER.info("Reloading biome alpha tinting config...");
        BiomeAlphaTintingConfigLoader.loadConfig();

        // Force all chunks to re-mesh with the new config
        Minecraft.getInstance().levelRenderer.allChanged();
    }

    public static void register() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
                .registerReloadListener(new BiomeAlphaTintingReloadListener());
    }
}
