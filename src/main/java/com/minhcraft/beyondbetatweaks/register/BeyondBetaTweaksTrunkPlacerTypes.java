package com.minhcraft.beyondbetatweaks.register;

import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
import com.minhcraft.beyondbetatweaks.world.tree.BaobabTrunkPlacer;
import com.minhcraft.beyondbetatweaks.world.tree.StraightBranchedTrunkPlacer;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class BeyondBetaTweaksTrunkPlacerTypes {

    public static final TrunkPlacerType<BaobabTrunkPlacer> BAOBAB_TRUNK_PLACER =
            register("baobab_trunk_placer", BaobabTrunkPlacer.CODEC);

    public static final TrunkPlacerType<StraightBranchedTrunkPlacer> STRAIGHT_BRANCHED_TRUNK_PLACER =
            register("straight_branched_trunk_placer", StraightBranchedTrunkPlacer.CODEC);

    private static <T extends TrunkPlacer> TrunkPlacerType<T> register(String name, Codec<T> codec) {
        return Registry.register(
                BuiltInRegistries.TRUNK_PLACER_TYPE,
                new ResourceLocation(BeyondBetaTweaks.MOD_ID, name),
                new TrunkPlacerType<>(codec)
        );
    }

    // Call from mod initializer to force class loading
    public static void init() {
//        Registry.register(
//                BuiltInRegistries.TRUNK_PLACER_TYPE,
//                new ResourceLocation(BeyondBetaTweaks.MOD_ID, "baobab_trunk_placer"),
//                BAOBAB_TRUNK_PLACER
//        );

//        Registry.register(
//                BuiltInRegistries.TRUNK_PLACER_TYPE,
//                new ResourceLocation(BeyondBetaTweaks.MOD_ID, "straight_branched_trunk_placer"),
//                STRAIGHT_BRANCHED_TRUNK_PLACER
//        );
    }
}