#version 330

#moj_import <zenith:common.glsl>

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};
layout(std140) uniform ZenithData {
    vec2 Resolution;
    float Time;
    vec4 Accent4;
    vec4 Secondary4;
    float Intensity;
};
#define Accent Accent4.xyz
#define Secondary Secondary4.xyz

in vec2 FragCoord;
in vec4 FragColor;

out vec4 OutColor;

float hash21(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float valueNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    float a = hash21(i);
    float b = hash21(i + vec2(1.0, 0.0));
    float c = hash21(i + vec2(0.0, 1.0));
    float d = hash21(i + vec2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm(vec2 p) {
    float sum = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 5; i++) {
        sum += amplitude * valueNoise(p);
        p = p * 2.02 + vec2(17.3, 9.1);
        amplitude *= 0.5;
    }
    return sum;
}

void main() {
    vec2 uv = FragCoord;
    float aspect = Resolution.x / max(1.0, Resolution.y);
    vec2 p = vec2((uv.x - 0.5) * aspect, uv.y - 0.5);

    float t = Time * 0.02;

    // Полотно: две медленные волны шума, сдвинутые друг относительно друга. Одна задаёт
    // крупные сгустки, вторая тянет их в стороны — так пятна не стоят на месте и не
    // повторяются с заметным периодом.
    vec2 drift = vec2(t * 0.6, -t * 0.35);
    float warp = fbm(p * 1.6 + drift);
    float clouds = fbm(p * 2.4 + vec2(warp * 1.4, -warp) + drift * 1.7);
    float veil = fbm(p * 5.0 - drift * 2.3 + warp);

    // Луч от верхнего угла: диагональная полоса света, медленно качающаяся поперёк экрана.
    float beamAngle = 0.55 + 0.08 * sin(Time * 0.05);
    float beam = dot(normalize(vec2(cos(beamAngle), sin(beamAngle))), p);
    float beamGlow = exp(-abs(beam - 0.15 * sin(Time * 0.04)) * 3.2);

    vec3 deep = vec3(0.030, 0.028, 0.045);
    vec3 color = deep;

    color += Accent * pow(clouds, 2.2) * 0.55 * Intensity;
    color += Secondary * pow(veil, 3.0) * 0.30 * Intensity;
    color += mix(Accent, Secondary, 0.5) * beamGlow * 0.10 * Intensity;

    // Звёзды: редкая сетка точек, каждая со своей фазой мерцания.
    vec2 starGrid = uv * Resolution / 3.0;
    vec2 cell = floor(starGrid);
    float star = hash21(cell);
    if (star > 0.9965) {
        vec2 local = fract(starGrid) - 0.5;
        float spark = exp(-dot(local, local) * 22.0);
        float twinkle = 0.55 + 0.45 * sin(Time * 1.7 + star * 90.0);
        color += vec3(0.85, 0.88, 1.0) * spark * twinkle * 0.9;
    }

    // Виньетка прижимает края, чтобы колонка меню читалась в центре.
    float vignette = smoothstep(1.15, 0.25, length(p * vec2(1.0, 1.25)));
    color *= 0.35 + 0.65 * vignette;

    // Зерно: без него плавные градиенты идут ступенями на тёмном.
    float grain = (hash21(uv * Resolution + fract(Time) * 91.7) - 0.5) * 0.012;
    color += grain;

    OutColor = vec4(max(color, vec3(0.0)), 1.0) * FragColor;
}
