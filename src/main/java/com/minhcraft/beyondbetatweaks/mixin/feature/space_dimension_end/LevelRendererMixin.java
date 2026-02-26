package com.minhcraft.beyondbetatweaks.mixin.feature.space_dimension_end;


import com.minhcraft.beyondbetatweaks.config.ModConfig;
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

    @Unique private VertexBuffer endStarBuffer;
    @Unique private boolean endStarsBuilt = false;

    @Unique private static final float END_SKY_R;
    @Unique private static final float END_SKY_G;
    @Unique private static final float END_SKY_B;

    @Unique private static final float END_FOG_R;
    @Unique private static final float END_FOG_G;
    @Unique private static final float END_FOG_B;

    static {
        int skyColor = Integer.parseInt(ModConfig.endDimensionSkyColor.replace("#", ""), 16);
        END_SKY_R = ((skyColor >> 16) & 0xFF) / 255.0f;
        END_SKY_G = ((skyColor >> 8) & 0xFF) / 255.0f;
        END_SKY_B = (skyColor & 0xFF) / 255.0f;

        int fogColor = Integer.parseInt(ModConfig.endDimensionFogColor.replace("#", ""), 16);
        END_FOG_R = ((fogColor >> 16) & 0xFF) / 255.0f;
        END_FOG_G = ((fogColor >> 8) & 0xFF) / 255.0f;
        END_FOG_B = (fogColor & 0xFF) / 255.0f;
    }


    // ─── SKY RENDERING ──────────────────────────────────────────────────

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void beyond_beta_tweaks$renderSky(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick,
                                  Camera camera, boolean isFoggy, Runnable fogSetup, CallbackInfo ci) {
        if (this.level == null || this.level.dimension() != Level.END) return;
        if (camera.getFluidInCamera() != FogType.NONE) return;

        ci.cancel();

        // Run fog setup first like vanilla does
        fogSetup.run();

        renderEndSky(poseStack, projectionMatrix, partialTick, camera, fogSetup);
    }

    @Unique
    private void renderEndSky(PoseStack poseStack, Matrix4f projectionMatrix,
                              float partialTick, Camera camera, Runnable fogSetup) {
        RenderSystem.depthMask(false);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        Matrix4f matrix = poseStack.last().pose();

        // ── Step 1: Fill the entire sky with solid color background ──
        // FLIPPED: upper = fog color, lower = sky color

        // Lower hemisphere first (drawn behind): sky color
        // Extends up to y=+2 (slightly above horizon)
        RenderSystem.setShaderColor(END_SKY_R, END_SKY_G, END_SKY_B, 1.0f);
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
        RenderSystem.setShaderColor(END_FOG_R, END_FOG_G, END_FOG_B, 1.0f);

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
        renderEndStars(poseStack, projectionMatrix, partialTick, fogSetup);

        // ── Step 3: Horizon gradient ──
        // Drawn AFTER stars so it overlays on top, naturally hiding stars near the horizon
        beyond_beta_tweaks$renderHorizonGradient(poseStack);

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

                // Overlay SKY color (lighter) instead of fog color
                builder.vertex(matrix, cosA * rBot, yBot, sinA * rBot)
                        .color(END_SKY_R, END_SKY_G, END_SKY_B, alphaBot).endVertex();
                builder.vertex(matrix, cosA * rTop, yTop, sinA * rTop)
                        .color(END_SKY_R, END_SKY_G, END_SKY_B, alphaTop).endVertex();
            }

            BufferUploader.drawWithShader(builder.end());
        }
    }

    // ─── STARS ───────────────────────────────────────────────────────────

    @Unique
    private void renderEndStars(PoseStack poseStack, Matrix4f projectionMatrix,
                                float partialTick, Runnable fogSetup) {
        if (!endStarsBuilt) {
            buildEndStars();
            endStarsBuilt = true;
        }
        if (endStarBuffer == null) return;

        // Disable fog so stars render crisply
        FogRenderer.setupNoFog();

        float starBrightness = 0.8f;
        RenderSystem.setShaderColor(starBrightness, starBrightness, starBrightness, starBrightness);
        RenderSystem.setShader(GameRenderer::getPositionShader);

        poseStack.pushPose();
        float time = (this.ticks + partialTick) * 0.001f;
        poseStack.mulPose(Axis.YP.rotationDegrees(time * 5.0f)); // horizontal star rotation
//        poseStack.mulPose(Axis.XP.rotationDegrees(time * 5.0f)); // vertical star rotation
//        poseStack.mulPose(Axis.ZP.rotationDegrees(time * 2.0f)); // tilt star rotation

        this.endStarBuffer.bind();
        this.endStarBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, RenderSystem.getShader());
        VertexBuffer.unbind();

        poseStack.popPose();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Restore fog
        fogSetup.run();
    }

    @Unique
    private void buildEndStars() {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionShader);

        if (this.endStarBuffer != null) {
            this.endStarBuffer.close();
        }

        this.endStarBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        BufferBuilder.RenderedBuffer renderedBuffer = buildStarGeometry(bufferBuilder);
        this.endStarBuffer.bind();
        this.endStarBuffer.upload(renderedBuffer);
        VertexBuffer.unbind();
    }

    @Unique
    private BufferBuilder.RenderedBuffer buildStarGeometry(BufferBuilder builder) {
        java.util.Random random = new java.util.Random(10842L);
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        int starCount = 1500;

        for (int i = 0; i < starCount; i++) {
            double x = random.nextFloat() * 2.0f - 1.0f;
            double y = random.nextFloat() * 2.0f - 1.0f;
            double z = random.nextFloat() * 2.0f - 1.0f;
            double size = ModConfig.endDimensionStarSize + random.nextFloat() * 0.1f;
            double distSq = x * x + y * y + z * z;

            if (distSq < 1.0 && distSq > 0.01) {
                double dist = 1.0 / Math.sqrt(distSq);
                x *= dist;
                y *= dist;
                z *= dist;

                // Skip stars below -10 degrees elevation
                // y on the unit sphere = sin(elevation), sin(-10°) ≈ -0.174
                if (y < -0.174) continue;

                double farDist = 100.0;
                double sx = x * farDist;
                double sy = y * farDist;
                double sz = z * farDist;

                double yAngle = Math.atan2(x, z);
                double sinY = Math.sin(yAngle);
                double cosY = Math.cos(yAngle);

                double xzDist = Math.sqrt(x * x + z * z);
                double xAngle = Math.atan2(xzDist, y);
                double sinX = Math.sin(xAngle);
                double cosX = Math.cos(xAngle);

                double rotation = random.nextDouble() * Math.PI * 2.0;
                double sinR = Math.sin(rotation);
                double cosR = Math.cos(rotation);

                for (int v = 0; v < 4; v++) {
                    double vx = ((v & 2) - 1) * size;
                    double vy = (((v + 1) & 2) - 1) * size;

                    double rotX = vx * cosR - vy * sinR;
                    double rotY = vy * cosR + vx * sinR;

                    double finalY = rotX * sinX;
                    double tempX = -rotX * cosX;

                    double finalX = tempX * sinY - rotY * cosY;
                    double finalZ = rotY * sinY + tempX * cosY;

                    builder.vertex((float) (sx + finalX), (float) (sy + finalY), (float) (sz + finalZ)).endVertex();
                }
            }
        }

        return builder.end();
    }

    // ─── FOG COLOR ──────────────────────────────────────────────────────

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void beyond_beta_tweaks$setEndFog(PoseStack poseStack, float partialTick, long finishNanoTime,
                                  boolean renderBlockOutline, Camera camera,
                                  GameRenderer gameRenderer, LightTexture lightTexture,
                                  Matrix4f projectionMatrix, CallbackInfo ci) {
        if (this.level != null && this.level.dimension() == Level.END) {
            RenderSystem.setShaderFogColor(END_FOG_R, END_FOG_G, END_FOG_B, 1.0f);
        }
    }

}
