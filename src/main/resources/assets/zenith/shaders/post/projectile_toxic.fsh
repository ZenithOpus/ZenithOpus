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
const float GAS_HEIGHT_PX = 46.0;
const int GAS_STEPS = 18;

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

void main() {
    float center = sampleMask(texCoord);

    vec2 resolution = 1.0 / oneTexel;
    vec2 fragPx = gl_FragCoord.xy;

    float gasAccum = 0.0;
    float closestBelow = 1.0;
    for (int i = 1; i <= GAS_STEPS; i++) {
        float k = float(i) / float(GAS_STEPS);
        float distPx = k * GAS_HEIGHT_PX;
        float sway = (fbm(vec2(fragPx.x * 0.03 + Time * 0.6, distPx * 0.06)) - 0.5) * distPx * 0.5;
        vec2 sampleUv = vec2(fragPx.x + sway, fragPx.y - distPx) * oneTexel;
        float s = sampleMask(sampleUv);
        if (s > 0.05) {
            float fade = 1.0 - k;
            gasAccum = max(gasAccum, s * fade * fade);
            if (k < closestBelow) closestBelow = k;
        }
    }

    float outline = 0.0;
    for (int d = 0; d < 8; d++) {
        float a = TAU * (float(d) / 8.0);
        vec2 dir = vec2(cos(a), sin(a));
        outline = max(outline, max(sampleMask(texCoord + dir * oneTexel * 2.0) - center, 0.0));
    }

    if (center <= 0.001 && gasAccum <= 0.001 && outline <= 0.001) {
        fragColor = vec4(0.0);
        return;
    }

    vec2 p = fragPx * 8.0 / resolution.yy;
    float bubble = smoothstep(0.72, 1.0, fbm(p * 4.0 - vec2(0.0, Time * 2.2)));
    float gasField = gasAccum * pow(1.0 - closestBelow, 1.3);
    gasField *= 0.7 + bubble * 0.6;
    gasField *= 0.8 + 0.2 * sin(Time * 5.0 + p.x * 6.0);

    float hotCore = smoothstep(0.4, 1.0, gasField);
    vec3 col = mix(FireColor.rgb, EmberColor.rgb, hotCore);
    col += EmberColor.rgb * bubble * gasField * 1.2;

    float bodyAlpha = center * (0.4 + bubble * 0.4) * FireColor.a;
    float gasAlpha = clamp(gasField, 0.0, 1.0) * FireColor.a * 1.3;
    float edgeAlpha = outline * (0.6 + hotCore * 0.5) * EmberColor.a;
    float finalAlpha = clamp(max(bodyAlpha, max(gasAlpha, edgeAlpha)), 0.0, 1.0);

    fragColor = vec4(clamp(col, 0.0, 1.3), finalAlpha);
}
