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

// Возвращает: x = расстояние до ближайшего центра, y = до второго,
// zw — идентификатор осколка (ячейка ближайшего центра).
vec4 shards(vec3 p) {
    vec3 cell = floor(p);
    vec3 local = fract(p);

    float f1 = 8.0;
    float f2 = 8.0;
    vec3 id = vec3(0.0);
    for (int x = -1; x <= 1; x++) {
        for (int y = -1; y <= 1; y++) {
            for (int z = -1; z <= 1; z++) {
                vec3 offset = vec3(float(x), float(y), float(z));
                vec3 neighbour = cell + offset;
                vec3 point = offset + hash33(neighbour) - local;
                float d = dot(point, point);
                if (d < f1) {
                    f2 = f1;
                    f1 = d;
                    id = neighbour;
                } else if (d < f2) {
                    f2 = d;
                }
            }
        }
    }
    float seed = fract(sin(dot(id, vec3(12.9898, 78.233, 37.719))) * 43758.5453);
    return vec4(sqrt(f1), sqrt(f2), seed, fract(seed * 7.13));
}

void main() {
    vec4 cell = shards(modelPos * 6.5);
    float seam = smoothstep(0.13, 0.0, cell.y - cell.x);
    float seed = cell.z;

    // Каждый осколок наклонён по-своему — иначе стекло читается как один кусок.
    vec3 tilt = (vec3(seed, cell.w, fract(seed * 3.77)) - 0.5) * 0.9;
    vec3 normal = normalize(viewNormal + tilt);
    vec3 incident = normalize(viewPos + vec3(0.0001));
    vec3 viewDir = -incident;

    float facing = clamp(dot(normal, viewDir), 0.0, 1.0);
    float fresnel = pow(1.0 - facing, max(0.1, FresnelPower));

    vec2 screenUv = gl_FragCoord.xy / max(ScreenSize, vec2(1.0));
    vec3 bent = refract(incident, normal, 0.78);
    vec2 uv = clamp(screenUv + bent.xy * (0.02 + Freq * 5.0), vec2(0.002), vec2(0.998));

    vec3 behind = texture(Sampler0, uv).rgb;

    vec3 tint = mix(PrimaryColor, SecondaryColor, seed);
    tint = mix(vec3(0.85, 0.9, 1.0), tint, clamp(ColorTransfer, 0.0, 1.0));

    vec3 color = behind * (0.55 + seed * 0.35);
    color += tint * fresnel * 0.45;

    float glow = 0.75 + 0.25 * sin(iTime * 2.0 + seed * 6.28);
    color += tint * seam * glow * 1.15;
    color += vec3(1.0) * seam * 0.25;

    float glint = pow(max(0.0, dot(normal, normalize(vec3(0.4, 0.8, 0.45)))), 30.0);
    color += vec3(1.0) * glint * 0.5;

    color *= max(0.0, Brightness) * 0.85;
    color = color / (1.0 + color * 0.25);

    float alpha = clamp(AlphaMul * (0.5 + fresnel * 0.4 + seam * 0.5), 0.0, 1.0);
    vec4 result = vec4(color, alpha) * vertexColor * ColorModulator;
    fragColor = apply_fog(result, sphericalVertexDistance, cylindricalVertexDistance,
            FogEnvironmentalStart, FogEnvironmentalEnd,
            FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
