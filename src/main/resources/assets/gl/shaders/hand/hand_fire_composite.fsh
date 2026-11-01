#version 330 core

in vec2 handUv;
out vec4 color;

uniform sampler2D FireTex;
uniform sampler2D HandTex;
uniform sampler2D MaskTex;
uniform vec2 TexelSize;
uniform float Time;
uniform float Intensity;
uniform float Alpha;
uniform vec4 ColorHot;
uniform vec4 ColorMid;
uniform vec4 ColorEdge;

float hash(vec2 p) {
    p = fract(p * vec2(127.1, 311.7));
    p += dot(p, p + 19.19);
    return fract(p.x * p.y);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

vec3 fireRamp(float heat) {
    float hotMix = smoothstep(0.48, 1.0, heat);
    float midMix = smoothstep(0.10, 0.70, heat);
    vec3 base = mix(ColorEdge.rgb, ColorMid.rgb, midMix);
    return mix(base, ColorHot.rgb, hotMix);
}

void main() {
    float waveA = sin(handUv.y * 44.0 + Time * 5.1);
    float waveB = sin((handUv.x + handUv.y) * 27.0 - Time * 3.7);
    float n = noise(handUv * vec2(11.0, 17.0) + vec2(Time * 0.22, -Time * 0.30));
    vec2 warp = vec2(waveA + waveB, waveB - n) * TexelSize * 2.0;

    vec4 soft = texture(FireTex, clamp(handUv + warp, vec2(0.001), vec2(0.999)));
    vec4 core = texture(FireTex, handUv);
    vec4 hand = texture(HandTex, handUv);
    float handChannel = max(max(hand.r, hand.g), hand.b);
    float handMask = texture(MaskTex, handUv).a;
    float handHeat = clamp(handMask * (0.72 + n * 0.34), 0.0, 1.0);
    vec3 handFire = mix(fireRamp(handHeat), max(hand.rgb, vec3(handChannel)) * 1.15, 0.38);
    handFire += ColorHot.rgb * handMask * 0.16;
    float pulse = 0.86 + 0.18 * sin(Time * 8.0 + n * 6.28318);
    float trailAlpha = max(soft.a * 0.76, core.a * 0.94);
    float currentAlpha = handMask * 0.72;
    float outAlpha = clamp(max(trailAlpha, currentAlpha) * Alpha, 0.0, 1.0);

    if (outAlpha <= 0.002) {
        discard;
    }

    vec3 rgb = soft.rgb * (0.72 + n * 0.18) + core.rgb * core.a * 0.78;
    rgb = mix(rgb, handFire, clamp(currentAlpha * 0.78, 0.0, 1.0));
    rgb *= Intensity * pulse;
    color = vec4(rgb, outAlpha);
}
