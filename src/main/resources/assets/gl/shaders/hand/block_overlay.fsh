#version 150

in vec2 handUv;
out vec4 outColor;

uniform sampler2D ColorTexture;
uniform vec2 resolution;
uniform vec4 outlineColor;
uniform vec4 firstFillColor;
uniform vec4 secondFillColor;
uniform float time;
uniform float effectAlpha;
uniform int effectMode;

const float TAU = 6.28318530718;
const float OUTLINE_WIDTH = 2.6;
const int DIRECTIONS = 16;
const int OCTAVES = 6;

float random(vec2 point) {
    return fract(sin(dot(point, vec2(12.9898, 78.233))) * 43758.5453123);
}

float noise(vec2 point) {
    vec2 cell = floor(point);
    vec2 local = fract(point);
    float a = random(cell);
    float b = random(cell + vec2(1.0, 0.0));
    float c = random(cell + vec2(0.0, 1.0));
    float d = random(cell + vec2(1.0, 1.0));
    vec2 smoothLocal = local * local * (3.0 - 2.0 * local);
    return mix(a, b, smoothLocal.x)
           + (c - a) * smoothLocal.y * (1.0 - smoothLocal.x)
           + (d - b) * smoothLocal.x * smoothLocal.y;
}

float fbm(vec2 point) {
    float value = 0.0;
    float amplitude = 0.5;
    mat2 rotation = mat2(cos(0.5), sin(0.5), -sin(0.5), cos(0.5));
    for (int i = 0; i < OCTAVES; i++) {
        value += amplitude * noise(point);
        point = rotation * point * 2.0 + vec2(100.0);
        amplitude *= 0.5;
    }
    return value;
}

vec3 hsvToRgb(vec3 color) {
    vec3 rgb = clamp(abs(mod(color.x * 6.0 + vec3(0.0, 4.0, 2.0), 6.0) - 3.0) - 1.0, 0.0, 1.0);
    return color.z * mix(vec3(1.0), rgb, color.y);
}

float sampleMask(vec2 uv) {
    if (uv.x < 0.0 || uv.y < 0.0 || uv.x > 1.0 || uv.y > 1.0) {
        return 0.0;
    }
    return texture(ColorTexture, uv).a;
}

float flamePattern(vec2 uv) {
    vec2 point = gl_FragCoord.xy * 8.0 / resolution.xx;
    float baseNoise = fbm(point - time * 0.10);
    vec2 curl = vec2(
        fbm(point + baseNoise + vec2(time * 0.42) - point.x - point.y),
        fbm(point + baseNoise - vec2(time * 0.31, time * 0.24))
    );
    float body = fbm(point + curl);
    float detail = fbm(point * 1.75 + curl.yx * 1.35 + vec2(-time * 0.18, time * 0.22));
    float heightFade = 1.0 - clamp(gl_FragCoord.y / resolution.y, 0.0, 1.0);
    float pulse = 0.5 + 0.5 * cos(time * 1.15 + uv.y * 5.8 + curl.x * 3.4);
    float flame = body * 0.50 + curl.y * 0.30 + detail * 0.20;
    return clamp(smoothstep(0.34, 1.12, flame * (0.42 + heightFade * 0.72) + pulse * 0.14), 0.0, 1.0);
}

