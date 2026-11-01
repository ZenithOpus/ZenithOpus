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

vec3 hash33(vec3 p) {
    p = vec3(dot(p, vec3(127.1, 311.7, 74.7)),
             dot(p, vec3(269.5, 183.3, 246.1)),
             dot(p, vec3(113.5, 271.9, 124.6)));
    return fract(sin(p) * 43758.5453);
}

// Worley: F1 — расстояние до ближайшего центра, F2 — до второго.
// Разность F2-F1 даёт тонкие грани между кристаллами.
vec2 worley(vec3 p) {
    vec3 cell = floor(p);
    vec3 local = fract(p);

    float f1 = 8.0;
    float f2 = 8.0;
    for (int x = -1; x <= 1; x++) {
        for (int y = -1; y <= 1; y++) {
            for (int z = -1; z <= 1; z++) {
                vec3 offset = vec3(float(x), float(y), float(z));
                vec3 point = offset + hash33(cell + offset) - local;
                float d = dot(point, point);
                if (d < f1) {
                    f2 = f1;
                    f1 = d;
                } else if (d < f2) {
                    f2 = d;
                }
            }
        }
    }
    return vec2(sqrt(f1), sqrt(f2));
}

vec3 blurredBehind(vec2 uv, float radius) {
    vec2 texel = radius / max(ScreenSize, vec2(1.0));
    vec3 sum = texture(Sampler0, uv).rgb * 0.28;
    sum += texture(Sampler0, clamp(uv + vec2(texel.x, 0.0), vec2(0.002), vec2(0.998))).rgb * 0.18;
    sum += texture(Sampler0, clamp(uv - vec2(texel.x, 0.0), vec2(0.002), vec2(0.998))).rgb * 0.18;
    sum += texture(Sampler0, clamp(uv + vec2(0.0, texel.y), vec2(0.002), vec2(0.998))).rgb * 0.18;
    sum += texture(Sampler0, clamp(uv - vec2(0.0, texel.y), vec2(0.002), vec2(0.998))).rgb * 0.18;
    return sum;
}

void main() {
    vec3 normal = normalize(viewNormal);
    vec3 viewDir = normalize(-viewPos + vec3(0.0001));
    float facing = clamp(abs(dot(normal, viewDir)), 0.0, 1.0);
    float fresnel = pow(1.0 - facing, max(0.1, FresnelPower));

    vec2 cells = worley(modelPos * 11.0 + vec3(0.0, iTime * 0.02, 0.0));
    float crystal = smoothstep(0.55, 0.02, cells.x);
    float seam = smoothstep(0.16, 0.0, cells.y - cells.x);

    // Иней нарастает с краёв к центру: у краёв fresnel высокий, там маска сильнее.
    float growth = clamp(fresnel * 1.35 + 0.18, 0.0, 1.0);
    float frost = clamp(crystal * growth + seam * 0.6 * growth, 0.0, 1.0);

    vec2 screenUv = gl_FragCoord.xy / max(ScreenSize, vec2(1.0));
    vec2 jitter = (cells.x - 0.5) * normal.xy * (0.02 + Freq * 2.0);
    vec2 uv = clamp(screenUv + jitter, vec2(0.002), vec2(0.998));

    vec3 clear = texture(Sampler0, uv).rgb;
    vec3 matte = blurredBehind(uv, 2.5 + frost * 6.0);
    vec3 behind = mix(clear, matte, clamp(frost + 0.25, 0.0, 1.0));

    vec3 ice = mix(vec3(0.78, 0.88, 0.98), mix(PrimaryColor, SecondaryColor, cells.x), clamp(ColorTransfer, 0.0, 1.0));

    vec3 color = behind * (0.55 + 0.35 * (1.0 - frost));
    color = mix(color, color * 0.6 + ice * 0.7, frost);
    color += ice * seam * growth * 0.55;
    color += vec3(1.0) * pow(1.0 - facing, 6.0) * 0.35;

    color *= max(0.0, Brightness) * 0.88;
    color = color / (1.0 + color * 0.24);

    float alpha = clamp(AlphaMul * (0.5 + frost * 0.35 + fresnel * 0.3), 0.0, 1.0);
    vec4 result = vec4(color, alpha) * vertexColor * ColorModulator;
    fragColor = apply_fog(result, sphericalVertexDistance, cylindricalVertexDistance,
            FogEnvironmentalStart, FogEnvironmentalEnd,
            FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
