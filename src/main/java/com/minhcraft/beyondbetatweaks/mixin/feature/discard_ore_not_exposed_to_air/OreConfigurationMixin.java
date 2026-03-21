package com.minhcraft.beyondbetatweaks.mixin.feature.discard_ore_not_exposed_to_air;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(OreConfiguration.class)
public abstract class OreConfigurationMixin {

    @Final
    @Mutable
    @Shadow
    public static Codec<OreConfiguration> CODEC;

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.list(OreConfiguration.TargetBlockState.CODEC).fieldOf("targets")
                        .forGetter(config -> config.targetStates),
                Codec.intRange(0, 64).fieldOf("size")
                        .forGetter(config -> config.size),
                Codec.floatRange(-1.0F, 1.0F) // Changed min value from 0.0F to -1.0F
                        .fieldOf("discard_chance_on_air_exposure")
                        .forGetter(config -> config.discardChanceOnAirExposure)
        ).apply(instance, OreConfiguration::new));
    }
}
