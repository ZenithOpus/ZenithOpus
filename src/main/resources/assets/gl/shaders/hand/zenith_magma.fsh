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

// МАГМА. Своей текстуры у режима нет: основа — сама рука, как её нарисовала игра, вместе с
// освещением. Поверх идут три зоны от одной линии: прогар, тление и расплав. Переход
// «целая кожа → копоть → угли → расплав» и читается как ожог; без прогара лава выглядит
// наклейкой, потому что настоящий расплав всегда оставляет след вокруг себя.
uniform float FlowSpeed;

void main() {
    vec4 hand = texture(ColorTexture, handUv);
    float mask = sampleMask(handUv);
    if (mask <= 0.001) {
        discard;
    }

    float rim = smoothstep(0.0, 1.0, rimBand(handUv, 2.4));
    float t = mod(time, 512.0) * FlowSpeed;

    // Растяжение по вертикали слабое: сильное даёт неестественно длинные полосы вместо
    // подтёков. Частота высокая — иначе на кисть приходится меньше одной ячейки шума.
    vec2 c = handCoord(handUv);
    vec2 p = vec2(c.x * 14.0, c.y * 9.0 + t * 0.9);

    // Искажение области: без него подтёки едут жёсткой картинкой, с ним — извиваются на месте.
    vec2 warp = vec2(fbm(p * 0.35 + vec2(0.0, t * 0.10)), fbm(p * 0.35 + vec2(4.7, t * 0.08))) - 0.5;
    p += warp * vec2(1.6, 2.4);

    float n = fbm(p);
    float d = abs(n - 0.5);

    float coreWidth = 0.028;
    float core = 1.0 - smoothstep(0.0, coreWidth, d);
    float ember = 1.0 - smoothstep(coreWidth, coreWidth * 3.5, d);
    float charred = 1.0 - smoothstep(coreWidth, coreWidth * 6.0, d);
    // Ширина свечения своя, а не доля от ядра: тонкая нить обязана светить широко.
    float glow = 1.0 - smoothstep(coreWidth, coreWidth + 0.26, d);

    float surge = 0.60 + 0.40 * sin(n * 24.0 - t * 2.4);
    core *= 0.70 + 0.50 * surge;
    ember *= 0.65 + 0.55 * surge;

    float soot = smoothstep(0.40, 0.78, fbm(p * 1.6 + vec2(11.3, 4.1)));

    vec3 LAVA_DEEP = vec3(1.0, 0.16, 0.02);
    vec3 EMBER = vec3(1.0, 0.32, 0.05);

    // Кожа темнеет, но не исчезает: иначе теряется рисунок руки.
    vec3 color = hand.rgb;
    color *= mix(1.0, 0.55, soot * 0.85);
    color *= mix(1.0, 0.08, charred);

    // Всё горячее добавляется после и не гасится — раскалённое светит само.
    color += EMBER * ember * 1.06;
    color = mix(color, mix(LAVA_DEEP, vec3(1.0, 0.90, 0.55), pow(core, 2.0)) * 2.4, core);
    color += LAVA_DEEP * glow * glow * 2.0;
    color += EMBER * rim * 0.7;

    color *= 0.6 + Brightness * 0.35;
    color = vec3(1.0) - exp(-color * (0.9 + Brightness * 0.4));
    outColor = vec4(color, mask * effectAlpha);
}
