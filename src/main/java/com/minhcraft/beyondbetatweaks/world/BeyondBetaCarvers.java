package com.minhcraft.beyondbetatweaks.world;

import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

public class BeyondBetaCarvers {

    // All beta cave code here is from https://codeberg.org/Nostalgica-Reverie/moderner-beta by @icanttellyou and @b3spectacled and @BlueStaggo

    public static final DeferredRegister<WorldCarver<?>> CARVER = DeferredRegister.create(BeyondBetaTweaks.MOD_ID, Registries.CARVER);
    public static final RegistrySupplier<WorldCarver<BetaCaveCarverConfiguration>> BETA_CAVE = register(
            "beta_cave",
            new BetaCaveWorldCarver(BetaCaveCarverConfiguration.CODEC)
    );

    private static RegistrySupplier<WorldCarver<BetaCaveCarverConfiguration>> register(String id, WorldCarver<BetaCaveCarverConfiguration> carver) {
        return CARVER.register(BeyondBetaTweaks.id(id), () -> carver);
    }

    public static void register() {
        CARVER.register();
    }
}
