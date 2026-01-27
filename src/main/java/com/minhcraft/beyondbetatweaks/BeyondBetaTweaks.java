package com.minhcraft.beyondbetatweaks;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import com.minhcraft.beyondbetatweaks.config.RecipeCustomSortingConfigLoader;
import com.minhcraft.beyondbetatweaks.network.ModNetworking;
import com.minhcraft.beyondbetatweaks.register.ModItems;
import com.minhcraft.beyondbetatweaks.register.ModRegistry;
import com.minhcraft.beyondbetatweaks.register.ModSounds;
import com.minhcraft.beyondbetatweaks.world.BeyondBetaCarvers;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeyondBetaTweaks implements ModInitializer {
	public static final String MOD_ID = "beyond-beta-tweaks";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Initializing Beyond Beta Tweaks");
		ModSounds.init();
		ModItems.init();
		ModRegistry.init();
		BeyondBetaCarvers.register();

		ModNetworking.registerServerReceivers();
		RecipeCustomSortingConfigLoader.loadConfig();
		MidnightConfig.init(MOD_ID, ModConfig.class);
	}

	public static ResourceLocation id(String id) {
		return new ResourceLocation(MOD_ID, id);
	}
}