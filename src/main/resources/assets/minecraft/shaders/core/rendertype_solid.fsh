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

    float alphaSignal = vertexColor.a;
    vec3 tint = vertexColor.rgb;

    vec3 finalColor;

    if (alphaSignal >= 0.95) {
        finalColor = texColor.rgb * tint;
    } else {
        float alphaFactor = 1.0 - (alphaSignal / 0.95);
        alphaFactor = clamp(alphaFactor, 0.0, 1.0);

        vec3 screenBlend = vec3(1.0) - (vec3(1.0) - texColor.rgb) * (vec3(1.0) - tint);
        vec3 multiplyBlend = texColor.rgb * tint;

        finalColor = mix(multiplyBlend, screenBlend, alphaFactor);
    }

    finalColor *= ColorModulator.rgb;
    fragColor = vec4(finalColor, texColor.a) * vec4(1.0, 1.0, 1.0, ColorModulator.a);
    fragColor = linear_fog(fragColor, vertexDistance, FogStart, FogEnd, FogColor);
}
