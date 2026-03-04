package com.minhcraft.beyondbetatweaks.mixin.feature.biome_alpha_tinting;

import com.minhcraft.beyondbetatweaks.util.BlockAlphaTinting;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.model.quad.BakedQuadView;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.material.Material;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer", remap = false)
public abstract class EmbeddiumBlockRendererMixin {

    @Final
    @Shadow
    private ChunkVertexEncoder.Vertex[] vertices;

    @Inject(
            method = "writeGeometry",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/jellysquid/mods/sodium/client/render/chunk/vertex/builder/ChunkMeshBufferBuilder;push([Lme/jellysquid/mods/sodium/client/render/chunk/vertex/format/ChunkVertexEncoder$Vertex;Lme/jellysquid/mods/sodium/client/render/chunk/terrain/material/Material;)V"
            )
    )
    private void beyond_beta_tweaks$encodeAlphaTintingFactorIntoVertexAlpha(
            BlockRenderContext ctx, ChunkModelBuilder builder, Vec3 offset,
            Material material, BakedQuadView quad, int[] colors,
            QuadLightData light, CallbackInfo ci) {

        if (!quad.hasColor() || !BlockAlphaTinting.isBlockAlphaTinted(ctx.state())) {
            return;
        }

        // Final factor = biomeFactor * blockFactor
        float biomeFactor = BlockAlphaTinting.getBlendedAlphaTintingFactor(ctx.world(), ctx.pos());
        float blockFactor = BlockAlphaTinting.getBlockAlphaTintingFactor(ctx.state());
        float finalFactor = biomeFactor * blockFactor;

        if (finalFactor <= 0.0f) {
            return;
        }

        for (int i = 0; i < 4; i++) {
            this.vertices[i].color = BlockAlphaTinting.encodeAlphaTintingFactorIntoVertexAlpha(this.vertices[i].color, finalFactor);
        }
    }

}