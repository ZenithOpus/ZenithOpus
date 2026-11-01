#version 330

#moj_import <minecraft:fog.glsl>

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};
layout(std140) uniform Projection {
    mat4 ProjMat;
};
layout(std140) uniform ZenithData {
    float iTime;
    vec4 PrimaryColor4;
    vec4 SecondaryColor4;
    float AlphaMul;
    float FresnelPower;
    float Freq;
    float ColorTransfer;
    float Brightness;
    float MirrorEnabled;
    vec2 ScreenSize;
};
// vec3 в std140 трактуется драйверами по-разному: тут он хранится как vec4,
// а имя остаётся прежним, чтобы не трогать тела шейдеров.
#define PrimaryColor PrimaryColor4.xyz
#define SecondaryColor SecondaryColor4.xyz


uniform sampler2D Sampler0;

in float sphericalVertexDistance;
in float cylindricalVertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec3 viewNormal;
in vec3 viewPos;
in vec3 modelPos;

out vec4 fragColor;

vec4 permute(vec4 x) {
    return mod(((x * 34.0) + 1.0) * x, 289.0);
}

vec4 taylorInvSqrt(vec4 r) {
    return 1.79284291400159 - 0.85373472095314 * r;
}

float snoise(vec3 v) {
    const vec2 C = vec2(1.0 / 6.0, 1.0 / 3.0);
    const vec4 D = vec4(0.0, 0.5, 1.0, 2.0);

    vec3 i = floor(v + dot(v, C.yyy));
    vec3 x0 = v - i + dot(i, C.xxx);

    vec3 g = step(x0.yzx, x0.xyz);
    vec3 l = 1.0 - g;
    vec3 i1 = min(g.xyz, l.zxy);
    vec3 i2 = max(g.xyz, l.zxy);

    vec3 x1 = x0 - i1 + C.xxx;
    vec3 x2 = x0 - i2 + C.xxx * 2.0;
    vec3 x3 = x0 - 1.0 + C.xxx * 3.0;

    i = mod(i, 289.0);
    vec4 p = permute(permute(permute(
            i.z + vec4(0.0, i1.z, i2.z, 1.0)
    ) + i.y + vec4(0.0, i1.y, i2.y, 1.0)
    ) + i.x + vec4(0.0, i1.x, i2.x, 1.0));

    float n_ = 1.0 / 7.0;
    vec3 ns = n_ * D.wyz - D.xzx;

    vec4 j = p - 49.0 * floor(p * ns.z * ns.z);
    vec4 x_ = floor(j * ns.z);
    vec4 y_ = floor(j - 7.0 * x_);

    vec4 x = x_ * ns.x + ns.yyyy;
    vec4 y = y_ * ns.x + ns.yyyy;
    vec4 h = 1.0 - abs(x) - abs(y);

    vec4 b0 = vec4(x.xy, y.xy);
    vec4 b1 = vec4(x.zw, y.zw);

    vec4 s0 = floor(b0) * 2.0 + 1.0;
    vec4 s1 = floor(b1) * 2.0 + 1.0;
    vec4 sh = -step(h, vec4(0.0));

    vec4 a0 = b0.xzyw + s0.xzyw * sh.xxyy;
    vec4 a1 = b1.xzyw + s1.xzyw * sh.zzww;

    vec3 p0 = vec3(a0.xy, h.x);
    vec3 p1 = vec3(a0.zw, h.y);
    vec3 p2 = vec3(a1.xy, h.z);
    vec3 p3 = vec3(a1.zw, h.w);

    vec4 norm = taylorInvSqrt(vec4(dot(p0, p0), dot(p1, p1), dot(p2, p2), dot(p3, p3)));
    p0 *= norm.x;
    p1 *= norm.y;
    p2 *= norm.z;
    p3 *= norm.w;

    vec4 m = max(0.6 - vec4(dot(x0, x0), dot(x1, x1), dot(x2, x2), dot(x3, x3)), 0.0);
    m *= m;

    return 42.0 * dot(m * m, vec4(dot(p0, x0), dot(p1, x1), dot(p2, x2), dot(p3, x3)));
}

vec2 mirrorUvByFace(vec2 screenUv, vec3 normal, vec3 reflectedDir, vec3 pos) {
    vec3 n = normalize(normal);
    vec3 r = normalize(reflectedDir);

    float denom = dot(r, n);
    if (abs(denom) < 1e-4) {
        return screenUv;
    }

    float planeOffset = 0.45 + 0.12 * clamp(abs(pos.z), 0.0, 16.0);
    vec3 planePoint = pos + n * (sign(denom) * planeOffset);
    float t = dot(planePoint - pos, n) / denom;
    t = max(t, 0.0);

    vec3 hit = pos + r * t;

    vec4 clip = ProjMat * vec4(hit, 1.0);
    if (clip.w <= 1e-4) {
        return screenUv;
    }

    vec2 uv = clip.xy / clip.w * 0.5 + 0.5;
    vec2 flow = r.xy * (0.005 + Freq * 0.10);
    return clamp(uv + flow, vec2(0.001), vec2(0.999));
}

void main() {
    float noise = snoise(vec3(viewPos * 0.11 + iTime * 0.10));

    vec3 baseNormal = normalize(viewNormal);
    vec3 baseIncident = normalize(viewPos + vec3(0.0001));

    vec3 normal = normalize(baseNormal + vec3(noise * Freq * 0.12));
    vec3 incident = normalize(baseIncident + vec3(noise) * (Freq * 0.95));
    vec3 reflectedDir = normalize(mix(baseIncident, reflect(incident, normal), 0.30));

    vec2 screenUv = gl_FragCoord.xy / max(ScreenSize, vec2(1.0));
    vec2 reflectOffset = reflectedDir.xy * (0.015 + Freq * 0.5) + noise * 0.002;
    vec2 uv = clamp(screenUv + reflectOffset, vec2(0.001), vec2(0.999));
    if (MirrorEnabled > 0.5) {
        uv = mirrorUvByFace(screenUv, viewNormal, reflectedDir, viewPos);
    }

    vec3 reflectedCurrent = texture(Sampler0, uv).rgb;
    vec3 reflected = reflectedCurrent;

    float wave = 0.5 + 0.5 * sin(iTime * 7.0 + texCoord0.y * 24.0 + texCoord0.x * 10.0);
    vec3 tint = mix(PrimaryColor, SecondaryColor, wave);

    vec3 viewDir = normalize(-viewPos + vec3(0.0001));
    float fresnel = pow(
            1.0 - clamp(abs(dot(normalize(viewNormal), viewDir)), 0.0, 1.0),
            max(0.1, FresnelPower)
    );

    float colorStrength = clamp(ColorTransfer, 0.0, 1.0);
    vec3 reflectionOnly = reflected * 0.85;
    vec3 colored = reflected * 0.55 + tint * (0.38 + fresnel * 0.26);
    vec3 glass = mix(reflectionOnly, colored, colorStrength);
    glass += tint * (0.03 + fresnel * 0.10) * colorStrength;
    glass *= max(0.0, Brightness);

    float alpha = clamp(AlphaMul * (0.42 + fresnel * 0.58), 0.0, 1.0);
    alpha = mix(alpha, 1.0, smoothstep(0.98, 1.0, AlphaMul));
    alpha = clamp(alpha, 0.0, 1.0);
    vec4 color = vec4(glass, alpha) * vertexColor * ColorModulator;

    fragColor = apply_fog(color, sphericalVertexDistance, cylindricalVertexDistance,
            FogEnvironmentalStart, FogEnvironmentalEnd,
            FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
