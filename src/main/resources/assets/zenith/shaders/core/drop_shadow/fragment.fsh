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
    vec2 ShapeSize;
    vec4 Radius;
    float Padding;
    float Sigma;
    float Spread;
};

in vec2 FragCoord;
in vec4 FragColor;

out vec4 OutColor;

float erfApprox(float value) {
    float signValue = value < 0.0 ? -1.0 : 1.0;
    float x = abs(value);
    float t = 1.0 / (1.0 + 0.3275911 * x);
    float polynomial = (((((1.061405429 * t - 1.453152027) * t) + 1.421413741) * t
            - 0.284496736) * t + 0.254829592) * t;
    return signValue * (1.0 - polynomial * exp(-x * x));
}

void main() {
    vec2 local = FragCoord * Size - vec2(Padding);
    vec2 centered = ShapeSize * 0.5 - local;
    float distance = roundedBoxSDF(centered, ShapeSize * 0.5, Radius);
    distance -= Spread;

    float coverage;
    if (Sigma > 0.0001) {
        coverage = 0.5 - 0.5 * erfApprox(distance / (1.41421356237 * Sigma));
    } else {
        coverage = 1.0 - smoothstep(-0.5, 0.5, distance);
    }

    vec4 finalColor = vec4(FragColor.rgb, FragColor.a * coverage) * ColorModulator;
    if (finalColor.a <= 0.0001) {
        discard;
    }
    OutColor = finalColor;
}
