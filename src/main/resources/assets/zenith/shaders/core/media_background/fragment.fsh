#version 330

#moj_import <zenith:common.glsl>

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};
layout(std140) uniform ZenithData {
    vec2 Resolution;
    float Time;
    vec4 ColorA4;
    vec4 ColorB4;
    vec4 ColorC4;
    vec4 ColorD4;
    float Energy;
};
#define ColorA ColorA4.xyz
#define ColorB ColorB4.xyz
#define ColorC ColorC4.xyz
#define ColorD ColorD4.xyz

in vec2 FragCoord;
in vec4 FragColor;

out vec4 OutColor;

float hash21(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash21(i), hash21(i + vec2(1.0, 0.0)), u.x),
               mix(hash21(i + vec2(0.0, 1.0)), hash21(i + vec2(1.0, 1.0)), u.x), u.y);
}

float fbm(vec2 p) {
    float sum = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 4; i++) {
        sum += amplitude * noise(p);
        p = p * 2.03 + vec2(11.7, 5.3);
        amplitude *= 0.5;
    }
    return sum;
}

vec3 blob(vec2 p, vec2 center, float radius, vec3 color, inout float weight) {
    float d = length(p - center);
    float w = exp(-d * d / (radius * radius));
    weight += w;
    return color * w;
}

void main() {
    vec2 uv = FragCoord;
    float aspect = Resolution.x / max(1.0, Resolution.y);
    vec2 p = vec2((uv.x - 0.5) * aspect, uv.y - 0.5);
    float t = Time * 0.07;

    vec2 warp = vec2(fbm(p * 1.4 + vec2(t * 0.7, -t * 0.4)), fbm(p * 1.4 + vec2(-t * 0.5, t * 0.6) + 7.1)) - 0.5;
    vec2 q = p + warp * 0.55;

    float weight = 0.0;
    vec3 color = vec3(0.0);
    color += blob(q, vec2(-0.55 * aspect * 0.5 + 0.25 * sin(t * 0.9), -0.2 + 0.22 * cos(t * 0.7)), 0.55, ColorA, weight);
    color += blob(q, vec2(0.45 * aspect * 0.5 + 0.3 * cos(t * 0.8 + 1.3), 0.25 + 0.2 * sin(t * 1.1)), 0.6, ColorB, weight);
    color += blob(q, vec2(0.1 + 0.35 * sin(t * 0.6 + 2.1), -0.35 + 0.18 * sin(t * 0.9 + 0.4)), 0.5, ColorC, weight);
    color += blob(q, vec2(-0.2 + 0.3 * cos(t * 0.75 + 4.0), 0.35 + 0.2 * cos(t * 0.65 + 1.7)), 0.55, ColorD, weight);
    color += blob(q, vec2(0.6 * sin(t * 0.5 + 3.3), 0.45 * cos(t * 0.55 + 2.6)), 0.4, mix(ColorA, ColorB, 0.5), weight);
    color /= max(weight, 1e-3);

    float swirl = fbm(q * 2.2 + vec2(t * 0.9, t * 0.3));
    color *= 0.82 + 0.36 * swirl;
    float luma = dot(color, vec3(0.299, 0.587, 0.114));
    color = mix(vec3(luma), color, 1.3);

    float sheen = pow(max(0.0, fbm(q * 3.1 - vec2(t * 1.2, -t * 0.8)) - 0.45), 2.0) * 1.6;
    color += sheen * mix(ColorB, vec3(1.0), 0.4) * (0.18 + 0.12 * Energy);

    float vignette = smoothstep(1.25, 0.2, length(p * vec2(0.85, 1.1)));
    color *= 0.68 + 0.32 * vignette;
    color *= 0.6;

    float grain = (hash21(floor(uv * Resolution * 2.0)) - 0.5) * 0.016;
    color += grain;

    OutColor = vec4(max(color, vec3(0.0)), 1.0) * FragColor;
}
