package com.minhcraft.beyondbetatweaks.register;

import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
import com.minhcraft.beyondbetatweaks.world.MessySpruceFoliagePlacer;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class BeyondBetaTweaksFoliagePlacers {

    public static final FoliagePlacerType<MessySpruceFoliagePlacer> MESSY_SPRUCE_FOLIAGE_PLACER =
            register("messy_spruce_foliage_placer", MessySpruceFoliagePlacer.CODEC);


    private static <T extends FoliagePlacer> FoliagePlacerType<T> register(String name, Codec<T> codec) {
        return Registry.register(
                BuiltInRegistries.FOLIAGE_PLACER_TYPE,
                new ResourceLocation(BeyondBetaTweaks.MOD_ID, name),
                new FoliagePlacerType<>(codec)
        );
    }

    public static void init() {
        // Call from mod initializer to force class loading
    }
}
