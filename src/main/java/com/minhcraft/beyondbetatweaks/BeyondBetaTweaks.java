package com.minhcraft.beyondbetatweaks;

import com.minhcraft.beyondbetatweaks.config.*;
import com.minhcraft.beyondbetatweaks.network.ModNetworking;
import com.minhcraft.beyondbetatweaks.register.*;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public class BeyondBetaTweaks implements ModInitializer {
	public static final String MOD_ID = "beyond-beta-tweaks";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // For responsive knockback feature from https://github.com/Revvilo/responsive-knockback by @Revvilo
	public static final Runnable DO_NOTHING = () -> {};
	public static final AtomicReference<Runnable> TRACKER_TICK = new AtomicReference<>(DO_NOTHING);


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Initializing Beyond Beta Tweaks");
		BeyondBetaTweaksSounds.init();
		BeyondBetaTweaksItems.init();
		BeyondBetaTweaksRegistry.init();
		BeyondBetaTweaksFoliagePlacers.init();
		BeyondBetaTweaksCarvers.register();

		ModNetworking.registerServerReceivers();
		RecipeCustomSortingConfigLoader.loadConfig();
		InnateEnchantmentConfig.loadConfig();
		BiomeAlphaTintingConfigLoader.loadConfig();
		BeyondBetaTweaksReloadListener.register();
		MidnightConfig.init(MOD_ID, ModConfig.class);

		FabricLoader
				.getInstance()
				.getModContainer(MOD_ID)
				.ifPresent(
						modContainer ->
								ResourceManagerHelper.registerBuiltinResourcePack(BeyondBetaTweaks.id(MOD_ID), modContainer, ResourcePackActivationType.ALWAYS_ENABLED));

	}

	public static ResourceLocation id(String id) {
		return new ResourceLocation(MOD_ID, id);
	}
}