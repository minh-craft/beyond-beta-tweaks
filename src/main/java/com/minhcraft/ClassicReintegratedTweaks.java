package com.minhcraft;

import com.minhcraft.config.ModConfig;
import com.minhcraft.config.RecipeCustomSortingConfigLoader;
import com.minhcraft.network.ModNetworking;
import com.minhcraft.register.ModItems;
import com.minhcraft.register.ModRegistry;
import com.minhcraft.register.ModSounds;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassicReintegratedTweaks implements ModInitializer {
	public static final String MOD_ID = "classic-reintegrated-tweaks";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Initializing Classic Reintegrated Tweaks");
		ModSounds.init();
		ModItems.init();
		ModRegistry.init();
		ModNetworking.registerServerReceivers();
		RecipeCustomSortingConfigLoader.loadConfig();
		MidnightConfig.init(MOD_ID, ModConfig.class);
	}

	public static ResourceLocation id(String id) {
		return new ResourceLocation(MOD_ID, id);
	}
}