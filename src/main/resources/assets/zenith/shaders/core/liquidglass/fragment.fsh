#version 330

#moj_import <zenith:common.glsl>

layout(std140) uniform ZenithData {
    vec2 Size;
    vec4 Tab;
    float TabRadius;
    float MainHeight;
    float Joint;
    vec4 Radius;
    float Smoothness;
    float CornerSmoothness;
    float GlobalAlpha;
    float FresnelPower;
    vec4 FresnelColor4;
    float FresnelAlpha;
    float BaseAlpha;
    float FresnelInvert;
    float FresnelMix;
    float DistortStrength;
    float Time;
    vec4 GlareColor4;
    float GlareAlpha;
    float GlareSpeed;
    float GlareMode;
    float GlareTexture;
};
// vec3 в std140 трактуется драйверами по-разному: тут он хранится как vec4,
// а имя остаётся прежним, чтобы не трогать тела шейдеров.
#define FresnelColor FresnelColor4.xyz
#define GlareColor GlareColor4.xyz


in vec2 FragCoord;
in vec2 TexCoord;
in vec4 FragColor;

uniform sampler2D Sampler0;


out vec4 OutColor;

float roundedBoxSDF(vec2 p, vec2 b, vec4 r, float smoothness) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x = (p.y > 0.0) ? r.x : r.y;
    vec2 q = abs(p) - b + r.x;
    vec2 q_clamped = max(q, 0.0);
    float len = pow(pow(q_clamped.x, smoothness) + pow(q_clamped.y, smoothness), 1.0/smoothness);
    return min(max(q.x, q.y), 0.0) + len - r.x;
}

float zenithGlare(vec2 p, vec2 size, float edge) {
    int mode = int(GlareMode + 0.5);
    float time = Time * GlareSpeed;
    vec2 px = p * size * 0.5;
    if (mode == 11) {
        return 0.0;
    }
    if (mode == 1) {
        float cycle = fract(time * 0.5);
        float eased = cycle * cycle * (3.0 - 2.0 * cycle);
        float x = p.x + p.y * 0.35;
        float d = x - mix(-1.8, 1.8, eased);
        float band = exp(-d * d * 18.0) + 0.45 * exp(-(d + 0.22) * (d + 0.22) * 90.0);
        float fade = smoothstep(0.0, 0.12, cycle) * smoothstep(1.0, 0.8, cycle);
        return band * fade * (1.0 - edge * 0.4);
    }
    if (mode == 2) {
        float cycle = fract(time * 0.5);
        float eased = cycle * cycle * (3.0 - 2.0 * cycle);
        vec2 dir = normalize(vec2(1.0, 0.55));
        float perp = dot(p, vec2(-dir.y, dir.x));
        float pos = mix(-1.9, 1.9, eased);
        float d1 = perp - pos;
        float d2 = perp - pos + 0.32;
        float band = exp(-d1 * d1 * 60.0) + 0.6 * exp(-d2 * d2 * 140.0);
        float fade = smoothstep(0.0, 0.12, cycle) * smoothstep(1.0, 0.8, cycle);
        return band * fade * (1.0 - edge * 0.4);
    }
    if (mode == 3) {
        float ang = atan(px.y, px.x);
        float head = mod(time * 3.14159, 6.28318) - 3.14159;
        float da = abs(mod(ang - head + 3.14159, 6.28318) - 3.14159);
        float trail = exp(-da * da * 5.0);
        return trail * pow(edge, 3.0) * 1.6;
    }
    if (mode == 5) {
        vec2 c = vec2(sin(time * 1.7), cos(time * 1.13)) * size * vec2(0.36, 0.3);
        float r = length(px - c) / max(min(size.x, size.y) * 0.55, 1.0);
        return exp(-r * r * 2.4) * 0.85;
    }
    if (mode == 6) {
        float y = p.y + 0.25 * sin(p.x * 2.6 + time * 1.7) + 0.12 * sin(p.x * 5.3 - time * 2.3);
        float d = y - sin(time * 0.6) * 0.35;
        float band = exp(-d * d * 7.0);
        float shimmer = 0.65 + 0.35 * sin(p.x * 9.0 + time * 3.0);
        return band * shimmer * 0.75 * (1.0 - edge * 0.3);
    }
    float cycle = mod(time, 4.0);
    float phase = floor(cycle);
    float t = fract(cycle);
    float eased = t * t * (3.0 - 2.0 * t);
    vec2 diagDir;
    if (phase < 1.0) diagDir = normalize(vec2(1.0, 0.7));
    else if (phase < 2.0) diagDir = normalize(vec2(-1.0, 0.7));
    else if (phase < 3.0) diagDir = normalize(vec2(-1.0, -0.7));
    else diagDir = normalize(vec2(1.0, -0.7));
    vec2 perpDir = vec2(-diagDir.y, diagDir.x);
    float bandDist = dot(p, perpDir) - mix(-1.6, 1.6, eased);
    float sharpBand = exp(-bandDist * bandDist * 35.0);
    float endFade = smoothstep(1.3, 0.4, abs(dot(p, diagDir)));
    float sweepFade = smoothstep(0.0, 0.15, t) * smoothstep(1.0, 0.85, t);
    return sharpBand * endFade * sweepFade * (1.0 - edge * 0.4);
}

