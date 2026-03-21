#version 330 core

#import <sodium:include/fog.glsl>

in vec4 v_Color;
in vec2 v_TexCoord;
in float v_FragDistance;
in float v_RawAlpha;

in float v_MaterialMipBias;
in float v_MaterialAlphaCutoff;

uniform sampler2D u_BlockTex;

uniform vec4 u_FogColor;
uniform float u_FogStart;
uniform float u_FogEnd;

out vec4 fragColor;

vec3 overlay(vec3 base, vec3 blend) {
    return vec3(
        base.r < 0.5 ? 2.0 * base.r * blend.r : 1.0 - 2.0 * (1.0 - base.r) * (1.0 - blend.r),
        base.g < 0.5 ? 2.0 * base.g * blend.g : 1.0 - 2.0 * (1.0 - base.g) * (1.0 - blend.g),
        base.b < 0.5 ? 2.0 * base.b * blend.b : 1.0 - 2.0 * (1.0 - base.b) * (1.0 - blend.b)
    );
}

void main() {
    vec4 diffuseColor = texture(u_BlockTex, v_TexCoord, v_MaterialMipBias);

#ifdef USE_FRAGMENT_DISCARD
    if (diffuseColor.a < v_MaterialAlphaCutoff) {
        discard;
    }
#endif

    // Decode the overlay factor from raw vertex alpha.
    //
    // Encoding (from mixin):
    //   newAlpha = originalAlpha * (1.0 - factor * 0.5)
    //
    // So:  factor = 0.0 -> alpha unchanged (1.0 for normal blocks)
    //      factor = 0.5 -> alpha * 0.75
    //      factor = 1.0 -> alpha * 0.5
    //
    // To recover the factor:
    //   ratio = v_RawAlpha / expectedAlpha
    //   factor = (1.0 - ratio) / 0.5 = 2.0 * (1.0 - ratio)
    //
    // For blocks with original alpha = 1.0 (which is always the case
    // for block vertices), expectedAlpha = 1.0, so:
    //   factor = 2.0 * (1.0 - v_RawAlpha)
    //
    // v_RawAlpha = 1.0 -> factor = 0.0 (vanilla)
    // v_RawAlpha = 0.75 -> factor = 0.5 (half blend)
    // v_RawAlpha = 0.5 -> factor = 1.0 (full overlay)

    float overlayFactor = clamp(2.0 * (1.0 - v_RawAlpha), 0.0, 1.0);

#ifdef USE_VANILLA_COLOR_FORMAT
    if (overlayFactor > 0.001) {
        vec3 tintLight = v_Color.rgb;

        float lum = dot(tintLight, vec3(0.2126, 0.7152, 0.0722));
        // can tune this variable for a different tinting result
        float targetLum = 0.5;
        vec3 tintNorm = tintLight * (targetLum / max(lum, 0.001));
        tintNorm = clamp(tintNorm, 0.0, 1.0);

        // can tune this variable for a different tinting result
        float texStrength = 0.82;
        vec3 compressed = mix(vec3(0.5), diffuseColor.rgb, texStrength);
        vec3 overlayResult = overlay(compressed, tintNorm);

        float brightnessScale = lum / targetLum;
        vec3 overlayColor = overlayResult * brightnessScale;

        // Vanilla multiply result
        vec3 multiplyColor = diffuseColor.rgb * v_Color.rgb;

        // Lerp between multiply and overlay based on factor
        diffuseColor.rgb = mix(multiplyColor, overlayColor, overlayFactor);
    } else {
        diffuseColor *= v_Color;
    }
#else
    if (overlayFactor > 0.001) {
        vec3 tintLight = v_Color.rgb;
        float aoShade = v_Color.a;

        float lum = dot(tintLight, vec3(0.2126, 0.7152, 0.0722));
        // can tune this variable for a different tinting result
        float targetLum = 0.5;
        vec3 tintNorm = tintLight * (targetLum / max(lum, 0.001));
        tintNorm = clamp(tintNorm, 0.0, 1.0);

        // can tune this variable for a different tinting result
        float texStrength = 0.82;
        vec3 compressed = mix(vec3(0.5), diffuseColor.rgb, texStrength);
        vec3 overlayResult = overlay(compressed, tintNorm);

        float brightnessScale = lum / targetLum;
        vec3 overlayColor = overlayResult * brightnessScale * aoShade;

        // Normal Embeddium multiply result
        vec3 multiplyColor = diffuseColor.rgb * v_Color.rgb * aoShade;

        // Lerp between multiply and overlay based on factor
        diffuseColor.rgb = mix(multiplyColor, overlayColor, overlayFactor);
    } else {
        diffuseColor.rgb *= v_Color.rgb;
        diffuseColor.rgb *= v_Color.a;
    }
#endif

    fragColor = _linearFog(diffuseColor, v_FragDistance, u_FogColor, u_FogStart, u_FogEnd);
}
