package com.minhcraft.beyondbetatweaks.config;

import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
import com.minhcraft.beyondbetatweaks.util.EndSkyColors;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

// Reloads the certain configs when resources are reloaded (F3+T or /reload).
public class BeyondBetaTweaksReloadListener implements SimpleSynchronousResourceReloadListener {

    public static final ResourceLocation ID = new ResourceLocation(BeyondBetaTweaks.MOD_ID, "biome_alpha_tinting");

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        BeyondBetaTweaks.LOGGER.info("Reloading biome alpha tinting, leaves ambient occlusion, and end sky colors...");
        BiomeAlphaTintingConfigLoader.loadConfig();
        LeavesAmbientOcclusionConfigLoader.loadConfig();
        EndSkyColors.reload();

        // Force all chunks to re-mesh with the new config
        Minecraft.getInstance().levelRenderer.allChanged();
    }

    public static void register() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
                .registerReloadListener(new BeyondBetaTweaksReloadListener());
    }
}
