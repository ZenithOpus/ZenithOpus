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

// ЛЁД. Прошлая версия строилась на цвете самой руки: он размывался, подкрашивался в голубой
// и обводился кантом. Днём это читалось бледно, а ночью рука приходит почти чёрной — и стекло
// схлопывалось в чёрный силуэт с белой каймой. Значит, материал не должен зависеть от того,
// сколько света достаётся руке.
//
// Здесь он собран заново, вокруг поддельной нормали. Маску размываем и берём её градиент:
// внутри силуэта она ровная, у края круто падает — получается скруглённая по краям поверхность.
// Дальше это обычное стекло: френель по нормали, блики от двух источников, преломление со
// сдвигом по нормали и разбегом каналов. Всё это своё, а не унаследованное от руки, поэтому в
// темноте стекло остаётся стеклом — темнеет только то, что сквозь него видно.

/**
 * Размытая маска: у края около половины, в глубине единица. Заодно это и толщина стекла.
 * Два кольца, а не одно: по одному кольцу нормаль выходит ступеньками, и они видны на фаске.
 */
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

/**
 * Нормаль из маски. Наклон берём с двух масштабов: крупный даёт общий объём, мелкий — фаску
 * у самого края. Знак минус потому, что маска растёт внутрь, а нормаль смотрит наружу.
 */
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

    return normalize(vec3(-slope * 5.5, 0.34));
}

void main() {
    float mask = sampleMask(handUv);
    if (mask <= 0.001) {
        discard;
    }

    vec3 normal = maskNormal(handUv);
    vec3 view = vec3(0.0, 0.0, 1.0);
    float facing = clamp(dot(normal, view), 0.0, 1.0);
    float fresnel = pow(1.0 - facing, 2.0);
    float thickness = softMask(handUv, 11.0);

    // Преломление: чем сильнее наклон, тем дальше уходит выборка. Каналы разводим — по краям
    // появляется цветная кайма, как на настоящей толстой грани.
    vec2 bend = normal.xy * (0.028 + 0.055 * fresnel);
    vec2 base = clamp(handUv + bend, vec2(0.002), vec2(0.998));
    vec3 behind = vec3(
            texture(ColorTexture, clamp(base + bend * 0.16, vec2(0.002), vec2(0.998))).r,
            texture(ColorTexture, base).g,
            texture(ColorTexture, clamp(base - bend * 0.16, vec2(0.002), vec2(0.998))).b
    );

    // Матовость: короткое кольцо отсчётов вместо отдельного прохода блюра.
    vec3 frost = behind;
    frost += texture(ColorTexture, clamp(base + vec2(0.005, 0.0), vec2(0.002), vec2(0.998))).rgb;
    frost += texture(ColorTexture, clamp(base - vec2(0.005, 0.0), vec2(0.002), vec2(0.998))).rgb;
    frost += texture(ColorTexture, clamp(base + vec2(0.0, 0.005), vec2(0.002), vec2(0.998))).rgb;
    frost += texture(ColorTexture, clamp(base - vec2(0.0, 0.005), vec2(0.002), vec2(0.998))).rgb;
    frost /= 5.0;

    // Собственный цвет стекла. Он есть всегда, поэтому в темноте материал не пропадает: то, что
    // видно сквозь него, лишь подмешивается сверху и на общую светимость почти не влияет.
    vec3 tint = vec3(0.58, 0.76, 0.94);
    float depthTone = mix(0.34, 0.92, thickness);
    vec3 color = tint * depthTone * 0.72;
    color += frost * 0.60;

    // Два блика: широкий сверху-слева, узкий и жёсткий снизу-справа. Они и делают поверхность
    // стеклом — без них любая подсветка читается как крашеный силуэт.
    vec3 keyLight = normalize(vec3(-0.55, 0.72, 0.62));
    vec3 fillLight = normalize(vec3(0.68, -0.45, 0.55));
    vec3 halfVec = normalize(normal + view);
    float keySpec = pow(clamp(dot(halfVec, keyLight), 0.0, 1.0), 14.0);
    float fillSpec = pow(clamp(dot(halfVec, fillLight), 0.0, 1.0), 40.0);
    color += vec3(0.90, 0.96, 1.0) * keySpec * 2.10;
    color += vec3(1.0) * fillSpec * 1.60;

    // Внутренние блики: рябь по толще, чтобы середина не была ровной заливкой. Ей же стекло и
    // отличается от крашеного силуэта — свет внутри должен куда-то собираться.
    vec2 flow = handCoord(handUv) * 17.0 + normal.xy * 3.2;
    float caustic = fbm(flow + vec2(time * 0.22, -time * 0.15));
    caustic = smoothstep(0.42, 0.88, caustic);
    color += mix(tint, vec3(1.0), 0.35) * caustic * thickness * 0.85;

    // Свет сверху: без него толща читается ровной заливкой независимо от бликов.
    color *= 0.82 + 0.34 * smoothstep(-0.12, 0.14, handCoord(handUv).y);

    // Скользящий по стеклу отблеск: чуть-чуть движения, чтобы материал не выглядел печатью.
    float sweep = exp(-abs(handCoord(handUv).x + handCoord(handUv).y * 0.6 - sin(time * 0.22) * 0.5) * 5.5);
    color += vec3(0.70, 0.85, 1.0) * sweep * (0.25 + fresnel) * 0.75;

    // Френелевая кромка вместо прежнего белого канта: ярче там, где грань уходит от взгляда.
    color += mix(tint, vec3(1.0), 0.75) * fresnel * 1.25;
    color += vec3(1.0) * pow(fresnel, 6.0) * 0.85;

    color = vec3(1.0) - exp(-color * (0.85 + Brightness * 0.35));

    // Стекло прозрачное в толще и плотное на гранях — там, где свет уходит вбок. Полностью
    // прозрачным середину не делаем: на тёмном фоне рука превратилась бы в дыру.
    float alpha = clamp(0.52 + fresnel * 0.48 + keySpec * 0.5 + fillSpec * 0.4 + caustic * 0.25, 0.0, 1.0);
    outColor = vec4(color, mask * alpha * effectAlpha);
}
