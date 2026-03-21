package com.minhcraft.beyondbetatweaks.mixin.feature.end_dimension_space_sky;


import com.minhcraft.beyondbetatweaks.config.ModConfig;
import com.minhcraft.beyondbetatweaks.util.EndFlashRenderer;
import com.minhcraft.beyondbetatweaks.util.EndSkyColors;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow private ClientLevel level;
    @Shadow private int ticks;

    // Pre-computed star data: {unitX, unitY, unitZ, size, sinRotation, cosRotation}
    @Unique private float[][] endStarData;
    @Unique private boolean endStarDataBuilt = false;

//    @Unique private static final float END_SKY_R;
//    @Unique private static final float END_SKY_G;
//    @Unique private static final float END_SKY_B;
//
//    @Unique private static final float END_FOG_R;
//    @Unique private static final float END_FOG_G;
//    @Unique private static final float END_FOG_B;

//    static {
//        int skyColor = Integer.parseInt(ModConfig.endDimensionSkyColor.replace("#", ""), 16);
//        END_SKY_R = ((skyColor >> 16) & 0xFF) / 255.0f;
//        END_SKY_G = ((skyColor >> 8) & 0xFF) / 255.0f;
//        END_SKY_B = (skyColor & 0xFF) / 255.0f;
//
//        int fogColor = Integer.parseInt(ModConfig.endDimensionFogColor.replace("#", ""), 16);
//        END_FOG_R = ((fogColor >> 16) & 0xFF) / 255.0f;
//        END_FOG_G = ((fogColor >> 8) & 0xFF) / 255.0f;
//        END_FOG_B = (fogColor & 0xFF) / 255.0f;
//    }


    // ─── SKY RENDERING ──────────────────────────────────────────────────

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void beyond_beta_tweaks$renderSky(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick,
                                  Camera camera, boolean isFoggy, Runnable fogSetup, CallbackInfo ci) {
        if (this.level == null || this.level.dimension() != Level.END) return;
        if (camera.getFluidInCamera() != FogType.NONE) return;

        ci.cancel();

        // Run fog setup first like vanilla does
        fogSetup.run();

        renderEndSky(poseStack, partialTick, fogSetup);
    }

    @Unique
    private void renderEndSky(PoseStack poseStack, float partialTick, Runnable fogSetup) {
        RenderSystem.depthMask(false);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        Matrix4f matrix = poseStack.last().pose();

        // ── Step 1: Fill the entire sky with solid color background ──
        // FLIPPED: upper = fog color, lower = sky color

        // Lower hemisphere first (drawn behind): sky color
        // Extends up to y=+2 (slightly above horizon)
        RenderSystem.setShaderColor(EndSkyColors.SKY_R, EndSkyColors.SKY_G, EndSkyColors.SKY_B, 1.0f);
        RenderSystem.setShader(GameRenderer::getPositionShader);

        builder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        builder.vertex(matrix, 0.0f, -48.0f, 0.0f).endVertex();
        for (int i = 180; i >= -180; i -= 45) {
            float rad = i * ((float) Math.PI / 180.0f);
            builder.vertex(matrix, 512.0f * Mth.cos(rad), 2.0f, 512.0f * Mth.sin(rad)).endVertex();
        }
        BufferUploader.drawWithShader(builder.end());

        // Upper hemisphere (drawn on top, overlapping): fog color
        // Extends down to y=-2 (slightly below horizon)
        RenderSystem.setShaderColor(EndSkyColors.FOG_UPPER_R, EndSkyColors.FOG_UPPER_G, EndSkyColors.FOG_UPPER_B, 1.0f);

        builder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        builder.vertex(matrix, 0.0f, 48.0f, 0.0f).endVertex();
        for (int i = -180; i <= 180; i += 45) {
            float rad = i * ((float) Math.PI / 180.0f);
            builder.vertex(matrix, 512.0f * Mth.cos(rad), -2.0f, 512.0f * Mth.sin(rad)).endVertex();
        }
        BufferUploader.drawWithShader(builder.end());

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        // ── Step 2: Stars (drawn BEFORE the horizon gradient so the gradient fades them out) ──
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        beyond_beta_tweaks$renderEndStars(poseStack, partialTick, fogSetup);

        // ── Step 3: Horizon gradient ──
        // Drawn AFTER stars so it overlays on top, naturally hiding stars near the horizon
        beyond_beta_tweaks$renderHorizonGradient(poseStack);

        // ── Step 4: End Flash ──
        EndFlashRenderer.render(poseStack);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    /**
     * Renders the horizon fog gradient as a series of elevation-based rings.
     * This overlays fog color with varying alpha on top of the sky + stars,
     * creating the smooth dark-to-light transition at the horizon.
     */
    @Unique
    private void beyond_beta_tweaks$renderHorizonGradient(PoseStack poseStack) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Matrix4f matrix = poseStack.last().pose();

        int segments = 64;
        float radius = 400.0f;

        // Elevation-based strips: {bottom_deg, top_deg, alpha_bottom, alpha_top}
        // FLIPPED: overlay sky color (lighter) from below, fading upward
        // Transition compressed closer to the horizon
        float[][] strips = {
                // Deep below: solid sky color overlay
                {-90.0f, -5.0f, 1.0f, 1.0f},
                // Below horizon: still solid
                { -5.0f,  0.0f, 1.0f, 1.0f},
                // Just above horizon: rapid fade
                {  0.0f,  2.0f, 1.0f, 0.80f},
                // Lower gradient
                {  2.0f,  5.0f, 0.80f, 0.45f},
                // Mid gradient
                {  5.0f,  9.0f, 0.45f, 0.12f},
                // Fade out
                {  9.0f, 14.0f, 0.12f, 0.0f},
        };

        for (float[] strip : strips) {
            float elevBotDeg = strip[0];
            float elevTopDeg = strip[1];
            float alphaBot = strip[2];
            float alphaTop = strip[3];

            float elevBotRad = elevBotDeg * ((float) Math.PI / 180.0f);
            float elevTopRad = elevTopDeg * ((float) Math.PI / 180.0f);

            float yBot = Mth.sin(elevBotRad) * radius;
            float rBot = Mth.cos(elevBotRad) * radius;
            float yTop = Mth.sin(elevTopRad) * radius;
            float rTop = Mth.cos(elevTopRad) * radius;

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder builder = tesselator.getBuilder();

            builder.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

            for (int i = 0; i <= segments; i++) {
                float angle = (float) (i * 2.0 * Math.PI / segments);
                float cosA = Mth.cos(angle);
                float sinA = Mth.sin(angle);

                // Overlay SKY color instead of fog color
                builder.vertex(matrix, cosA * rBot, yBot, sinA * rBot)
                        .color(EndSkyColors.SKY_R, EndSkyColors.SKY_G, EndSkyColors.SKY_B, alphaBot).endVertex();
                builder.vertex(matrix, cosA * rTop, yTop, sinA * rTop)
                        .color(EndSkyColors.SKY_R, EndSkyColors.SKY_G, EndSkyColors.SKY_B, alphaTop).endVertex();
            }

            BufferUploader.drawWithShader(builder.end());
        }
    }

    // ─── STARS ───────────────────────────────────────────────────────────

    /**
     * Renders stars using immediate mode. Each frame, the rotation is applied
     * to each star's center position to determine its current elevation.
     * Stars below the fade threshold are made transparent based on their
     * ROTATED position, so fading works correctly with rotation.
     */
    @Unique
    private void beyond_beta_tweaks$renderEndStars(PoseStack poseStack, float partialTick, Runnable fogSetup) {
        if (!endStarDataBuilt) {
            beyond_beta_tweaks$buildStarData();
            endStarDataBuilt = true;
        }
        if (endStarData == null) return;

        FogRenderer.setupNoFog();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        // Calculate rotation angles
        float time = (this.ticks + partialTick) * 0.001f;
        float xRotDeg = time * 12.0f; // rotation speed - adjust here
        float zRotDeg = time * 2.0f;

        // Build rotation matrix (X then Z) to transform star centers
        double xRotRad = Math.toRadians(xRotDeg);
        double zRotRad = Math.toRadians(zRotDeg);
        double cosXR = Math.cos(xRotRad), sinXR = Math.sin(xRotRad);
        double cosZR = Math.cos(zRotRad), sinZR = Math.sin(zRotRad);

        // Combined rotation matrix R = Rz * Rx
        // We only need the second row to get rotated Y:
        // rotatedY = m10*x + m11*y + m12*z
        double m10 = sinZR * cosXR;
        double m11 = cosZR * cosXR;
        double m12 = -sinXR;

        // Fade thresholds
        // 0 = horizon
        double fadeTopY = Math.sin(Math.toRadians(ModConfig.endDimensionStarFadeTopAngle));
        double fadeBotY = Math.sin(Math.toRadians(ModConfig.endDimensionStarFadeBottomAngle));
        float starBrightness = 0.8f;

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(xRotDeg)); // vertical star rotation
        poseStack.mulPose(Axis.ZP.rotationDegrees(zRotDeg)); // tilt star rotation

        Matrix4f matrix = poseStack.last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (float[] star : endStarData) {
            float cx = star[0];
            float cy = star[1];
            float cz = star[2];
            float size = star[3];
            float sinR = star[4];
            float cosR = star[5];

            // Calculate rotated Y to determine current elevation
            double rotatedY = m10 * cx + m11 * cy + m12 * cz;

            // Determine alpha based on rotated elevation
            float alpha;
            if (rotatedY >= fadeTopY) {
                alpha = starBrightness;
            } else if (rotatedY <= fadeBotY) {
                continue; // fully invisible, skip
            } else {
                alpha = starBrightness * (float) ((rotatedY - fadeBotY) / (fadeTopY - fadeBotY));
            }

            // Build quad at this star's (unrotated) position
            // The poseStack rotation will transform it to the correct rotated position
            double farDist = 100.0;
            double sx = cx * farDist;
            double sy = cy * farDist;
            double sz = cz * farDist;

            double yAngle = Math.atan2(cx, cz);
            double sinYA = Math.sin(yAngle);
            double cosYA = Math.cos(yAngle);

            double xzDist = Math.sqrt(cx * cx + cz * cz);
            double xAngle = Math.atan2(xzDist, cy);
            double sinXA = Math.sin(xAngle);
            double cosXA = Math.cos(xAngle);

            for (int v = 0; v < 4; v++) {
                double vx = ((v & 2) - 1) * size;
                double vy = (((v + 1) & 2) - 1) * size;

                double rotX = vx * cosR - vy * sinR;
                double rotY = vy * cosR + vx * sinR;

                double finalY = rotX * sinXA;
                double tempX = -rotX * cosXA;

                double finalX = tempX * sinYA - rotY * cosYA;
                double finalZ = rotY * sinYA + tempX * cosYA;

                builder.vertex(matrix, (float) (sx + finalX), (float) (sy + finalY), (float) (sz + finalZ))
                        .color(starBrightness, starBrightness, starBrightness, alpha)
                        .endVertex();
            }
        }

        BufferUploader.drawWithShader(builder.end());

        poseStack.popPose();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        fogSetup.run();
    }

    /**
     * Pre-compute star positions once. Stored as arrays of
     * {unitX, unitY, unitZ, size, sinRotation, cosRotation}.
     */
    @Unique
    private void beyond_beta_tweaks$buildStarData() {
        java.util.Random random = new java.util.Random(10842L);
        java.util.List<float[]> stars = new java.util.ArrayList<>();

        int starCount = 1500;

        for (int i = 0; i < starCount; i++) {
            double x = random.nextFloat() * 2.0f - 1.0f;
            double y = random.nextFloat() * 2.0f - 1.0f;
            double z = random.nextFloat() * 2.0f - 1.0f;
            double size = ModConfig.endDimensionBaseStarSize + random.nextFloat() * ModConfig.endDimensionStarSizeVariation;
            double distSq = x * x + y * y + z * z;

            if (distSq < 1.0 && distSq > 0.01) {
                double dist = 1.0 / Math.sqrt(distSq);
                x *= dist;
                y *= dist;
                z *= dist;

                double rotation = random.nextDouble() * Math.PI * 2.0;

                stars.add(new float[]{
                        (float) x, (float) y, (float) z,
                        (float) size,
                        (float) Math.sin(rotation), (float) Math.cos(rotation)
                });
            }
        }

        endStarData = stars.toArray(new float[0][]);
    }

    // ─── FOG COLOR ──────────────────────────────────────────────────────

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void beyond_beta_tweaks$setEndFog(PoseStack poseStack, float partialTick, long finishNanoTime,
                                  boolean renderBlockOutline, Camera camera,
                                  GameRenderer gameRenderer, LightTexture lightTexture,
                                  Matrix4f projectionMatrix, CallbackInfo ci) {
        if (this.level != null && this.level.dimension() == Level.END) {
            RenderSystem.setShaderFogColor(EndSkyColors.FOG_R, EndSkyColors.FOG_G, EndSkyColors.FOG_B, 1.0f);
        }
    }

}
