#version 330 core

in vec2 handUv;
out vec4 color;

uniform sampler2D PreviousTex;
uniform sampler2D HandTex;
uniform sampler2D MaskTex;
uniform vec2 TexelSize;
uniform float Time;
uniform float Decay;
uniform float Intensity;
uniform float Lift;
uniform float Turbulence;
uniform float Alpha;
uniform float EmitStrength;
uniform vec4 ColorHot;
uniform vec4 ColorMid;
uniform vec4 ColorEdge;
uniform vec4 ColorSmoke;

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
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

float fbm(vec2 p) {
    float value = 0.0;
    float amp = 0.5;
    for (int i = 0; i < 4; i++) {
        value += noise(p) * amp;
        p = p * 2.03 + 13.17;
        amp *= 0.5;
    }
    return value;
}

vec3 fireRamp(float heat) {
    float hotMix = smoothstep(0.48, 1.0, heat);
    float midMix = smoothstep(0.10, 0.70, heat);
    vec3 base = mix(ColorEdge.rgb, ColorMid.rgb, midMix);
    return mix(base, ColorHot.rgb, hotMix);
}

vec4 readPrevious(vec2 pos) {
    float waveScale = 1.5;
    float flameLift = (2.0 + Lift * 20.0) * TexelSize.y;
    float lateral = sin(pos.y * 38.0 + Time * 4.2) + sin((pos.x + pos.y) * 21.0 - Time * 3.1);
    vec2 wave = vec2(lateral * TexelSize.x * waveScale, flameLift);
    vec2 noiseUv = pos * vec2(8.0, 13.0) + vec2(Time * 0.24, -Time * 0.34);
    float gust = (fbm(noiseUv) - 0.5) * Turbulence;
    vec2 prevUv = pos - wave + vec2(gust * TexelSize.x * 26.0, 0.0);

    vec4 previous = texture(PreviousTex, clamp(prevUv, vec2(0.001), vec2(0.999)));
    float bounds = smoothstep(0.0, 0.025, prevUv.x)
            * smoothstep(0.0, 0.025, prevUv.y)
            * smoothstep(0.0, 0.025, 1.0 - prevUv.x)
            * smoothstep(0.0, 0.025, 1.0 - prevUv.y);

    float n = fbm(prevUv * vec2(12.0, 18.0) + vec2(Time * 0.18, Time * 0.10));
    float localDecay = mix(0.985, 0.90, n * Turbulence * 0.55);
    previous.rgb = mix(previous.rgb, ColorSmoke.rgb * previous.a, 0.018 * Turbulence);
    previous *= Decay * localDecay * bounds;
    return previous;
}

void main() {
    vec4 hand = texture(HandTex, handUv);
    float channel = max(max(hand.r, hand.g), hand.b);
    float mask = texture(MaskTex, handUv).a;

    vec2 noisePos = handUv * vec2(9.0, 15.0) + vec2(Time * 0.32, -Time * 0.58);
    float flameNoise = fbm(noisePos);
    float edgeBreak = smoothstep(0.12, 0.92, flameNoise + mask * 0.35);
    float heat = clamp(mask * (0.78 + flameNoise * 0.48), 0.0, 1.0);
    float emit = clamp(EmitStrength, 0.0, 1.6);
    float currentAlpha = mask * Alpha * emit * mix(0.74, 1.16, edgeBreak);

    vec3 handColor = max(hand.rgb, vec3(channel));
    vec3 currentRgb = fireRamp(heat);
    currentRgb = mix(currentRgb, handColor * 1.22, 0.42);
    currentRgb += ColorHot.rgb * mask * smoothstep(0.55, 1.0, flameNoise) * 0.22;

    vec4 previous = readPrevious(handUv);
    vec4 current = vec4(currentRgb * Intensity * mix(0.82, 1.0, min(emit, 1.0)), currentAlpha);

    float blend = clamp(current.a * (0.78 + flameNoise * 0.22), 0.0, 1.0);
    vec3 rgb = mix(previous.rgb, current.rgb, blend);
    rgb += current.rgb * current.a * 0.16;
    float outAlpha = clamp(max(previous.a, previous.a * 0.72 + current.a), 0.0, 1.0);

    color = vec4(rgb, outAlpha);
}