vec3 zenithGlareLight(vec2 p, vec2 size, float edge) {
    int kind = int(GlareTexture + 0.5);
    float g = zenithGlare(p, size, edge);
    float c = clamp(g, 0.0, 1.0);
    vec2 px = p * size * 0.5;
    if (kind == 1) {
        vec2 shift = vec2(9.0, 3.5) / max(size, vec2(1.0));
        float r = zenithGlare(p + shift, size, edge);
        float b = zenithGlare(p - shift, size, edge);
        vec3 split = vec3(r, g, b);
        vec3 tint = mix(vec3(1.0), GlareColor, 0.35);
        return split * tint * 1.25;
    }
    if (kind == 2) {
        vec3 halo = GlareColor * pow(c, 0.7) * 0.5;
        vec3 core = mix(GlareColor, vec3(1.0), 0.85) * pow(c, 5.0) * 1.2;
        return halo + core;
    }
    if (kind == 3) {
        float rim = clamp(c * (1.0 - c) * 4.0, 0.0, 1.0);
        rim *= rim;
        vec3 vivid = GlareColor / max(max(GlareColor.r, max(GlareColor.g, GlareColor.b)), 0.001);
        return vivid * (rim * 1.3 + pow(c, 10.0) * 0.2);
    }
    if (kind == 4) {
        float lines = 0.5 + 0.5 * cos(c * 3.14159 * 6.0);
        lines *= lines;
        vec3 metal = mix(GlareColor, vec3(1.0), 0.55);
        return metal * c * (0.3 + 0.7 * lines) * 1.3;
    }
    if (kind == 5) {
        float ang = atan(px.x, px.y + size.y * 0.9);
        float drift = Time * GlareSpeed;
        float fine = pow(0.5 + 0.5 * sin(ang * 48.0 + drift * 2.0), 3.0);
        float wide = 0.5 + 0.5 * sin(ang * 17.0 - drift * 1.3);
        return GlareColor * c * (0.2 + 1.0 * mix(fine, wide, 0.35)) * 1.2;
    }
    return GlareColor * g;
}

void main() {
    vec2 local = FragCoord * Size;
    vec2 main_size = vec2(Size.x, MainHeight > 0.0 ? min(MainHeight, Size.y) : Size.y);
    vec2 center = main_size * 0.5;
    vec2 box_half_size = center - 1.0;
    vec2 pos = local - center;

    float distance = roundedBoxSDF(-pos, box_half_size, Radius, CornerSmoothness);

    if (Tab.w > 0.0) {
        vec2 tab_center = Tab.xy + Tab.zw * 0.5;
        vec2 tab_half = max(Tab.zw * 0.5 - 1.0, vec2(0.01));
        float tab = roundedBoxSDF(tab_center - local, tab_half, vec4(TabRadius), CornerSmoothness);
        float k = max(Joint, 0.001);
        float blend = clamp(0.5 + 0.5 * (tab - distance) / k, 0.0, 1.0);
        distance = mix(tab, distance, blend) - k * blend * (1.0 - blend);
    }

    float alpha = 1.0 - smoothstep(1.0 - Smoothness, 1.0, distance);

    float distToEdge = abs(distance);

    float max_dist_norm = min(box_half_size.x, box_half_size.y);
    float edge_gradient = 1.0 - clamp(distToEdge / max_dist_norm, 0.0, 1.0);

    float fresnel;
    float base = FresnelInvert > 0.5 ? edge_gradient : (1.0 - edge_gradient);

    if (FresnelPower > 20.0) {
        fresnel = exp(FresnelPower * log(clamp(base, 0.001, 1.0)));
    } else {
        fresnel = pow(base, FresnelPower);
    }
    fresnel = clamp(fresnel, 0.0, 1.0);

    // Refraction distortion
    vec2 dir = normalize(pos + vec2(0.001));
    vec2 baseDistort = dir * fresnel * DistortStrength;

    // Multi-sample refraction with chromatic aberration
    float aberration = DistortStrength * 2.5;
    vec3 refracted = vec3(0.0);
    float totalWeight = 0.0;

    for (int i = 0; i < 5; i++) {
        float s = float(i) * 0.25;
        float g = s * 2.0 - 1.0;
        float weight = exp(-g * g * 2.0);
        vec2 offset = baseDistort * (0.3 + s * 1.4);

        refracted.r += texture(Sampler0, TexCoord + offset * (1.0 + aberration)).r * weight;
        refracted.g += texture(Sampler0, TexCoord + offset).g * weight;
        refracted.b += texture(Sampler0, TexCoord + offset * (1.0 - aberration)).b * weight;
        totalWeight += weight;
    }
    refracted /= totalWeight;
    vec4 texColor = vec4(refracted, 1.0) * FragColor;

    vec2 normPos = (FragCoord - 0.5) * 2.0;
    vec3 glareLight = zenithGlareLight(normPos, Size, edge_gradient) * GlareAlpha;
    float glare = max(glareLight.r, max(glareLight.g, glareLight.b));

    // Edge caustic — light concentration at glass edges
    float caustic = pow(edge_gradient, 4.0) * 0.1;

    // Combine
    vec3 finalColor = mix(texColor.rgb, FresnelColor, fresnel * FresnelMix);
    finalColor += glareLight + caustic * FresnelColor;
    finalColor = clamp(finalColor, 0.0, 1.0);

    float finalAlpha = (mix(BaseAlpha, FresnelAlpha, fresnel) + glare * 0.35) * alpha;

    if (finalAlpha < 0.001) {
        discard;
    }

    OutColor = vec4(finalColor, finalAlpha * GlobalAlpha);
}
