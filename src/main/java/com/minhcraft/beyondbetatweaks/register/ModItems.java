package com.minhcraft.beyondbetatweaks.register;

import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
import com.minhcraft.beyondbetatweaks.item.RottenLeather;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class ModItems {

    public static final Item ROTTEN_LEATHER = Registry.register(
            BuiltInRegistries.ITEM,
            BeyondBetaTweaks.id("rotten_leather"),
            new RottenLeather(new Item.Properties()));

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(content -> {
            content.accept(ROTTEN_LEATHER);
        });
    }
}
