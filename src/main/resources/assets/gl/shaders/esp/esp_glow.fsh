#version 150

// Сборка свечения. На входе два буфера: In — исходные силуэты сущностей (ванильный
// entity_outline, куда модуль пишет свой цвет через team color), Glow — та же картинка, но
// размытая двумя предыдущими проходами и с домноженным на покрытие цветом.
//
// Всё строится вокруг размытого поля, и это главная идея. Снаружи силуэта оно уже готовый мягкий
// ореол, а внутри — расстояние до края: в глубине тела поле около единицы, у самой кромки около
// половины. Одна и та же величина работает и свечением снаружи, и координатой заливки внутри.
// Заливка из-за этого держится за сущность и не плывёт по экрану при повороте камеры.
//
// Радиус размытия Java держит не меньше ширины обводки: ранний выход отсекает пиксель по полю,
// и если бы поле не дотягивалось до полосы контура, контур пропал бы вместе с ним.

uniform sampler2D InSampler;
uniform sampler2D GlowSampler;

uniform float OutlineWidth;
uniform float GlowStrength;
uniform float FillOpacity;
/** Множитель яркости ореола: дыхание считает Java, здесь оно уже готовым числом. */
uniform float Pulse;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

const float TAU = 6.28318530718;
const int OUTLINE_DIRS = 12;

// Чисто чёрный пиксель — метка снаряда Predictions (Predictions#SHADER_GLOW_MARKER). Буфер
// обводки общий, и цепочка огня разбирает его после нас, поэтому такие пиксели мы не трогаем:
// ни в покрытие, ни в контур они не входят и до выхода доезжают нетронутыми.
bool zenith_foreign(vec4 color) {
    return color.a > 0.5 && max(color.r, max(color.g, color.b)) < 0.02;
}

float coverage(vec4 color) {
    if (zenith_foreign(color)) {
        return 0.0;
    }
    return clamp(max(color.a, max(color.r, max(color.g, color.b))), 0.0, 1.0);
}

void main() {
    vec4 source = texture(InSampler, texCoord);
    if (zenith_foreign(source)) {
        fragColor = source;
        return;
    }
    float mask = coverage(source);
    vec4 blurred = texture(GlowSampler, texCoord);
    float field = clamp(blurred.a, 0.0, 1.0);

    // Пусто и рядом ничего не светится — выходим сразу, это почти весь кадр.
    if (mask <= 0.001 && field <= 0.001) {
        fragColor = vec4(0.0);
        return;
    }

    // Цвет берётся из самой картинки: внутри силуэта — свой, снаружи — из размытого поля, где
    // он усреднён по соседям. Поэтому цвет здесь не настраивается: его выбирает модуль на
    // каждую сущность отдельно, а шейдер лишь переносит.
    vec3 tint = mask > 0.001 ? source.rgb : clamp(blurred.rgb / max(field, 0.001), 0.0, 1.0);

    // Полоса контура снаружи силуэта: смотрим по кругу на расстоянии ширины — если там силуэт
    // есть, а здесь нет, мы прямо за кромкой. Наружу, а не внутрь: у тонких целей вроде
    // предметов внутренний контур съел бы весь силуэт.
    float band = 0.0;
    vec3 bandTint = tint;
    if (OutlineWidth > 0.01) {
        for (int d = 0; d < OUTLINE_DIRS; d++) {
            float angle = TAU * (float(d) / float(OUTLINE_DIRS));
            vec4 nearby = texture(InSampler, texCoord + vec2(cos(angle), sin(angle)) * oneTexel * OutlineWidth);
            float edge = coverage(nearby) - mask;
            if (edge > band) {
                band = edge;
                bandTint = nearby.rgb;
            }
        }
    }
    float outlineAlpha = clamp(band, 0.0, 1.0);

    // Степень поджимает ореол к телу: без неё свечение расползается ровным пятном и выглядит
    // грязным.
    float halo = pow(clamp(field - mask, 0.0, 1.0), 1.35) * GlowStrength * Pulse;
    float glowAlpha = clamp(halo, 0.0, 1.0);

    // Внутри силуэта поле падает к краю — готовая координата «глубина внутрь тела»:
    // единица у самой кромки, ноль в середине.
    float rim = clamp((1.0 - field) * 2.0, 0.0, 1.0);
    float fillAlpha = mask > 0.001
            ? clamp(FillOpacity * (0.25 + 0.75 * smoothstep(0.05, 0.95, rim)), 0.0, 1.0)
            : 0.0;

    // Ядро контура уводим к белому: так грань читается на любом фоне, а цвет остаётся в
    // ореоле вокруг неё.
    vec3 outlineRgb = mix(bandTint, vec3(1.0), 0.35);

    // Складываем в предумноженном виде и делим в конце: смешивать напрямую нельзя, слои лежат
    // друг на друге и с разной прозрачностью.
    float glowOver = glowAlpha * (1.0 - fillAlpha);
    float alpha = clamp(fillAlpha + glowOver + outlineAlpha, 0.0, 1.0);
    vec3 rgb = tint * fillAlpha + tint * glowOver + outlineRgb * outlineAlpha;

    fragColor = vec4(clamp(rgb / max(alpha, 0.001), 0.0, 1.0), alpha);
}
