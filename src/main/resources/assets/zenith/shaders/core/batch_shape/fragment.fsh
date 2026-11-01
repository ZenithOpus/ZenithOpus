#version 330

#moj_import <zenith:common.glsl>

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};
layout(std140) uniform ZenithData {
    vec2 Smoothness;
};

in vec2 fragRectCoord;
in vec2 fragSize;
in vec4 fragRadius;
in vec4 FragColor;
in float fragThickness;
in float fragMode; // x — внутреннее сглаживание, y — внешнее

out vec4 OutColor;

/** Контур: та же пара smoothstep, что была в шейдерах border и corner. */
float outlineAlpha(float distance) {
    float inner = smoothstep(1.0 - fragThickness - Smoothness.x - Smoothness.y,
                             1.0 - fragThickness - Smoothness.y, distance);
    return inner * (1.0 - smoothstep(1.0 - Smoothness.y, 1.0, distance));
}

const float PI = 3.14159265358979323846;
const float TAU = 6.28318530717958647692;
const float ARC_START = PI * 0.5;

float arcAngle(vec2 p) {
    return mod(ARC_START - atan(-p.y, p.x), TAU);
}

vec2 arcDir(float angle) {
    return vec2(cos(angle), -sin(angle));
}

float arcAlpha(vec2 p) {
    float innerAA = 0.4;
    float outerAA = 0.4;
    float safeThickness = max(0.001, fragThickness);
    float outerRadius = max(0.0, min(fragSize.x, fragSize.y) * 0.5 - 1.0 - outerAA);
    float innerRadius = max(0.0, outerRadius - safeThickness);
    float centerRadius = 0.5 * (outerRadius + innerRadius);
    float halfThickness = 0.5 * (outerRadius - innerRadius);

    float r = length(p);
    float ring = smoothstep(innerRadius - innerAA, innerRadius + innerAA, r)
            * (1.0 - smoothstep(outerRadius - outerAA, outerRadius + outerAA, r));

    float arc = clamp(radians(fragRadius.x), 0.0, TAU);
    if (arc >= TAU - 0.0001) {
        return ring;
    }
    if (arc <= 0.0001 || halfThickness <= 0.0) {
        return 0.0;
    }

    float body = ring * float(arcAngle(p) <= arc);
    float capRadius = halfThickness * clamp(fragRadius.y, 0.0, 1.0);
    float caps = 0.0;
    if (capRadius > 0.0001) {
        float capAA = max(innerAA, outerAA);
        vec2 startCenter = arcDir(ARC_START) * centerRadius;
        vec2 endCenter = arcDir(ARC_START - arc) * centerRadius;
        caps = max(1.0 - smoothstep(capRadius - capAA, capRadius + capAA, length(p - startCenter)),
                   1.0 - smoothstep(capRadius - capAA, capRadius + capAA, length(p - endCenter)));
    }
    return max(body, caps);
}

void main() {
    vec2 center = fragSize * 0.5;
    vec2 pixel = fragRectCoord * fragSize;
    float alpha;

    if (fragMode > 6.5) {
        alpha = arcAlpha(pixel - center);
    } else if (fragMode < 0.5) {
        // Скруглённая заливка.
        alpha = ralpha(fragSize, fragRectCoord, fragRadius, Smoothness.x);
    } else if (fragMode < 1.5) {
        // Прямоугольная заливка: гнать её через формулу скругления нельзя — она размывает
        // край на пиксель, и у разделительных линий появилась бы мягкая кромка.
        alpha = 1.0;
    } else {
        if (fragMode > 2.5) {
            // Уголок: четверть контура. Номер угла — 0 верхний левый, 1 верхний правый,
            // 2 нижний левый, 3 нижний правый; порядок тот же, что был в шейдере corner.
            float corner = fragMode - 3.0;
            if (corner < 0.5) {
                if (pixel.x > center.x || pixel.y > center.y) discard;
            } else if (corner < 1.5) {
                if (pixel.x < center.x || pixel.y > center.y) discard;
            } else if (corner < 2.5) {
                if (pixel.x > center.x || pixel.y < center.y) discard;
            } else {
                if (pixel.x < center.x || pixel.y < center.y) discard;
            }
        }
        alpha = outlineAlpha(rdist(center - pixel, center - 1.0, fragRadius));
    }

    vec4 finalColor = vec4(FragColor.rgb, FragColor.a * alpha);

    if (finalColor.a == 0.0) {
        discard;
    }

    OutColor = finalColor * ColorModulator;
}
