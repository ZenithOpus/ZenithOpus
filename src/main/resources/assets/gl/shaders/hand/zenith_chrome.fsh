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

// ХРОМ. Жидкий металл: рука не светится сама, она отражает. Настоящей сцены для отражения нет,
// поэтому окружение сочиняется на месте — светлый верх, тёмный низ и пара студийных полос. Их
// достаточно: хром узнаётся не по картинке в отражении, а по тому, как та ползёт по форме.
//
// Цвет здесь не настраивается: у хрома его нет. Радужность на кромках — это тонкая плёнка, она
// живёт от угла, а не от палитры, и любой заданный оттенок её бы просто перекрасил в один тон.

/** Размытая маска — из неё дальше получается нормаль. Два кольца, иначе на бликах ступеньки. */
float softMask(vec2 sampleUv, float radius) {
    vec2 outer = radius / resolution;
    vec2 inner = outer * 0.5;
    float acc = sampleMask(sampleUv) * 3.0;

    acc += sampleMask(sampleUv + vec2(outer.x, 0.0));
    acc += sampleMask(sampleUv - vec2(outer.x, 0.0));
    acc += sampleMask(sampleUv + vec2(0.0, outer.y));
    acc += sampleMask(sampleUv - vec2(0.0, outer.y));
    acc += sampleMask(sampleUv + outer * 0.7);
    acc += sampleMask(sampleUv - outer * 0.7);
    acc += sampleMask(sampleUv + vec2(outer.x, -outer.y) * 0.7);
    acc += sampleMask(sampleUv + vec2(-outer.x, outer.y) * 0.7);

    acc += sampleMask(sampleUv + vec2(inner.x, 0.0)) * 1.5;
    acc += sampleMask(sampleUv - vec2(inner.x, 0.0)) * 1.5;
    acc += sampleMask(sampleUv + vec2(0.0, inner.y)) * 1.5;
    acc += sampleMask(sampleUv - vec2(0.0, inner.y)) * 1.5;
    acc += sampleMask(sampleUv + inner * 0.7) * 1.5;
    acc += sampleMask(sampleUv - inner * 0.7) * 1.5;
    acc += sampleMask(sampleUv + vec2(inner.x, -inner.y) * 0.7) * 1.5;
    acc += sampleMask(sampleUv + vec2(-inner.x, inner.y) * 0.7) * 1.5;

    return acc / 23.0;
}

vec3 maskNormal(vec2 sampleUv) {
    vec2 wide = 13.0 / resolution;
    vec2 fine = 3.0 / resolution;

    vec2 slope = vec2(
            softMask(sampleUv + vec2(wide.x, 0.0), 11.0) - softMask(sampleUv - vec2(wide.x, 0.0), 11.0),
            softMask(sampleUv + vec2(0.0, wide.y), 11.0) - softMask(sampleUv - vec2(0.0, wide.y), 11.0)
    );
    slope += vec2(
            sampleMask(sampleUv + vec2(fine.x, 0.0)) - sampleMask(sampleUv - vec2(fine.x, 0.0)),
            sampleMask(sampleUv + vec2(0.0, fine.y)) - sampleMask(sampleUv - vec2(0.0, fine.y))
    ) * 0.45;

    return normalize(vec3(-slope * 6.5, 0.32));
}

/** Придуманное окружение: студия с двумя софтбоксами. Читается по направлению отражённого луча. */
vec3 environment(vec3 dir) {
    float height = dir.y;

    vec3 sky = mix(vec3(0.10, 0.12, 0.18), vec3(0.66, 0.74, 0.92), smoothstep(-0.55, 0.85, height));
    sky = mix(sky, vec3(0.17, 0.18, 0.23), smoothstep(-0.15, -0.85, height));

    // Софтбоксы: широкая полоса сверху и узкая контровая снизу. Именно они дают хрому «стекание».
    float key = exp(-abs(height - 0.62) * 9.0);
    float kick = exp(-abs(height + 0.34) * 17.0);
    sky += vec3(1.0, 0.98, 0.94) * key * 1.90;
    sky += vec3(0.72, 0.82, 1.0) * kick * 0.55;

    // Вертикальные стойки студии — по ним видно, что отражение поворачивается вместе с рукой.
    float angle = atan(dir.x, max(abs(dir.z), 0.05));
    sky += vec3(0.85, 0.9, 1.0) * pow(max(sin(angle * 5.0 + time * 0.12), 0.0), 22.0) * 0.5;

    return sky;
}

void main() {
    float mask = sampleMask(handUv);
    if (mask <= 0.001) {
        discard;
    }

    vec3 normal = maskNormal(handUv);
    vec3 view = vec3(0.0, 0.0, 1.0);
    float facing = clamp(dot(normal, view), 0.0, 1.0);
    float fresnel = pow(1.0 - facing, 3.4);

    // Металл течёт: нормаль слегка гуляет от медленного шума, и отражение ползёт по поверхности.
    vec2 c = handCoord(handUv);
    vec2 ripple = vec2(
            fbm(c * 9.0 + vec2(time * 0.16, 0.0)) - 0.5,
            fbm(c * 9.0 + vec2(0.0, time * 0.13) + 21.7) - 0.5
    );
    vec3 flowNormal = normalize(normal + vec3(ripple * 0.22, 0.0));

    vec3 reflected = reflect(-view, flowNormal);
    vec3 color = environment(reflected);

    // Хром почти не имеет своего цвета, но чуть холодит отражение — иначе выглядит как зеркало.
    color *= vec3(0.88, 0.92, 1.0);

    // Тонкая плёнка на кромках. Три канала берут волну со сдвигом фазы — так и получается
    // бензиновый перелив, без всяких таблиц.
    float film = fresnel * 2.1 + fbm(c * 6.0) * 0.6 + time * 0.05;
    vec3 iridescence = 0.5 + 0.5 * cos(6.28318 * (vec3(0.0, 0.33, 0.67) + film));
    color = mix(color, color * iridescence * 1.7, clamp(fresnel * 0.70, 0.0, 0.34));

    // Жёсткий блик: у полированного металла он маленький и очень яркий.
    vec3 keyLight = normalize(vec3(-0.42, 0.78, 0.46));
    float spec = pow(clamp(dot(normalize(flowNormal + view), keyLight), 0.0, 1.0), 46.0);
    color += vec3(1.0) * spec * 2.4;

    // Кромка: тонкая светлая грань, как заполированный край детали.
    float rim = smoothstep(0.0, 1.0, rimBand(handUv, 2.2));
    color += vec3(0.92, 0.96, 1.0) * pow(rim, 2.5) * 1.1;

    color = vec3(1.0) - exp(-color * (0.85 + Brightness * 0.30));
    outColor = vec4(color, mask * effectAlpha);
}
