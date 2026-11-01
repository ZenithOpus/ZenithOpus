#version 150

// Гаусс в один проход по одной оси. Цепочка вызывает его дважды — сначала по X, потом по Y, —
// и это не экономия ради экономии: двумерное ядро радиуса R стоит R^2 отсчётов, два раздельных
// прохода дают тот же результат за 2R. Ванилла для своего свечения делает так же.
//
// Направление приходит из вершинного шейдера через sampleStep: BlurDir задан в самом проходе
// цепочки, а не из Java, потому что у двух проходов он разный, а Java ставит униформу всем
// проходам сразу.

uniform sampler2D InSampler;
uniform float Radius;

in vec2 texCoord;
in vec2 sampleStep;

out vec4 fragColor;

// Число отсчётов в одну сторону. Шаг растягивается под радиус, поэтому цена не зависит от него,
// а провалы между отсчётами добирает билинейная выборка — вход помечен bilinear в цепочке.
const int TAPS = 8;

void main() {
    if (Radius <= 0.01) {
        fragColor = texture(InSampler, texCoord);
        return;
    }

    float step = Radius / float(TAPS);
    // Сигма выбрана так, чтобы ядро гасло к самому краю радиуса: на 3 сигма вес около 0.011,
    // обрыв на таком уровне глазом не читается как кольцо.
    float sigma = Radius / 3.0;
    float twoSigmaSq = 2.0 * sigma * sigma;

    vec4 sum = texture(InSampler, texCoord);
    float total = 1.0;

    for (int i = 1; i <= TAPS; i++) {
        float offset = float(i) * step;
        float weight = exp(-offset * offset / twoSigmaSq);
        sum += texture(InSampler, texCoord + sampleStep * offset) * weight;
        sum += texture(InSampler, texCoord - sampleStep * offset) * weight;
        total += weight * 2.0;
    }

    fragColor = sum / total;
}