vec3 getEffectColor(vec2 uv, vec4 baseColor, vec4 accentColor, out float energy) {
    vec2 aspectUv = (uv - 0.5) * vec2(resolution.x / max(resolution.y, 1.0), 1.0);

    if (effectMode == 1) {
        float distortion = fbm(uv * 7.0 + vec2(time * 0.18, -time * 0.12));
        float wave = 0.5 + 0.5 * sin(uv.x * 25.0 + uv.y * 13.0 - time * 4.0 + distortion * 5.0);
        energy = smoothstep(0.12, 0.95, wave);
        return mix(baseColor.rgb * 0.65, accentColor.rgb * 1.18, energy);
    }

    if (effectMode == 2) {
        float hue = fract(uv.x * 0.72 + uv.y * 0.38 - time * 0.10);
        float shimmer = 0.82 + 0.18 * sin((uv.x - uv.y) * 30.0 + time * 5.0);
        energy = shimmer;
        return hsvToRgb(vec3(hue, 0.76, shimmer));
    }

    if (effectMode == 3) {
        float scan = 0.5 + 0.5 * sin(gl_FragCoord.y * 0.42 - time * 10.0);
        float bars = smoothstep(0.62, 1.0, scan);
        float glitch = step(0.94, random(vec2(floor(time * 14.0), floor(gl_FragCoord.y / 7.0))));
        energy = clamp(0.38 + bars * 0.55 + glitch * 0.35, 0.0, 1.0);
        return mix(baseColor.rgb * 0.30, accentColor.rgb * (1.0 + glitch * 0.25), energy);
    }

    if (effectMode == 4) {
        float curtainA = sin(uv.x * 15.0 + time * 1.8 + sin(uv.y * 8.0 - time));
        float curtainB = sin(uv.x * 9.0 - time * 1.25 + fbm(uv * 5.0) * 4.0);
        float aurora = smoothstep(-0.25, 1.0, curtainA * 0.55 + curtainB * 0.45);
        float vertical = 0.62 + 0.38 * sin(uv.y * 12.0 + time * 0.8);
        energy = clamp(aurora * vertical, 0.0, 1.0);
        return mix(baseColor.rgb * 0.28, accentColor.rgb * 1.16, energy);
    }

    if (effectMode == 5) {
        float plasma = sin(aspectUv.x * 18.0 + time * 2.2)
                     + sin(aspectUv.y * 21.0 - time * 1.7)
                     + sin((aspectUv.x + aspectUv.y) * 15.0 + time * 1.3);
        energy = 0.5 + 0.1666667 * plasma;
        vec3 plasmaColor = mix(baseColor.rgb, accentColor.rgb, energy);
        return clamp(plasmaColor * (0.82 + energy * 0.34), 0.0, 1.0);
    }

    float flame = flamePattern(uv);
    float flicker = 0.82 + 0.18 * sin(time * 3.6 + flame * 5.0 + uv.x * 7.0);
    energy = clamp(flame * flicker, 0.0, 1.0);
    float hotCore = smoothstep(0.62, 1.0, energy);
    vec3 flameRgb = clamp(accentColor.rgb * (0.92 + hotCore * 0.28), 0.0, 1.0);
    return mix(baseColor.rgb, flameRgb, clamp(energy * 0.92, 0.0, 1.0));
}

void main() {
    vec2 oneTexel = 1.0 / resolution;
    float center = sampleMask(handUv);
    float outlineMask = 0.0;
    for (int direction = 0; direction < DIRECTIONS; direction++) {
        float angle = TAU * (float(direction) / float(DIRECTIONS));
        vec2 offset = vec2(cos(angle), sin(angle)) * oneTexel * OUTLINE_WIDTH;
        outlineMask = max(outlineMask, max(sampleMask(handUv + offset) - center, 0.0));
    }

    if (center <= 0.001 && outlineMask <= 0.001) {
        discard;
    }

    float colorSwap = smoothstep(0.12, 0.88, 0.5 + 0.5 * sin(time * 0.62));
    vec4 baseColor = mix(firstFillColor, secondFillColor, colorSwap);
    vec4 accentColor = mix(secondFillColor, firstFillColor, colorSwap);
    float energy;
    vec3 fillRgb = getEffectColor(handUv, baseColor, accentColor, energy);

    float outsideMask = 1.0 - smoothstep(0.001, 0.20, center);
    float edgePulse = 0.84 + 0.16 * sin(time * 3.2 + energy * 5.0);
    float fillAlpha = center * baseColor.a;
    float energyAlpha = center * energy * accentColor.a * 0.72;
    float insideAlpha = clamp(fillAlpha + energyAlpha * (1.0 - fillAlpha * 0.45), 0.0, 1.0);
    float edgeAlpha = outlineMask * outsideMask * outlineColor.a * edgePulse;
    float alpha = clamp(insideAlpha + edgeAlpha, 0.0, 1.0) * effectAlpha;
    vec3 color = fillRgb * insideAlpha + outlineColor.rgb * edgeAlpha;
    outColor = vec4(clamp(color / max(insideAlpha + edgeAlpha, 0.001), 0.0, 1.0), alpha);
}
