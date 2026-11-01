#version 330

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};
layout(std140) uniform ZenithPost {
    vec4 FireColor;
    vec4 EmberColor;
    float Time;
    float KeepForeign;
};

uniform sampler2D InSampler;
/** 1 — в буфере уже лежит свечение ShaderESP, его надо сохранить; 0 — гасить всё, кроме снарядов. */

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

// Снаряд в буфере обводки помечен чисто чёрным (Predictions#SHADER_GLOW_MARKER). Буфер общий:
// в нём же лежит свечение ShaderESP, которое цепочка проходит раньше нас. Морозить всё подряд
// нельзя — иначе игроки обмерзали бы вместе с жемчугом, — поэтому маска строится только по
// метке, а чужие пиксели уезжают на выход как есть.
bool isProjectile(vec4 c) {
    return c.a > 0.5 && max(c.r, max(c.g, c.b)) < 0.02;
}

float sampleMask(vec2 uv) {
    if (uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0) return 0.0;
    return isProjectile(texture(InSampler, uv)) ? 1.0 : 0.0;
}

float haloGlow(vec2 uv) {
    float glow = 0.0;
    for (int i = 1; i <= 9; i++) {
        float r = float(i) * 1.7;
        float ring = 0.0;
        for (int j = 0; j < 5; j++) {
            float a = TAU * float(j) / 5.0 + float(i) * 0.4;
            ring += sampleMask(uv + vec2(cos(a), sin(a)) * oneTexel * r);
        }
        glow = max(glow, ring / 5.0 * (1.0 - float(i) / 10.0));
    }
    return glow;
}

void main() {
    // Чужой пиксель держим только когда его есть кому предъявить: при выключенном ESP в буфере
    // остаётся ванильное свечение, а его цепочку мы подменили собой — пропусти такой пиксель, и
    // силуэт ляжет в кадр сплошным пятном без размытия.
    vec4 source = texture(InSampler, texCoord);
    vec4 keep = KeepForeign > 0.5 && !isProjectile(source) ? source : vec4(0.0);
    float center = sampleMask(texCoord);
    float glow = haloGlow(texCoord);

    float outline = 0.0;
    for (int d = 0; d < 8; d++) {
        float a = TAU * (float(d) / 8.0);
        vec2 dir = vec2(cos(a), sin(a));
        outline = max(outline, max(sampleMask(texCoord + dir * oneTexel * 2.0) - center, 0.0));
    }

    if (center <= 0.001 && glow <= 0.001 && outline <= 0.001) {
        fragColor = keep;
        return;
    }

    float band = glow * (1.0 - center);
    float shimmer = 0.6 + 0.4 * fbm(texCoord * 26.0 + vec2(0.0, -Time * 0.6));

    vec2 crystalCell = floor(texCoord * vec2(210.0, 210.0));
    float crystal = smoothstep(0.82, 1.0, random(crystalCell));
    float twinkle = 0.5 + 0.5 * sin(Time * 5.0 + dot(crystalCell, vec2(1.3, 0.7)));
    float sparkle = crystal * twinkle * max(band, center);

    vec3 col = mix(FireColor.rgb, EmberColor.rgb, smoothstep(0.3, 1.0, glow));
    col = mix(col, vec3(1.0), sparkle * 0.85);

    float bodyAlpha = center * 0.42 * FireColor.a;
    float haloAlpha = band * shimmer * FireColor.a * 1.15;
    float edgeAlpha = outline * EmberColor.a * 0.85;
    float finalAlpha = clamp(max(sparkle, max(bodyAlpha, max(haloAlpha, edgeAlpha))), 0.0, 1.0);
    col = clamp(col, 0.0, 1.3);

    // Иней кладём поверх того, что уже лежало в пикселе, а не вместо него: ореол дотягивается до
    // соседнего силуэта, и замена выжигала бы там свечение ESP.
    float baseAlpha = keep.a;
    float outAlpha = finalAlpha + baseAlpha * (1.0 - finalAlpha);
    vec3 outRgb = col * finalAlpha + keep.rgb * baseAlpha * (1.0 - finalAlpha);

    fragColor = vec4(outRgb / max(outAlpha, 0.001), clamp(outAlpha, 0.0, 1.0));
}
