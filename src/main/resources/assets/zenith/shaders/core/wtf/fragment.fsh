#version 330

#moj_import <zenith:common.glsl>

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};
layout(std140) uniform ZenithData {
    vec2 Size;
    vec4 Radius;
    float Smoothness;
};

in vec2 FragCoord; // normalized fragment coord relative to the primitive
in vec2 TexCoord;
in vec4 FragColor;

uniform sampler2D Sampler0;

out vec4 OutColor;

void main() {
    vec4 texColor = texture(Sampler0, TexCoord);

    vec2 center = Size * 0.5;
    float distance = roundedBoxSDF(center - (FragCoord * Size), center - 1.0, Radius);

    float alpha = 1.0 - smoothstep(1.0 - Smoothness, 1.0, distance);

    vec4 finalColor = texColor * FragColor * ColorModulator;
    finalColor.a *= alpha;

    if (finalColor.a <= 0.0) {
        discard;
    }

    OutColor = finalColor;
}