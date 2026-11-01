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

// МАТРИЦА. Рука становится экраном, по которому сверху вниз сыплется код. Силуэт держится не
// цветом руки — он почти погашен, — а самим потоком: столбцы обрываются на кромке, и форма
// читается по тому, где ливень кончается.
//
// Столбец живёт сам по себе: своя скорость, своя фаза, свой набор знаков. Общего таймера нет
// специально — синхронный дождь сразу выдаёт себя как узор, а не как поток данных.

/** Знак в ячейке: сетка три на пять зажжённых точек, как на семисегментнике. */
float glyph(vec2 cellUv, vec2 cellId, float flick) {
    vec2 bit = floor(cellUv * vec2(3.0, 5.0));
    if (bit.x < 0.0 || bit.y < 0.0 || bit.x > 2.0 || bit.y > 4.0) {
        return 0.0;
    }

    // Знак меняется скачками, а не плавно: символ должен перещёлкиваться целиком.
    float seed = hash2(cellId * 1.37 + vec2(flick, flick * 0.7));
    float lit = hash2(bit + cellId * 7.31 + seed * 41.0);

    // Небольшой зазор между точками — иначе знак сливается в сплошной прямоугольник.
    vec2 inner = fract(cellUv * vec2(3.0, 5.0));
    float gap = smoothstep(0.06, 0.22, inner.x) * smoothstep(0.06, 0.22, inner.y)
            * smoothstep(0.06, 0.22, 1.0 - inner.x) * smoothstep(0.06, 0.22, 1.0 - inner.y);

    return step(0.42, lit) * gap;
}

void main() {
    float mask = sampleMask(handUv);
    if (mask <= 0.001) {
        discard;
    }

    float rim = smoothstep(0.0, 1.0, rimBand(handUv, 2.4));
    vec2 c = handCoord(handUv);

    vec2 grid = c * vec2(25.0, 33.0);
    vec2 cellId = floor(grid);
    vec2 cellUv = fract(grid);

    // Ливень: голова потока едет вниз, за ней экспоненциальный хвост. mod замыкает поток в
    // кольцо, поэтому столбец идёт непрерывно и не приходится плодить копии.
    float speed = 0.55 + 1.35 * hash2(vec2(cellId.x, 11.0));
    float phase = hash2(vec2(cellId.x, 4.0)) * 40.0;
    float head = -(time * speed * 2.1 + phase);
    float behind = mod(cellId.y - head, 13.0);
    float tail = exp(-behind * 0.34);

    // Голова горит белым и держится чуть шире одной ячейки, чтобы не мигала на границах.
    float headGlow = exp(-behind * 2.6);

    float symbol = glyph(cellUv, cellId, floor(time * 3.0 + hash2(cellId) * 9.0));
    float rain = symbol * (tail * 1.65 + headGlow * 2.6);

    // Редкие столбцы вспыхивают целиком — как будто по ним прошёл запрос.
    float burst = step(0.985, hash2(vec2(cellId.x, floor(time * 0.7)))) * exp(-behind * 0.06);
    rain += symbol * burst * 0.9;

    vec3 trailColor = mix(PrimaryColor, SecondaryColor, clamp(behind / 16.0, 0.0, 1.0));
    vec3 color = trailColor * rain;
    color += vec3(1.0) * symbol * headGlow * 0.85;

    // Подложка: почти погашенная рука, чтобы силуэт не пропадал между столбцами.
    color += PrimaryColor * 0.26 * (0.4 + 0.6 * rimBand(handUv, 6.0));
    color += mix(PrimaryColor, vec3(1.0), 0.35) * pow(rim, 2.0) * 2.20;

    // Развёртка: слабая горизонтальная полоса, ползущая по руке снизу вверх.
    float sweep = exp(-abs(fract(c.y * 0.4 + 0.5) - fract(time * 0.18)) * 20.0);
    color += PrimaryColor * sweep * 0.35;

    color = vec3(1.0) - exp(-color * (0.9 + Brightness * 0.45));

    float alpha = clamp(0.46 + rain * 0.8 + rim * 0.5 + burst * 0.3, 0.0, 1.0);
    outColor = vec4(color, mask * alpha * effectAlpha);
}
