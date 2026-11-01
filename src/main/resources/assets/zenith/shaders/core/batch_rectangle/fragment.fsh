#version 330

#moj_import <zenith:common.glsl>

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};
layout(std140) uniform ZenithData {
    vec4 Radius;
    float Smoothness;
};

in vec2 fragRectCoord;
in vec2 fragSize;
in vec4 FragColor;

out vec4 OutColor;

void main() {
    float alpha = ralpha(fragSize, fragRectCoord, Radius, Smoothness);
    vec4 finalColor = vec4(FragColor.rgb, FragColor.a * alpha);

    if (finalColor.a == 0.0) {
        discard;
    }

    OutColor = finalColor * ColorModulator;
}
