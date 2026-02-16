package com.minhcraft.beyondbetatweaks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class InnateEnchantmentConfig {

    private static final Map<Item, List<EnchantmentEntry>> ENCHANTMENT_MAP = new HashMap<>();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(BeyondBetaTweaks.MOD_ID + "/innate_enchantments.json");

    public static void loadConfig() {
        Path configPath = PATH;

        if (!Files.exists(configPath)) {
            createDefaultConfig(configPath);
        }

        try {
            String json = Files.readString(configPath);
            Gson gson = new Gson();
            ConfigData config = gson.fromJson(json, ConfigData.class);

            parseConfig(config);
            BeyondBetaTweaks.LOGGER.info("Loaded innate enchantments for {} items", ENCHANTMENT_MAP.size());
        } catch (IOException e) {
            BeyondBetaTweaks.LOGGER.error("Failed to load config:", e);
        }
    }

    private static void createDefaultConfig(Path path) {
        ConfigData defaultConfig = new ConfigData();
        defaultConfig.enchantments = new ArrayList<>();

        // Gold tools get Silk Touch
        ItemEnchantConfig goldTools = new ItemEnchantConfig();
        goldTools.items = Arrays.asList(
                "minecraft:golden_pickaxe",
                "minecraft:golden_axe",
                "minecraft:golden_shovel",
                "minecraft:golden_hoe"
        );
        goldTools.enchantments = Collections.singletonMap("minecraft:silk_touch", 1);
        defaultConfig.enchantments.add(goldTools);

        // Gold armor gets Feather Falling
        ItemEnchantConfig goldArmor = new ItemEnchantConfig();
        goldArmor.items = Arrays.asList(
                "minecraft:golden_boots",
                "minecraft:golden_leggings",
                "minecraft:golden_chestplate",
                "minecraft:golden_helmet"
        );
        goldArmor.enchantments = Collections.singletonMap("minecraft:feather_falling", 1);
        defaultConfig.enchantments.add(goldArmor);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            Files.writeString(path, gson.toJson(defaultConfig));
        } catch (IOException e) {
            BeyondBetaTweaks.LOGGER.error("Failed to save config", e);
        }
    }

    private static void parseConfig(ConfigData config) {
        ENCHANTMENT_MAP.clear();

        for (ItemEnchantConfig itemConfig : config.enchantments) {
            List<EnchantmentEntry> enchantments = new ArrayList<>();

            for (Map.Entry<String, Integer> entry : itemConfig.enchantments.entrySet()) {
                ResourceLocation enchantId = new ResourceLocation(entry.getKey());
                Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(enchantId);

                if (enchantment != null) {
                    enchantments.add(new EnchantmentEntry(enchantment, entry.getValue()));
                } else {
                    BeyondBetaTweaks.LOGGER.error("Unknown enchantment: {}", entry.getKey());
                }
            }

            for (String itemId : itemConfig.items) {
                ResourceLocation id = new ResourceLocation(itemId);
                Item item = BuiltInRegistries.ITEM.get(id);

                if (item != Items.AIR) {
                    ENCHANTMENT_MAP.put(item, enchantments);
                } else {
                    BeyondBetaTweaks.LOGGER.error("Unknown item: {}", itemId);
                }
            }
        }
    }

    public static List<EnchantmentEntry> getEnchantments(Item item) {
        return ENCHANTMENT_MAP.getOrDefault(item, Collections.emptyList());
    }

    public static boolean hasEnchantments(Item item) {
        return ENCHANTMENT_MAP.containsKey(item);
    }

    private static class ConfigData {
        List<ItemEnchantConfig> enchantments;
    }

    private static class ItemEnchantConfig {
        List<String> items;
        Map<String, Integer> enchantments;
    }

    public static class EnchantmentEntry {
        public final Enchantment enchantment;
        public final int level;

        public EnchantmentEntry(Enchantment enchantment, int level) {
            this.enchantment = enchantment;
            this.level = level;
        }
    }
}
