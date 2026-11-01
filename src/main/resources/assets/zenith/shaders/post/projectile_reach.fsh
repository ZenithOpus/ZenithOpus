#version 330

// Карта досягаемости пламени: 1 там, где в окрестности есть пиксель снаряда, 0 — где нет.
//
// Шейдер огня на каждый пиксель экрана делал 24 шага с fbm (около пятисот sin), хотя пламя,
// контур и тело могут появиться только в пределах ~45 px от силуэта снаряда. Три прохода этого
// шейдера с шагом 1, 5 и 25 растягивают маску квадратом 5×5 до ±62 px, и шейдер огня по одной
// выборке отсюда сразу отдаёт нетронутый пиксель всему остальному экрану. Картинка та же:
// вне карты огонь и так ничего не рисовал.

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};
layout(std140) uniform ZenithPost {
    float Step;
    /** 1 — вход это буфер обводки и снаряд ищем по чёрной метке; 0 — вход это прошлая карта. */
    float FromOutline;
};

uniform sampler2D InSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

bool hit(vec4 c) {
    if (FromOutline > 0.5) {
        // Та же метка, что isProjectile в projectile_fire (Predictions#SHADER_GLOW_MARKER).
        return c.a > 0.5 && max(c.r, max(c.g, c.b)) < 0.02;
    }
    return c.r > 0.5;
}

void main() {
    vec2 stepUv = oneTexel * Step;
    for (int dy = -2; dy <= 2; dy++) {
        for (int dx = -2; dx <= 2; dx++) {
            if (hit(texture(InSampler, texCoord + vec2(float(dx), float(dy)) * stepUv))) {
                fragColor = vec4(1.0);
                return;
            }
        }
    }
    fragColor = vec4(0.0);
}
