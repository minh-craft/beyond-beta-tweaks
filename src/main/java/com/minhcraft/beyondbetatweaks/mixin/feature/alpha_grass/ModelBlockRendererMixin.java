package com.minhcraft.beyondbetatweaks.mixin.feature.alpha_grass;

import com.minhcraft.beyondbetatweaks.util.AlphaGrassHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vanilla ModelBlockRenderer mixin for the shader-based approach.
 *
 * In 1.20.1 Mojang mappings, putQuadData calls:
 *
 *   vertexConsumer.putBulkData(poseEntry, bakedQuad,
 *       new float[]{brightness0..3}, red, green, blue,
 *       new int[]{lightmap0..3}, packedOverlay, true);
 *
 * Inside putBulkData, for each vertex, the alpha byte is read from the
 * baked quad's int[] vertex data at offset 3 (the packed ARGB color),
 * and it's always 0xFF (255). The shader receives vertexColor.a = 1.0.
 *
 * To signal alpha-grass to the shader, we need vertex alpha < 0.95.
 * We redirect putBulkData to temporarily modify the quad's vertex data
 * alpha bytes, call the real method, then restore them.
 */
@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRendererMixin {

    @Unique
    private static final ThreadLocal<Float> alphaGrass$alphaFactor = ThreadLocal.withInitial(() -> 0.0f);

    /**
     * At the HEAD of putQuadData, compute and store the alpha factor.
     */
    @Inject(method = "putQuadData", at = @At("HEAD"))
    private void alphaGrass$captureAlphaFactor(
            BlockAndTintGetter level, BlockState state, BlockPos pos,
            VertexConsumer consumer, PoseStack.Pose pose, BakedQuad quad,
            float b0, float b1, float b2, float b3,
            int l0, int l1, int l2, int l3,
            int overlay, CallbackInfo ci) {

        if (!quad.isTinted()) {
            alphaGrass$alphaFactor.set(0.0f);
            return;
        }

        // Read the factor from BlockColorsMixin
        float factor = AlphaGrassHelper.CURRENT_ALPHA_FACTOR.get();
//        alphaGrass$alphaFactor.set(Math.max(0.0f, factor));
        // TODO TESTING
        alphaGrass$alphaFactor.set(Math.max(1.0f, factor));
    }

    /**
     * Redirect the putBulkData call to inject modified vertex alpha.
     *
     * Target: the VertexConsumer.putBulkData call inside putQuadData.
     */
    @Redirect(
            method = "putQuadData",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;[FFFF[IIZ)V"
            )
    )
    private void alphaGrass$redirectPutBulkData(
            VertexConsumer consumer, PoseStack.Pose pose, BakedQuad quad,
            float[] brightness, float red, float green, float blue,
            int[] lightmaps, int overlay, boolean doShading) {

        float factor = alphaGrass$alphaFactor.get();

        if (factor <= 0.0f) {
            // No alpha-grass: call vanilla putBulkData normally
            consumer.putBulkData(pose, quad, brightness, red, green, blue, lightmaps, overlay, doShading);
            return;
        }

        // We need to modify the vertex alpha in the baked quad data.
        // BakedQuad stores vertices as int[], 8 ints per vertex, 4 vertices.
        // Layout per vertex: [x, y, z, color, u, v, light, normal]
        // The color int at index 3 (per vertex) is packed ARGB.
        // We need to change the alpha byte (bits 24-31).
        //
        // Encode: alphaFactor=1.0 → vertexAlpha = 0 (0x00)
        //         alphaFactor=0.0 → vertexAlpha = 242 (0xF2, = 0.95 * 255)
        // The shader treats alpha >= 0.95 (≈242+) as vanilla.

        int encodedAlpha = (int) ((1.0f - factor) * 0.95f * 255.0f);
        encodedAlpha = Math.max(0, Math.min(242, encodedAlpha));

        int[] vertices = quad.getVertices();
        int[] backup = new int[4]; // save original color ints

        // Modify alpha byte for each vertex
        for (int v = 0; v < 4; v++) {
            int colorIdx = v * 8 + 3; // color is at offset 3 in each vertex
            backup[v] = vertices[colorIdx];

            // The color int is in ABGR format in the vertex data (little-endian).
            // Actually, in vanilla BakedQuad, the color is packed as AABBGGRR
            // (alpha in bits 24-31).
            // Clear old alpha and set new one:
            vertices[colorIdx] = (vertices[colorIdx] & 0x00FFFFFF) | (encodedAlpha << 24);
        }

        // Call the real putBulkData with modified vertex data
        consumer.putBulkData(pose, quad, brightness, red, green, blue, lightmaps, overlay, doShading);

        // Restore original vertex data (quads are shared/cached, don't permanently modify)
        for (int v = 0; v < 4; v++) {
            vertices[v * 8 + 3] = backup[v];
        }
    }

    @Inject(method = "putQuadData", at = @At("RETURN"))
    private void alphaGrass$cleanup(
            BlockAndTintGetter level, BlockState state, BlockPos pos,
            VertexConsumer consumer, PoseStack.Pose pose, BakedQuad quad,
            float b0, float b1, float b2, float b3,
            int l0, int l1, int l2, int l3,
            int overlay, CallbackInfo ci) {
        alphaGrass$alphaFactor.set(0.0f);
        AlphaGrassHelper.CURRENT_ALPHA_FACTOR.set(-1.0f);
    }
}
