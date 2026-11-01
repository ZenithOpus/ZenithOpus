#version 330

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};
layout(std140) uniform ZenithData {
    vec2 Size;
    vec2 Smoothness;
    float Thickness;
    float ArcDegrees;
    float CapRoundness;
};

in vec2 FragCoord;
in vec4 FragColor; // x = inner AA, y = outer AA // 0..360 // 0..1

out vec4 OutColor;

const float PI = 3.14159265358979323846;
const float TAU = 6.28318530717958647692;
const float START_ANGLE = PI * 0.5; // pi/2 = 0deg

float clockwiseAngleFromTop(vec2 p) {
    float angle = atan(-p.y, p.x); // convert to math coordinates (Y-up)
    return mod(START_ANGLE - angle, TAU);
}

vec2 angleToDirScreen(float angle) {
    return vec2(cos(angle), -sin(angle)); // back to screen coordinates (Y-down)
}

void main() {
    vec2 center = Size * 0.5;
    vec2 p = FragCoord * Size - center;

    float innerAA = max(0.0001, Smoothness.x);
    float outerAA = max(0.0001, Smoothness.y);
    float capAA = max(innerAA, outerAA);

    float safeThickness = max(0.001, Thickness);
    float outerRadius = max(0.0, min(Size.x, Size.y) * 0.5 - 1.0 - outerAA);
    float innerRadius = max(0.0, outerRadius - safeThickness);
    float centerRadius = 0.5 * (outerRadius + innerRadius);
    float halfThickness = 0.5 * (outerRadius - innerRadius);

    float r = length(p);
    float innerMask = smoothstep(innerRadius - innerAA, innerRadius + innerAA, r);
    float outerMask = 1.0 - smoothstep(outerRadius - outerAA, outerRadius + outerAA, r);
    float ringAlpha = innerMask * outerMask;

    float arc = clamp(radians(ArcDegrees), 0.0, TAU);
    float alpha = 0.0;

    if (arc >= TAU - 0.0001) {
        alpha = ringAlpha;
    } else if (arc > 0.0001 && halfThickness > 0.0) {
        float rel = clockwiseAngleFromTop(p);
        float bodyAlpha = ringAlpha * float(rel <= arc);

        float capRadius = halfThickness * clamp(CapRoundness, 0.0, 1.0);
        float capAlpha = 0.0;
        if (capRadius > 0.0001) {
            vec2 startCenter = angleToDirScreen(START_ANGLE) * centerRadius;
            vec2 endCenter = angleToDirScreen(START_ANGLE - arc) * centerRadius;

            float startCap = 1.0 - smoothstep(capRadius - capAA, capRadius + capAA, length(p - startCenter));
            float endCap = 1.0 - smoothstep(capRadius - capAA, capRadius + capAA, length(p - endCenter));
            capAlpha = max(startCap, endCap);
        }

        alpha = max(bodyAlpha, capAlpha);
    }

    vec4 finalColor = vec4(FragColor.rgb, FragColor.a * alpha);
    if (finalColor.a < 0.001) {
        discard;
    }

    OutColor = finalColor * ColorModulator;
}
