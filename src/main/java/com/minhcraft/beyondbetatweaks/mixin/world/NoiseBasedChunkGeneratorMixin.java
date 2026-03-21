package com.minhcraft.beyondbetatweaks.mixin.world;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = NoiseBasedChunkGenerator.class, priority = 1500)
public abstract class NoiseBasedChunkGeneratorMixin {

    /**
     * @author minhcraft
     * @reason Allow configuring overworld lava level. Includes performance optimization from c2me by @ishland
     */
    @Overwrite
    private static Aquifer.FluidPicker createFluidPicker(NoiseGeneratorSettings settings) {
        Aquifer.FluidStatus fluidStatus = new Aquifer.FluidStatus(ModConfig.overworldLavaLevel, Blocks.LAVA.defaultBlockState());
        int i = settings.seaLevel();
        Aquifer.FluidStatus fluidStatus2 = new Aquifer.FluidStatus(i, settings.defaultFluid());
        final int min = Math.min(ModConfig.overworldLavaLevel, i);
        return (j, k, l) -> k < min ? fluidStatus : fluidStatus2;
    }

}
