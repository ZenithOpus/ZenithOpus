#version 330

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

// Первый проход свечения: из буфера обводки сущностей достаём поле «силуэт + его цвет».
//
// Отдельный проход, а не размытие прямо по entity_outline, потому что альфа там не единственный
// признак: ванилла пишет туда сущность цветом свечения, и в разных случаях заполнены разные
// каналы. Покрытие — максимум по всем; брать максимум до размытия и после — разные вещи, и верно
// именно до, иначе полупрозрачные края силуэта дадут ореол не той плотности.
//
// Цвет выводится домноженным на покрытие (premultiplied): так его можно размывать теми же
// проходами, что и саму маску, а в сборке вернуть делением на альфу. Благодаря этому цвет
// свечения приходит от самой сущности — модуль задаёт его через team color, и рядом стоящие
// цели светятся каждая своим.

uniform sampler2D InSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

// Снаряды Predictions лежат в том же буфере и помечены чисто чёрным. Они не наши: их поле
// свечения строить нельзя, иначе ореол радиусом в десятки пикселей раздувает жемчуг.
bool zenith_foreign(vec4 color) {
    return color.a > 0.5 && max(color.r, max(color.g, color.b)) < 0.02;
}

void main() {
    vec4 source = texture(InSampler, texCoord);
    if (zenith_foreign(source)) {
        fragColor = vec4(0.0);
        return;
    }
    float mask = clamp(max(source.a, max(source.r, max(source.g, source.b))), 0.0, 1.0);
    fragColor = vec4(source.rgb * mask, mask);
}
