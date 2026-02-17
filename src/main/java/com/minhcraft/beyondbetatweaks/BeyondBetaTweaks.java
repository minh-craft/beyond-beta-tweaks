package com.minhcraft.beyondbetatweaks;

import com.minhcraft.beyondbetatweaks.config.AlphaGrassConfig;
import com.minhcraft.beyondbetatweaks.config.InnateEnchantmentConfig;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import com.minhcraft.beyondbetatweaks.config.RecipeCustomSortingConfigLoader;
import com.minhcraft.beyondbetatweaks.network.ModNetworking;
import com.minhcraft.beyondbetatweaks.register.BeyondBetaItems;
import com.minhcraft.beyondbetatweaks.register.BeyondBetaRegistry;
import com.minhcraft.beyondbetatweaks.register.BeyondBetaSounds;
import com.minhcraft.beyondbetatweaks.world.BeyondBetaCarvers;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;

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
		BeyondBetaSounds.init();
		BeyondBetaItems.init();
		BeyondBetaRegistry.init();
		BeyondBetaCarvers.register();

		ModNetworking.registerServerReceivers();
		RecipeCustomSortingConfigLoader.loadConfig();
		InnateEnchantmentConfig.loadConfig();
		AlphaGrassConfig.load();
		MidnightConfig.init(MOD_ID, ModConfig.class);
	}

	public static ResourceLocation id(String id) {
		return new ResourceLocation(MOD_ID, id);
	}
}