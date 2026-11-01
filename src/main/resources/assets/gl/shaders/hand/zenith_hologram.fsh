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
    return smoothstep(0.999, 0.990, texture(DepthTexture, clamp(sampleUv, 0.0, 1.0)).r);
}

/**
 * Маска силуэта.
 *
 * За краем кадра берём ближайший пиксель, а не ноль. Ноль означал бы, что там руки нет, то есть
 * объявлял границу экрана краем силуэта — и кольца отсчётов находили по ней кромку, которой на
 * самом деле нет. Рука, уходящая за край экрана, получала вдоль него рамку и обрывалась.
 */
float sampleMask(vec2 sampleUv) {
    vec2 clamped = clamp(sampleUv, 0.0, 1.0);
    return max(texture(ColorTexture, clamped).a, handDepthMask(clamped));
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
// ГОЛОГРАММА. Узнаваемой её делает не цвет, а признаки плохой проекции: строки развёртки,
// бегущий луч, срывы строк вбок и разбег цветовых каналов.
uniform float ScanDensity;
uniform float Glitch;

void main() {
    vec2 uv = handUv;

    // Срыв: строка целиком уезжает вбок. Полосы редкие — постоянная тряска читается поломкой.
    float band = floor(uv.y * 90.0);
    float roll = hash2(vec2(band, floor(time * 6.0)));
    uv.x += step(0.94, roll) * (hash2(vec2(band, 3.7)) - 0.5) * 0.06 * Glitch;

    float mask = sampleMask(uv);
    if (mask <= 0.001) {
        discard;
    }

    float rim = smoothstep(0.0, 1.0, rimBand(uv, 2.4));
    vec2 c = handCoord(uv);
    vec3 tint = mix(PrimaryColor, SecondaryColor, clamp(c.y * 1.4 + 0.5, 0.0, 1.0));

    float scan = sin(c.y * ScanDensity + time * 2.5);
    float lines = 0.55 + 0.45 * smoothstep(-0.2, 0.6, scan);

    // Разбег каналов: красный и синий берут строки со сдвигом по фазе.
    float split = 0.18 * Glitch;
    vec3 channels = vec3(
            0.55 + 0.45 * smoothstep(-0.2, 0.6, sin(c.y * ScanDensity + time * 2.5 + split)),
            lines,
            0.55 + 0.45 * smoothstep(-0.2, 0.6, sin(c.y * ScanDensity + time * 2.5 - split))
    );

    // Луч построчной развёртки ползёт снизу вверх.
    float sweep = exp(-abs(fract(c.y * 0.5 + 0.5) - fract(time * 0.35)) * 26.0);

    vec3 color = tint * channels * (0.55 + rim * 0.9);
    color += tint * sweep * 1.4;
    color += mix(tint, vec3(1.0), 0.5) * pow(rim, 3.0) * 0.7;
    color *= (1.0 - Glitch * 0.18 * hash2(vec2(floor(time * 14.0), 1.0))) * (0.55 + Brightness * 0.35);

    color = vec3(1.0) - exp(-color * (0.9 + Brightness * 0.4));

    // Проекция просвечивает: плотнее у кромки и по строкам, почти прозрачна в середине.
    float alpha = clamp(0.30 + rim * 0.55 + sweep * 0.35, 0.0, 0.95) * lines;
    outColor = vec4(color, mask * alpha * effectAlpha);
}
