package com.minhcraft.beyondbetatweaks.util;

import com.minhcraft.beyondbetatweaks.config.BiomeAlphaTintingConfigLoader;
import me.jellysquid.mods.sodium.client.world.biome.BiomeSlice;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;

// Based on Sodium/Embeddium's BiomeColorCache
public class AlphaTintingFactorCache {

    private static final int NEIGHBOR_BLOCK_RADIUS = 2;

    private final BiomeSlice biomeData;
    private final int blendRadius;
    private final int sizeXZ;
    private final int sizeY;

    private int minX, minY, minZ;

    // Per-Y-slice blurred factor grids, lazily computed
    private final float[][] sliceBuffers;
    private final boolean[] sliceComputed;

    public AlphaTintingFactorCache(BiomeSlice biomeData) {
        this.biomeData = biomeData;
        this.blendRadius = Math.min(7, Minecraft.getInstance().options.biomeBlendRadius().get());
        this.sizeXZ = 16 + ((NEIGHBOR_BLOCK_RADIUS + this.blendRadius) * 2);
        this.sizeY = 16 + (NEIGHBOR_BLOCK_RADIUS * 2);

        this.sliceBuffers = new float[sizeY][];
        this.sliceComputed = new boolean[sizeY];
    }

    public void update(int originMinX, int originMinY, int originMinZ) {
        this.minX = originMinX - NEIGHBOR_BLOCK_RADIUS - this.blendRadius;
        this.minY = originMinY - NEIGHBOR_BLOCK_RADIUS;
        this.minZ = originMinZ - NEIGHBOR_BLOCK_RADIUS - this.blendRadius;

        for (int i = 0; i < sizeY; i++) {
            sliceComputed[i] = false;
        }
    }

    public float getFactor(int blockX, int blockY, int blockZ) {
        int relX = Mth.clamp(blockX - this.minX, 0, sizeXZ - 1);
        int relY = Mth.clamp(blockY - this.minY, 0, sizeY - 1);
        int relZ = Mth.clamp(blockZ - this.minZ, 0, sizeXZ - 1);

        if (!sliceComputed[relY]) {
            computeSlice(relY);
        }

        // After blur, the valid region is inset by blendRadius.
        // The block at world position (originMinX, originMinZ) maps to
        // relative (NEIGHBOR_BLOCK_RADIUS + blendRadius, NEIGHBOR_BLOCK_RADIUS + blendRadius).
        return sliceBuffers[relY][relX * sizeXZ + relZ];
    }

    private void computeSlice(int relY) {
        int worldY = this.minY + relY;
        float[] raw = new float[sizeXZ * sizeXZ];

        // Fill raw factor grid from biome data
        for (int rz = 0; rz < sizeXZ; rz++) {
            int worldZ = this.minZ + rz;
            for (int rx = 0; rx < sizeXZ; rx++) {
                int worldX = this.minX + rx;

                Holder<Biome> biome = this.biomeData.getBiome(worldX, worldY, worldZ);
                ResourceLocation biomeId = biome.unwrapKey()
                        .map(ResourceKey::location)
                        .orElse(null);

                raw[rx * sizeXZ + rz] = biomeId != null
                        ? BiomeAlphaTintingConfigLoader.getAlphaTintingFactorForBiome(biomeId) : 0.0f;
            }
        }

        // Box blur
        if (this.blendRadius > 0) {
            sliceBuffers[relY] = boxBlur2D(raw, sizeXZ, sizeXZ, blendRadius);
        } else {
            sliceBuffers[relY] = raw;
        }

        sliceComputed[relY] = true;
    }

    /**
     * Two-pass separable box blur on a 2D float grid.
     * Grid is indexed as [x * height + z].
     */
    private static float[] boxBlur2D(float[] input, int width, int height, int radius) {
        float[] temp = new float[width * height];
        float[] output = new float[width * height];
        int diameter = 2 * radius + 1;

        // Pass 1: blur along Z (for each X row)
        for (int x = 0; x < width; x++) {
            // Compute initial window sum for z = 0
            float sum = 0;
            for (int z = -radius; z <= radius; z++) {
                int cz = Mth.clamp(z, 0, height - 1);
                sum += input[x * height + cz];
            }

            for (int z = 0; z < height; z++) {
                temp[x * height + z] = sum / diameter;

                // Slide window: add next, remove trailing
                int addZ = z + radius + 1;
                int remZ = z - radius;
                sum += input[x * height + Mth.clamp(addZ, 0, height - 1)];
                sum -= input[x * height + Mth.clamp(remZ, 0, height - 1)];
            }
        }

        // Pass 2: blur along X (for each Z column)
        for (int z = 0; z < height; z++) {
            float sum = 0;
            for (int x = -radius; x <= radius; x++) {
                int cx = Mth.clamp(x, 0, width - 1);
                sum += temp[cx * height + z];
            }

            for (int x = 0; x < width; x++) {
                output[x * height + z] = sum / diameter;

                int addX = x + radius + 1;
                int remX = x - radius;
                sum += temp[Mth.clamp(addX, 0, width - 1) * height + z];
                sum -= temp[Mth.clamp(remX, 0, width - 1) * height + z];
            }
        }

        return output;
    }
}