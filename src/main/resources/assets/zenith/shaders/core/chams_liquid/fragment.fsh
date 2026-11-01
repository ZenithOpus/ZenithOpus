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

// Метаболы: несколько плавающих центров, их суммарное поле гнёт нормаль.
// Центры живут в координатах модели, поэтому капли не ездят от поворота камеры.
float blobField(vec3 p, float t) {
    float field = 0.0;
    for (int i = 0; i < 5; i++) {
        float fi = float(i);
        vec3 center = vec3(
            sin(t * (0.5 + fi * 0.13) + fi * 2.1) * 0.55,
            cos(t * (0.37 + fi * 0.09) + fi * 1.3) * 0.9,
            sin(t * (0.43 + fi * 0.17) + fi * 3.7) * 0.55
        );
        vec3 d = p - center;
        field += 0.22 / (dot(d, d) + 0.05);
    }
    return field;
}

vec3 blobNormal(vec3 p, float t) {
    float eps = 0.06;
    float base = blobField(p, t);
    return vec3(
        blobField(p + vec3(eps, 0.0, 0.0), t) - base,
        blobField(p + vec3(0.0, eps, 0.0), t) - base,
        blobField(p + vec3(0.0, 0.0, eps), t) - base
    ) / eps;
}

void main() {
    float t = iTime * (0.35 + Freq * 40.0);
    vec3 flow = blobNormal(modelPos * 1.6, t);

    vec3 normal = normalize(viewNormal + flow * 0.16);
    vec3 incident = normalize(viewPos + vec3(0.0001));
    vec3 viewDir = -incident;
    vec3 reflected = reflect(incident, normal);

    float facing = clamp(abs(dot(normal, viewDir)), 0.0, 1.0);
    float fresnel = pow(1.0 - facing, max(0.1, FresnelPower));

    // Отражение берём в экранных координатах: форма течёт, а отражённая сцена стоит на месте.
    vec2 screenUv = gl_FragCoord.xy / max(ScreenSize, vec2(1.0));
    vec2 uv = clamp(screenUv + reflected.xy * (0.10 + Freq * 8.0), vec2(0.002), vec2(0.998));
    vec3 mirror = texture(Sampler0, uv).rgb;

    vec3 tint = mix(PrimaryColor, SecondaryColor, 0.5 + 0.5 * reflected.y);
    tint = mix(vec3(0.82, 0.85, 0.9), tint, clamp(ColorTransfer, 0.0, 1.0));

    vec3 color = mirror * mix(vec3(1.0), tint, 0.4);
    color *= 0.65 + fresnel * 0.9;

    float spec = pow(max(0.0, dot(normal, normalize(vec3(0.3, 0.9, 0.35)))), 48.0);
    color += vec3(1.0) * spec * 1.1;

    float rim = pow(1.0 - facing, 5.0);
    color += tint * rim * 0.45;

    color *= max(0.0, Brightness) * 0.92;
    color = color / (1.0 + color * 0.2);

    float alpha = clamp(AlphaMul * (0.8 + fresnel * 0.2), 0.0, 1.0);
    vec4 result = vec4(color, alpha) * vertexColor * ColorModulator;
    fragColor = apply_fog(result, sphericalVertexDistance, cylindricalVertexDistance,
            FogEnvironmentalStart, FogEnvironmentalEnd,
            FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
