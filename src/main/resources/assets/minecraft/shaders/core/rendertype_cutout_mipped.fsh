#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

void main() {
    vec4 texColor = texture(Sampler0, texCoord0);
    if (texColor.a < 0.5) {
        discard;
    }

    // Extract the alpha-grass blend signal from vertex alpha.
    // Normal blocks have vertexColor.a == 1.0 (after ColorModulator).
    // Alpha-grass blocks have vertexColor.a < 0.95, where the value
    // encodes the blend factor:
    //   vertexColor.a ≈ 0.0  → full screen blend (maximum alpha-grass effect)
    //   vertexColor.a ≈ 0.5  → 50/50 mix of screen and multiply
    //   vertexColor.a >= 0.95 → normal multiply (vanilla behavior)

    float alphaSignal = vertexColor.a;
    vec3 tint = vertexColor.rgb;

    vec3 finalColor;

    if (alphaSignal >= 0.95) {
        // Normal vanilla multiply blending
        finalColor = texColor.rgb * tint;
    } else {
        // Compute the alpha-grass factor (0 to 1, where 1 = full screen blend)
        // Map alphaSignal from [0.0, 0.95] to alphaFactor [1.0, 0.0]
        float alphaFactor = 1.0 - (alphaSignal / 0.95);
        alphaFactor = clamp(alphaFactor, 0.0, 1.0);

        // Screen blend: 1 - (1 - tex) * (1 - tint)
        // This preserves bright texture pixels while still tinting dark areas.
        // White pixels stay mostly white, dark pixels get the biome color.
        vec3 screenBlend = vec3(1.0) - (vec3(1.0) - texColor.rgb) * (vec3(1.0) - tint);

        // Normal multiply for comparison
        vec3 multiplyBlend = texColor.rgb * tint;

        // Lerp between multiply and screen based on alphaFactor
        finalColor = mix(multiplyBlend, screenBlend, alphaFactor);
    }

    // Apply ColorModulator (handles things like world border tinting)
    // Note: we only apply the RGB part since we've repurposed alpha
    finalColor *= ColorModulator.rgb;

    fragColor = vec4(finalColor, texColor.a) * vec4(1.0, 1.0, 1.0, ColorModulator.a);

    // Apply fog
    fragColor = linear_fog(fragColor, vertexDistance, FogStart, FogEnd, FogColor);
}
