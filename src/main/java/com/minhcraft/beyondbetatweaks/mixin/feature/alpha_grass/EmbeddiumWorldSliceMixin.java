package com.minhcraft.beyondbetatweaks.mixin.feature.alpha_grass;

import com.minhcraft.beyondbetatweaks.util.AlphaGrassHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Embeddium mixin targeting WorldSlice.getBlockTint.
 *
 * For the shader approach, we DON'T modify the tint color — the shader
 * handles the blending. Instead, we need to encode the alpha-grass signal
 * into the vertex alpha.
 *
 * However, getBlockTint returns a 24-bit RGB color (no alpha channel used
 * by the caller). The alpha gets added later when packing into ABGR.
 *
 * STRATEGY: We encode the alpha factor in the top byte of the returned
 * color int. Embeddium's color blender reads the bottom 24 bits for RGB,
 * and the top byte is normally 0x00. If we set it to a special marker
 * value, downstream code can detect it.
 *
 * BUT — this is fragile and depends on Embeddium ignoring the top byte.
 *
 * ALTERNATIVE STRATEGY: Use a ThreadLocal to communicate the alpha factor
 * from getBlockTint to the vertex writer, similar to vanilla.
 *
 * PRACTICAL APPROACH: Since Embeddium's BlockRenderer writes vertex colors
 * as ABGR ints with alpha = 0xFF, we target the final vertex color write.
 * In Embeddium, this happens in BlockRenderer.renderQuad where it calls
 * something like:
 *   quad.setColor(i, ColorABGR.mul(colors[i], brightness));
 *
 * We'll store the alpha factor per-block in a ThreadLocal, and mixin into
 * the method that writes the final vertex colors to modify alpha.
 *
 * FOR INITIAL TESTING: We use the getBlockTint intercept to store the
 * alpha factor, and a separate mixin on the vertex writer to apply it.
 * If getBlockTint isn't called for a block, it means FlatColorBlender
 * goes through BlockColors.getColor instead, which is handled by
 * BlockColorsMixin.
 */
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.world.WorldSlice", remap = false)
public abstract class EmbeddiumWorldSliceMixin {


    @Inject(
            method = "getBlockTint",
            at = @At("RETURN"),
            remap = true,
            cancellable = true)
    private void alphaGrass$captureAlphaFactor(BlockPos pos, ColorResolver resolver,
                                               CallbackInfoReturnable<Integer> cir) {
        // TODO: proper biome/block checks
        // For now, apply to all biome-tinted blocks going through this path
        float factor = 1.0f;
        AlphaGrassHelper.EMBEDDIUM_ALPHA_FACTOR.set(factor);

        // Also modify the color to encode alpha in the top byte.
        // Embeddium's FlatColorBlender reads this as:
        //   ColorARGB.toABGR(colorizer.getColor(...))
        // If we set the top byte here, it flows into the ABGR alpha.
        int color = cir.getReturnValue();

        // Encode: factor=1.0 → alpha byte = 0 (max screen blend)
        //         factor=0.0 → alpha byte = 242 (vanilla threshold)
        int encodedAlpha = (int) ((1.0f - factor) * 0.95f * 255.0f);
        encodedAlpha = Math.max(0, Math.min(242, encodedAlpha));

        // Pack into top byte of color int
        // Note: getBlockTint normally returns 0x00RRGGBB.
        // We set it to 0xAARRGGBB where AA is our encoded alpha.
        int modifiedColor = (encodedAlpha << 24) | (color & 0x00FFFFFF);
        cir.setReturnValue(modifiedColor);
    }
}
