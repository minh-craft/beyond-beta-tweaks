package com.minhcraft.beyondbetatweaks.mixin.feature.flat_lighting_with_ao;

import com.minhcraft.beyondbetatweaks.config.LeavesAmbientOcclusionConfigLoader;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import me.jellysquid.mods.sodium.client.model.light.data.LightDataAccess;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.model.light.smooth.SmoothLightPipeline;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SmoothLightPipeline.class, remap = false)
public abstract class SmoothLightPipelineMixin {

    @Shadow @Final private LightDataAccess lightCache;

    @Inject(method = "calculate", at = @At("RETURN"))
    private void beyond_beta_tweaks$flattenSmoothLightingLightmap(ModelQuadView quad, BlockPos pos,
                                 QuadLightData out, Direction cullFace, Direction lightFace,
                                 boolean shade, CallbackInfo ci) {

        // Leaves AO reduction (applied first, before flat lighting)
        if (ModConfig.enableReducedLeavesAmbientOcclusion) {
            BlockState state = this.lightCache.getWorld().getBlockState(pos);
            if (state.getBlock() instanceof LeavesBlock) {
                ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
                float factor = LeavesAmbientOcclusionConfigLoader.getAmbientOcclusionReductionFactor(blockId);
                if (factor > 0.0f) {
                    float[] br = out.br;
                    for (int i = 0; i < 4; i++) {
                        br[i] = br[i] + (1.0f - br[i]) * factor;
                    }
                }
            }
        }

        if (ModConfig.enableFlatLightingWithAmbientOcclusion) {
            if (ModConfig.flatLightingMode == ModConfig.FlatLightingMode.AVERAGE) {
                // Average all 4 vertex light values for a flat per-face result
                int totalSky = 0;
                int totalBlock = 0;

                for (int i = 0; i < 4; i++) {
                    int lm = out.lm[i];
                    totalSky += (lm >> 16) & 0xFF;
                    totalBlock += lm & 0xFF;
                }

                int flatLm = ((totalSky / 4) << 16) | (totalBlock / 4);

                for (int i = 0; i < 4; i++) {
                    out.lm[i] = flatLm;
                }
            } else if (ModConfig.flatLightingMode == ModConfig.FlatLightingMode.MINIMUM) {
                // Find the minimum light value among the 4 vertices.
                int minSky = 0xFF;
                int minBlock = 0xFF;
                for (int i = 0; i < 4; i++) {
                    int lm = out.lm[i];
                    // Compare sky light (upper 16 bits) and block light (lower 16 bits) separately
                    int sky = (lm >> 16) & 0xFF;
                    int block = lm & 0xFF;
                    minSky = Math.min(sky, minSky);
                    minBlock = Math.min(block, minBlock);
                }

                int flatLm = (minSky << 16) | minBlock;

                // Set all vertices to the same flat light value
                for (int i = 0; i < 4; i++) {
                    out.lm[i] = flatLm;
                }
            } else {
                // Find the maximum light value among the 4 vertices.
                int maxLm = 0;
                for (int i = 0; i < 4; i++) {
                    int lm = out.lm[i];
                    // Compare sky light (upper 16 bits) and block light (lower 16 bits) separately
                    int sky = (lm >> 16) & 0xFF;
                    int block = lm & 0xFF;
                    int maxSky = (maxLm >> 16) & 0xFF;
                    int maxBlock = maxLm & 0xFF;
                    maxLm = (Math.max(sky, maxSky) << 16) | Math.max(block, maxBlock);
                }

                // Set all vertices to the same flat light value
                for (int i = 0; i < 4; i++) {
                    out.lm[i] = maxLm;
                }
            }
        }
    }
}
