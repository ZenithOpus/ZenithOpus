#version 330

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};
layout(std140) uniform ZenithPost {
    vec4 FireColor;
    vec4 EmberColor;
    float Time;
};

uniform sampler2D InSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

const float TAU = 6.28318530718;

float random(vec2 st) {
    return fract(sin(dot(st, vec2(12.9898, 78.233))) * 43758.5453123);
}

float noise(vec2 st) {
    vec2 i = floor(st);
    vec2 f = fract(st);
    float a = random(i);
    float b = random(i + vec2(1.0, 0.0));
    float c = random(i + vec2(0.0, 1.0));
    float d = random(i + vec2(1.0, 1.0));
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

float fbm(vec2 st) {
    float value = 0.0;
    float amp = 0.55;
    mat2 rot = mat2(cos(0.6), sin(0.6), -sin(0.6), cos(0.6));
    for (int i = 0; i < 5; i++) {
        value += amp * noise(st);
        st = rot * st * 2.05 + vec2(50.0);
        amp *= 0.5;
    }
    return value;
}

float sampleMask(vec2 uv) {
    if (uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0) return 0.0;
    vec4 c = texture(InSampler, uv);
    return clamp(max(c.a, max(c.r, max(c.g, c.b))), 0.0, 1.0);
}

float haloGlow(vec2 uv) {
    float glow = 0.0;
    for (int i = 1; i <= 8; i++) {
        float r = float(i) * 1.8;
        float ring = 0.0;
        for (int j = 0; j < 5; j++) {
            float a = TAU * float(j) / 5.0 + float(i) * 0.7;
            ring += sampleMask(uv + vec2(cos(a), sin(a)) * oneTexel * r);
        }
        glow = max(glow, ring / 5.0 * (1.0 - float(i) / 9.0));
    }
    return glow;
}

void main() {
    float center = sampleMask(texCoord);
    float glow = haloGlow(texCoord);

    float outline = 0.0;
    for (int d = 0; d < 8; d++) {
        float a = TAU * (float(d) / 8.0);
        vec2 dir = vec2(cos(a), sin(a));
        outline = max(outline, max(sampleMask(texCoord + dir * oneTexel * 2.0) - center, 0.0));
    }

    if (center <= 0.001 && glow <= 0.001 && outline <= 0.001) {
        fragColor = vec4(0.0);
        return;
    }

    float band = glow * (1.0 - center);
    float arcN = fbm(texCoord * vec2(120.0, 110.0) + vec2(Time * 5.0, -Time * 3.5));
    float arc = smoothstep(0.62, 0.95, arcN);
    float bolt = pow(arc, 2.0) * band;
    float flicker = 0.55 + 0.45 * sin(Time * 38.0 + arcN * 30.0 + texCoord.y * 120.0);
    bolt *= flicker;

    float coreEdge = outline * (0.7 + 0.3 * flicker);
    vec3 col = mix(FireColor.rgb, EmberColor.rgb, smoothstep(0.2, 1.0, bolt));
    col += EmberColor.rgb * coreEdge;

    float bodyAlpha = center * 0.32 * FireColor.a;
    float boltAlpha = clamp(bolt, 0.0, 1.0) * FireColor.a * 1.5;
    float edgeAlpha = coreEdge * EmberColor.a;
    float finalAlpha = clamp(max(bodyAlpha, max(boltAlpha, edgeAlpha)), 0.0, 1.0);

    fragColor = vec4(clamp(col, 0.0, 1.4), finalAlpha);
}
