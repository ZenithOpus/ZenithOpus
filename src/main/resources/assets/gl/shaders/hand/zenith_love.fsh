#version 330 core

// Общая часть шейдеров руки. Рука уже нарисована в отдельный буфер, и сюда приходит её цвет
// вместе с глубиной — ни нормалей, ни развёртки скина здесь нет. Поэтому всё, что в чамсах
// берётся из геометрии, тут приходится добывать из картинки: силуэт — из маски, кромка — из
// её края, объём — из перепада глубины.

in vec2 handUv;
out vec4 outColor;

uniform sampler2D ColorTexture;
uniform sampler2D DepthTexture;
uniform vec2 resolution;
uniform float time;
uniform vec2 handMotion;
uniform float effectAlpha;
/** Сдвиг узора вслед за качанием руки при повороте камеры. */
uniform vec2 handSway;

uniform vec3 PrimaryColor;
uniform vec3 SecondaryColor;
uniform float Brightness;

/** Есть ли рука в этой точке по глубине. Фон остаётся на дальней плоскости, рука ближе. */
float handDepthMask(vec2 sampleUv) {
    if (sampleUv.x < 0.0 || sampleUv.y < 0.0 || sampleUv.x > 1.0 || sampleUv.y > 1.0) {
        return 0.0;
    }
    return smoothstep(0.999, 0.990, texture(DepthTexture, sampleUv).r);
}

float sampleMask(vec2 sampleUv) {
    if (sampleUv.x < 0.0 || sampleUv.y < 0.0 || sampleUv.x > 1.0 || sampleUv.y > 1.0) {
        return 0.0;
    }
    return max(texture(ColorTexture, sampleUv).a, handDepthMask(sampleUv));
}

/**
 * Полоса вдоль края силуэта — замена френелю. В чамсах кромка бралась из угла между нормалью
 * и взглядом, но нормалей тут нет. Зато край виден по маске: смотрим вокруг точки и считаем,
 * сколько соседей уже вне руки. Чем больше — тем ближе к краю.
 */
float rimBand(vec2 sampleUv, float radius) {
    vec2 px = radius / resolution;
    float acc = 0.0;
    acc += sampleMask(sampleUv + vec2(px.x, 0.0));
    acc += sampleMask(sampleUv - vec2(px.x, 0.0));
    acc += sampleMask(sampleUv + vec2(0.0, px.y));
    acc += sampleMask(sampleUv - vec2(0.0, px.y));
    acc += sampleMask(sampleUv + px * 0.7);
    acc += sampleMask(sampleUv - px * 0.7);
    acc += sampleMask(sampleUv + vec2(px.x, -px.y) * 0.7);
    acc += sampleMask(sampleUv + vec2(-px.x, px.y) * 0.7);
    return clamp(1.0 - acc / 8.0, 0.0, 1.0);
}

/** Координаты узора, привязанные к руке, а не к экрану: иначе рисунок плывёт при повороте. */
vec2 handCoord(vec2 sampleUv) {
    return (sampleUv - handMotion - handSway) * vec2(resolution.x / resolution.y, 1.0);
}

float hash2(vec2 p) {
    return fract(sin(dot(mod(p, 512.0), vec2(127.1, 311.7))) * 43758.5453);
}

float vnoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    float a = hash2(i);
    float b = hash2(i + vec2(1.0, 0.0));
    float c = hash2(i + vec2(0.0, 1.0));
    float d = hash2(i + vec2(1.0, 1.0));
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm(vec2 p) {
    return vnoise(p) * 0.65 + vnoise(p * 2.7) * 0.35;
}
// ЛЮБОВЬ. Тёмный бархат с горящими сердцами. Светящееся видно только на тёмном, поэтому
// основа намеренно глубокая, а вся яркость отдана сердцам и кромке.
const int HEART_ROWS[9] = int[9](0x0C6, 0x1EF, 0x1FF, 0x1FF, 0x1FF, 0x0FE, 0x07C, 0x038, 0x010);

/** Сердце из хотбара, 9 на 9. Пиксельная форма на малом размере читается лучше гладкой. */
float heartMask(vec2 p, float size) {
    vec2 uv = p / (size * 2.0) + 0.5;
    if (uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0) {
        return 0.0;
    }
    ivec2 cell = clamp(ivec2(floor(uv * 9.0)), ivec2(0), ivec2(8));
    return float((HEART_ROWS[8 - cell.y] >> (8 - cell.x)) & 1);
}

void main() {
    float mask = sampleMask(handUv);
    if (mask <= 0.001) {
        discard;
    }

    float rim = smoothstep(0.0, 1.0, rimBand(handUv, 2.2));
    vec2 c = handCoord(handUv);
    float ramp = smoothstep(0.0, 1.0, clamp(c.y * 1.6 + 0.5, 0.0, 1.0));

    vec3 color = mix(vec3(0.13, 0.01, 0.07), vec3(0.42, 0.05, 0.20), ramp);
    color += vec3(1.0, 0.32, 0.55) * pow(rim, 1.6) * 0.30;

    // Сетка сердец плывёт по времени; заворачиваем его, иначе синус теряет точность.
    float flowTime = mod(time, 512.0);
    vec2 grid = c * 7.0 + vec2(sin(flowTime * 0.30) * 0.12, -flowTime * 0.09);
    vec2 cell = floor(grid);
    vec2 local = fract(grid) - 0.5;

    float seed = hash2(cell);
    float filled = step(0.40, seed);
    vec2 p = local - (vec2(hash2(cell + 3.0), hash2(cell + 9.0)) - 0.5) * 0.40;
    float size = 0.26 + hash2(cell + 21.0) * 0.12;

    float heart = heartMask(p, size) * filled;
    float beat = 0.55 + 0.45 * sin(flowTime * 2.8 + seed * 6.28);
    float halo = exp(-length(p) / (size * 0.55)) * filled;

    color += vec3(1.0, 0.20, 0.38) * halo * (0.6 + 1.4 * beat);
    color = mix(color, mix(vec3(1.0, 0.25, 0.42), vec3(1.0, 0.85, 0.90), beat * 0.55) * (1.6 + 1.2 * beat), heart);
    color += vec3(1.0, 0.32, 0.55) * rim * 0.55;

    color *= 0.55 + Brightness * 0.30;
    color = vec3(1.0) - exp(-color * (0.9 + Brightness * 0.4));
    outColor = vec4(color, mask * effectAlpha);
}