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

float hash13(vec3 p) {
    p = fract(p * 0.1031);
    p += dot(p, p.zyx + 31.32);
    return fract((p.x + p.y) * p.z);
}

float valueNoise(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);

    float n000 = hash13(i + vec3(0.0, 0.0, 0.0));
    float n100 = hash13(i + vec3(1.0, 0.0, 0.0));
    float n010 = hash13(i + vec3(0.0, 1.0, 0.0));
    float n110 = hash13(i + vec3(1.0, 1.0, 0.0));
    float n001 = hash13(i + vec3(0.0, 0.0, 1.0));
    float n101 = hash13(i + vec3(1.0, 0.0, 1.0));
    float n011 = hash13(i + vec3(0.0, 1.0, 1.0));
    float n111 = hash13(i + vec3(1.0, 1.0, 1.0));

    return mix(
        mix(mix(n000, n100, f.x), mix(n010, n110, f.x), f.y),
        mix(mix(n001, n101, f.x), mix(n011, n111, f.x), f.y),
        f.z
    );
}

float fbm(vec3 p) {
    float sum = 0.0;
    float amp = 0.5;
    for (int i = 0; i < 4; i++) {
        sum += amp * valueNoise(p);
        p *= 2.03;
        amp *= 0.5;
    }
    return sum;
}

// Интерференция в тонкой плёнке: разность хода 2*n*d*cos(угол преломления),
// для каждой длины волны своя фаза — отсюда бензиновый перелив, а не радуга по кругу.
vec3 thinFilm(float thicknessNm, float cosView) {
    float ior = 1.32;
    float sinI = sqrt(max(0.0, 1.0 - cosView * cosView));
    float sinT = sinI / ior;
    float cosT = sqrt(max(0.0, 1.0 - sinT * sinT));

    float path = 2.0 * ior * thicknessNm * cosT;
    vec3 lambda = vec3(650.0, 550.0, 450.0);
    vec3 phase = 6.28318 * path / lambda + 3.14159;
    return 0.5 + 0.5 * cos(phase);
}

void main() {
    vec3 normal = normalize(viewNormal);
    vec3 viewDir = normalize(-viewPos + vec3(0.0001));
    float facing = clamp(abs(dot(normal, viewDir)), 0.0, 1.0);
    float fresnel = pow(1.0 - facing, max(0.1, FresnelPower));

    float slow = iTime * (0.05 + Freq * 6.0);
    float film = fbm(modelPos * 3.1 + vec3(0.0, -slow * 1.4, slow * 0.6));
    float thickness = mix(210.0, 680.0, film);
    thickness += fresnel * 150.0;

    vec3 interference = thinFilm(thickness, facing);

    vec2 screenUv = gl_FragCoord.xy / max(ScreenSize, vec2(1.0));
    vec2 uv = clamp(screenUv + normal.xy * 0.012, vec2(0.002), vec2(0.998));
    vec3 behind = texture(Sampler0, uv).rgb;

    vec3 tint = mix(PrimaryColor, SecondaryColor, film);
    interference = mix(interference, interference * tint * 1.6, clamp(ColorTransfer, 0.0, 1.0));

    float darkBase = 0.12 + 0.18 * film;
    vec3 color = behind * darkBase;
    color += interference * (0.35 + fresnel * 0.75);

    float sheen = pow(max(0.0, dot(normal, normalize(vec3(0.35, 0.85, 0.4)))), 22.0);
    color += interference * sheen * 0.8;

    color *= max(0.0, Brightness) * 0.9;
    color = color / (1.0 + color * 0.22);

    float alpha = clamp(AlphaMul * (0.72 + fresnel * 0.28), 0.0, 1.0);
    vec4 result = vec4(color, alpha) * vertexColor * ColorModulator;
    fragColor = apply_fog(result, sphericalVertexDistance, cylindricalVertexDistance,
            FogEnvironmentalStart, FogEnvironmentalEnd,
            FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
