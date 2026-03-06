package com.minhcraft.beyondbetatweaks.util;

import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
import com.minhcraft.beyondbetatweaks.interfaces.EndFlashAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

// End flash backport code from https://github.com/Smallinger/Copper-Age-Backport by [Smallinger](https://github.com/Smallinger)
public class EndFlashRenderer {

    private static final ResourceLocation END_FLASH_LOCATION =
            new ResourceLocation(BeyondBetaTweaks.MOD_ID, "textures/environment/end_flash.png");
    private static final float END_FLASH_HEIGHT = 100.0F;
    private static final float END_FLASH_SCALE = 60.0F;

    public static void render(PoseStack poseStack) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;

        EndFlashState endFlashState = EndFlashAccessor.get(level);
        if (endFlashState == null) return;

        float partialTick = mc.getFrameTime();
        float intensity = endFlashState.getIntensity(partialTick);

        if (intensity <= 0.0001F) return;
        if (mc.options.hideLightningFlash().get()) return;
        if (mc.gui.getBossOverlay().shouldCreateWorldFog()) return;

        float xAngle = endFlashState.getXAngle();
        float yAngle = endFlashState.getYAngle();

        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yAngle));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F - xAngle));
        poseStack.translate(0.0F, END_FLASH_HEIGHT, 0.0F);
        poseStack.scale(END_FLASH_SCALE, 1.0F, END_FLASH_SCALE);

        Matrix4f matrix4f = poseStack.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 1, 1, 0);
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, END_FLASH_LOCATION);

        int colorValue = (int) (intensity * 255.0F);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        builder.vertex(matrix4f, -1.0F, 0.0F, -1.0F).uv(0.0F, 0.0F).color(colorValue, colorValue, colorValue, colorValue).endVertex();
        builder.vertex(matrix4f, 1.0F, 0.0F, -1.0F).uv(1.0F, 0.0F).color(colorValue, colorValue, colorValue, colorValue).endVertex();
        builder.vertex(matrix4f, 1.0F, 0.0F, 1.0F).uv(1.0F, 1.0F).color(colorValue, colorValue, colorValue, colorValue).endVertex();
        builder.vertex(matrix4f, -1.0F, 0.0F, 1.0F).uv(0.0F, 1.0F).color(colorValue, colorValue, colorValue, colorValue).endVertex();
        BufferUploader.drawWithShader(builder.end());

        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();

        poseStack.popPose();
    }
}