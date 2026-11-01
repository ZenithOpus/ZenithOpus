#version 330 core

// Общая часть шейдеров руки. Рука уже нарисована в отдельный буфер, и сюда приходит её цвет
// вместе с глубиной — ни нормалей, ни развёртки скина здесь нет. Поэтому всё, что в чамсах
// берётся из геометрии, тут приходится добывать из картинки: силуэт — из маски, кромка — из
// её края, объём — из перепада глубины.

in vec2 handUv;
out vec4 outColor;

uniform sampler2D ColorTexture;
uniform sampler2D DepthTexture;
/** Кадр без руки: мир, который стекло пропускает сквозь себя. */
uniform sampler2D SceneTexture;
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
// СТЕКЛО. Рука превращается в толщу прозрачного вещества: сквозь неё видно мир, но сдвинутый
// преломлением и слегка расфокусированный, а форму держат блик по наклону и светлая кромка.
//
// Мир сюда приходит отдельной картинкой (SceneTexture), снятой до отрисовки руки. Читать его из
// главного буфера прямо здесь нельзя: этот же буфер — цель записи прохода.
//
// Нормалей у прохода нет, зато есть своя глубина руки. Её перепад между соседними текселями и
// работает наклоном поверхности: на плоскости он около нуля, на скруглении и у силуэта растёт.
// Отсюда берётся и направление преломления, и блик.

vec3 sceneAt(vec2 sampleUv) {
    return texture(SceneTexture, clamp(sampleUv, vec2(0.001), vec2(0.999))).rgb;
}

/** Матовость: короткое кольцо отсчётов вместо отдельного прохода блюра. */
vec3 sceneFrost(vec2 sampleUv, float spread) {
    vec2 r = spread / resolution;
    vec3 sum = sceneAt(sampleUv);
    sum += sceneAt(sampleUv + vec2(r.x, 0.0));
    sum += sceneAt(sampleUv - vec2(r.x, 0.0));
    sum += sceneAt(sampleUv + vec2(0.0, r.y));
    sum += sceneAt(sampleUv - vec2(0.0, r.y));
    return sum / 5.0;
}

void main() {
    float mask = sampleMask(handUv);
    if (mask <= 0.001) {
        discard;
    }

    vec2 texel = 1.0 / resolution;

    // Наклон поверхности из перепада глубины. Множитель большой потому, что глубина нелинейна:
    // у самой камеры разница между соседними текселями идёт в тысячных долях.
    float dx = texture(DepthTexture, clamp(handUv + vec2(texel.x, 0.0), vec2(0.0), vec2(1.0))).r
             - texture(DepthTexture, clamp(handUv - vec2(texel.x, 0.0), vec2(0.0), vec2(1.0))).r;
    float dy = texture(DepthTexture, clamp(handUv + vec2(0.0, texel.y), vec2(0.0), vec2(1.0))).r
             - texture(DepthTexture, clamp(handUv - vec2(0.0, texel.y), vec2(0.0), vec2(1.0))).r;
    vec2 slope = clamp(vec2(dx, dy) * 260.0, vec2(-1.0), vec2(1.0));

    float rim = smoothstep(0.0, 1.0, rimBand(handUv, 2.6));

    // Преломление: середина уводит мир едва заметно, у граней — заметно вбок. Второе слагаемое
    // разводит картинку от центра руки, чтобы толща читалась даже на плоском участке.
    vec2 refraction = slope * 0.035
            + normalize(handCoord(handUv) + vec2(0.0001)) * rim * 0.020;

    // Мутность растёт к кромке: у грани луч идёт сквозь большую толщу.
    vec3 behind = sceneFrost(handUv + refraction, 1.5 + rim * 5.0);

    vec3 glassTone = vec3(0.86, 0.94, 1.0);
    // Проходящий свет: холодный оттенок и небольшая потеря на поглощение.
    vec3 color = behind * mix(vec3(1.0), glassTone, 0.55) * 0.90;

    // Собственная светотень руки. Без неё стекло теряет объём и читается дыркой в кадре: доля
    // маленькая, цвет скина сквозь неё не проступает, а складки и тени остаются.
    vec3 own = texture(ColorTexture, handUv).rgb;
    float shade = clamp(dot(own, vec3(0.299, 0.587, 0.114)), 0.0, 1.0);
    color *= 0.78 + 0.44 * shade;

    // Блик по наклону: узкая полоса света там, где поверхность отворачивается от камеры.
    float spec = pow(clamp(length(slope), 0.0, 1.0), 0.65);
    color += glassTone * spec * 0.32 * Brightness;

    // Кант почти белый: именно он держит форму на любом фоне.
    color += mix(glassTone, vec3(1.0), 0.7) * pow(rim, 1.4) * 0.50 * Brightness;

    outColor = vec4(color, mask * effectAlpha);
}
