#version 330

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};
layout(std140) uniform ZenithPost {
    float NearPlane;
    float FarPlane;
    float WetAmount;
    float ReflectAmount;
    float PuddleAmount;
    float GlintAmount;
    float Frost;
    float MistAmount;
    vec4 MistTint4;
    float MistBase;
    float MistHeight;
    vec2 MistWind;
    float TerrainValid;
    float FadeEnd;
    float CoverRadius;
    float RainAmount;
    float time;
    vec4 CameraPosition4;
    float SkyPitch;
    float SkyYaw;
    float TanHalfFov;
    float Aspect;
    vec4 SkyZenith4;
    vec4 SkyHorizon4;
    vec4 SkyNadir4;
    vec4 SunDir4;
    vec4 SunColor4;
    float SunSize;
    float SunGlare;
    float CloudCover;
    float CloudDark;
    float CloudSpeed;
    float CloudScale;
    vec4 CloudLit4;
    vec4 CloudShadow4;
    float StarAmount;
    float BandAmount;
    float AuroraAmount;
    float MeteorAmount;
    float HazeAmount;
    float FrostAmount;
    vec4 HazeColor4;
    vec4 GlowDir4;
    float GlowFocus;
    float Flash;
    vec4 FlashColor4;
    // Цветокоррекция мира — порядок обязан совпадать с концом списка в weather_gloss.json.
    float ZenithSaturation;
    float FogEnvironmentalStart;
    float FogEnvironmentalEnd;
    float FogRenderDistanceStart;
    float FogRenderDistanceEnd;
    vec4 ZenithGrade;
    vec4 FogColor;
};
// vec3 в std140 трактуется драйверами по-разному: тут он хранится как vec4,
// а имя остаётся прежним, чтобы не трогать тела шейдеров.
#define MistTint MistTint4.xyz
#define CameraPosition CameraPosition4.xyz
#define SkyZenith SkyZenith4.xyz
#define SkyHorizon SkyHorizon4.xyz
#define SkyNadir SkyNadir4.xyz
#define SunDir SunDir4.xyz
#define SunColor SunColor4.xyz
#define CloudLit CloudLit4.xyz
#define CloudShadow CloudShadow4.xyz
#define HazeColor HazeColor4.xyz
#define GlowDir GlowDir4.xyz
#define FlashColor FlashColor4.xyz


// Наложение глянца на кадр и настоящие отражения в лужах.
//
// Мягкий слой (потемнение, отблеск неба, снежный покров) приходит сюда уже посчитанным и
// размытым: color * (1 - a) + rgb. Этого хватает, чтобы поверхность читалась мокрой, но не
// хватает, чтобы читалась зеркальной — в луже обязано отражаться то, что над ней стоит.
//
// Отражение считается здесь, а не в первом проходе, ровно по одной причине: между ними лежит
// размытие. Размытая копия мира — это не отражение, а туман; отражению нужен резкий кадр, и
// единственное место, где он есть вместе с готовым мягким слоем, — этот проход.
//
// Способ — марш по буферу глубины. Луч отражается от восстановленной нормали, шагает вперёд в
// пространстве камеры, и на каждом шаге его глубина сравнивается с глубиной кадра: как только
// луч оказался за поверхностью, найдено место, которое в луже и отражается. Это честное
// отражение геометрии, а не подделка: в луже видно ровно тех, кто рядом с ней стоит.
//
// Чего этот способ не умеет — знать о том, чего нет в кадре. Луч, ушедший за край экрана или
// за спину камере, отражать нечего: там просто нет данных. Такие лучи гасятся к небесному
// оттенку, и это заметно меньше, чем растянутая по краю экрана полоса.

// Небо погоды: то же самое, что рисуется над головой. В луже отражается именно оно, а не
// подобранный на глаз оттенок, — вместе с тучами, их движением и вспышками молний.
#moj_import <zenith:weather_sky_color.glsl>

uniform sampler2D InSampler;
// Снимок глубины до сущностей: по нему снег отличает землю от живого.
uniform sampler2D TerrainDepthSampler;
uniform sampler2D GlossSampler;
uniform sampler2D DepthSampler;
// 1 — вода собрана в лужи, 0 — лежит сплошной плёнкой. Отражение считается в обоих случаях;
// разница в площади воды и в её шершавости: плёнка на шершавом блоке отражает мутно, стоячая
// лужа — почти зеркально.

// Глубина кадра без существ: всё, что оказалось ближе местности, погодой не красится вообще.
// Именно этим чамсы и защищены от снега — шейдер до них просто не доходит.
// 1 — снег: льду и покрову зеркало не положено.
// Сила дождя: рябь есть только пока он идёт.

// Объёмный туман у земли. Спрайты сюда не годятся: любой билборд — это диск с центром и
// краем, и из дисков дым не собирается ни при каком их числе. Настоящий туман — плотность,
// проинтегрированная вдоль луча зрения, и считается она здесь, по буферу глубины.

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

const int STEPS = 22;
const int REFINE = 4;

// Синусы углов камеры — один раз на пиксель в начале main: углы одни на весь кадр, а
// поворотов на пиксель до семи, и каждый раньше заново считал по четыре тригонометрии.
float camPitchCos;
float camPitchSin;
float camYawCos;
float camYawSin;

vec3 toWorld(vec3 v) {
    float pc = camPitchCos;
    float ps = camPitchSin;
    vec3 p = vec3(v.x, v.y * pc + v.z * ps, v.z * pc - v.y * ps);
    float yc = camYawCos;
    float ys = camYawSin;
    return vec3(p.x * yc + p.z * ys, p.y, p.z * yc - p.x * ys);
}

vec3 toView(vec3 v) {
    float yc = camYawCos;
    float ys = camYawSin;
    vec3 p = vec3(v.x * yc - v.z * ys, v.y, v.z * yc + v.x * ys);
    float pc = camPitchCos;
    float ps = camPitchSin;
    return vec3(p.x, p.y * pc - p.z * ps, p.z * pc + p.y * ps);
}

float linearDepth(float depth) {
    float ndc = depth * 2.0 - 1.0;
    return 2.0 * NearPlane * FarPlane / (FarPlane + NearPlane - ndc * (FarPlane - NearPlane));
}

// Цветокоррекция мира (zenith:post/world_grade), влитая в этот проход: когда глянец рисуется,
// отдельные проходы коррекции и её копии в кадр не нужны. Формула та же — небо не трогается,
// туман снимается ванильной формулой, коррекция ложится под него, и туман возвращается.
float zenithFogValue(float vertexDistance, float fogStart, float fogEnd) {
    if (vertexDistance <= fogStart) {
        return 0.0;
    } else if (vertexDistance >= fogEnd) {
        return 1.0;
    }
    return (vertexDistance - fogStart) / (fogEnd - fogStart);
}

vec3 zenithGrade(vec3 color) {
    float amount = max(ZenithSaturation, 0.0);
    float luma = dot(color, vec3(0.299, 0.587, 0.114));
    float boost = max(amount - 1.0, 0.0);
    vec3 saturated = mix(vec3(luma), color, amount);
    saturated = clamp(saturated * (1.0 + boost * 0.35), 0.0, 1.0);
    return clamp(saturated * ZenithGrade.rgb * ZenithGrade.a, 0.0, 1.0);
}

vec3 gradeScene(vec3 color, vec2 uv, float depth) {
    if (depth >= 0.999999 || (ZenithSaturation == 1.0 && ZenithGrade == vec4(1.0))) {
        return color;
    }
    vec2 ndc = uv * 2.0 - 1.0;
    vec3 view = vec3(ndc.x * Aspect * TanHalfFov, ndc.y * TanHalfFov, -1.0) * linearDepth(depth);
    // Повороту по рысканью длины безразличны — хватает того же разворота, что у неба.
    vec3 offset = toWorld(view);
    float spherical = length(offset);
    float cylindrical = max(length(offset.xz), abs(offset.y));
    float fog = max(zenithFogValue(spherical, FogEnvironmentalStart, FogEnvironmentalEnd),
            zenithFogValue(cylindrical, FogRenderDistanceStart, FogRenderDistanceEnd)) * FogColor.a;
    if (fog >= 0.999) {
        return color;
    }
    vec3 lit = clamp((color - FogColor.rgb * fog) / (1.0 - fog), 0.0, 1.0);
    return mix(zenithGrade(lit), FogColor.rgb, fog);
}

// Кадр в точке — уже с коррекцией: отражение обязано отражать тот же мир, что вокруг лужи.
vec3 sceneAt(vec2 uv) {
    return gradeScene(texture(InSampler, uv).rgb, uv, texture(DepthSampler, uv).r);
}

// Точка пространства камеры на экран. Проекция та же, из которой построен кадр, поэтому марш
// попадает ровно в те пиксели, куда смотрит луч.
vec2 project(vec3 view) {
    vec2 ndc = vec2(view.x / (-view.z * Aspect * TanHalfFov), view.y / (-view.z * TanHalfFov));
    return ndc * 0.5 + 0.5;
}

vec3 viewAt(vec2 uv, float depth) {
    vec2 ndc = uv * 2.0 - 1.0;
    return vec3(ndc.x * Aspect * TanHalfFov, ndc.y * TanHalfFov, -1.0) * linearDepth(depth);
}

float fresnel(float cosTheta) {
    float base = 1.0 - clamp(cosTheta, 0.0, 1.0);
    float sq = base * base;
    return 0.02 + 0.98 * sq * sq * base;
}

/**
 * Рябь от капель: круги, расходящиеся из случайных точек сетки. Наклон поверхности берётся
 * производной волны — по нему отражение и дрожит. Без ряби зеркальная лужа выглядит стеклом,
 * а не водой под дождём.
 */
vec2 ripple(vec2 world, float time) {
    // Одна сетка на три на три ячейки вокруг точки: круг живёт меньше, чем успевает уйти за
    // соседнюю ячейку, и дальше смотреть незачем. Вторая сетка мельче выглядела богаче, но
    // удваивала стоимость каждого пикселя лужи — а разницу видно только стоя носом в воду.
    vec2 slope = vec2(0.0);
    float scale = 1.9;
    vec2 cell = floor(world * scale);
    for (int ox = -1; ox <= 1; ox++) {
        for (int oz = -1; oz <= 1; oz++) {
            vec2 c = cell + vec2(float(ox), float(oz));
            float seed = hash21(c);
            // Каждая ячейка роняет каплю раз в цикл, со своей задержкой.
            float phase = fract(time * 0.9 + seed);
            vec2 center = (c + 0.35 + 0.3 * vec2(seed, fract(seed * 7.3))) / scale;
            vec2 delta = world - center;
            float dist = length(delta);
            float wave = exp(-dist * 7.0) * exp(-phase * 3.5) * sin((dist - phase * 0.55) * 46.0);
            slope += normalize(delta + 1.0e-5) * wave;
        }
    }
    // Размах наклона. Рябь — единственное, чем удар капли обозначен на воде: колец-наклеек
    // на земле больше нет, и вся работа лежит на ней. Поэтому размах ощутимо больше прежнего,
    // но по-прежнему в тех пределах, где это круги на воде, а не рябь на жести.
    return slope * 0.13;
}

// Форма клочьев — три произведения синусов от точки q = drift * 0.34 (drift — точка мглы со
// сносом ветром и растянутой высотой):
//   n = sin(a.x) sin(b.x) + 0.50 sin(a.y) sin(b.y) + 0.25 sin(a.z) sin(b.z),
//   wisp = clamp(n * 0.35 + 0.5, 0, 1).
// Аргументы синусов линейны по точке; здесь их линейная часть, постоянные — в MIST_A0/MIST_B0.
const vec3 MIST_A0 = vec3(0.0, 1.7, 2.3);
const vec3 MIST_B0 = vec3(0.0);
const vec3 MIST_WEIGHTS = vec3(1.0, 0.50, 0.25);

vec3 mistArgA(vec3 q) {
    return vec3(q.x * 0.30 + q.z * 0.17, q.x * 0.71 - q.z * 0.53, (q.x + q.z) * 1.31);
}

vec3 mistArgB(vec3 q) {
    return vec3(q.z * 0.23 - q.y * 0.41, q.y * 0.63 + q.x * 0.29, q.y * 1.11 - q.z * 0.87);
}

vec3 applyMist(vec3 color, vec3 rayEnd) {
    if (MistAmount <= 0.002) {
        return color;
    }
    vec3 delta = rayEnd - CameraPosition;
    float distance = length(delta);
    if (distance < 0.05) {
        return color;
    }
    float reach = min(distance, 110.0);
    vec3 direction = delta / distance;

    // Дробление шага случайным сдвигом: без него шестнадцать отсчётов ложатся слоями и дают
    // кольца по дальности — тот самый дефект, только в другом месте.
    float jitter = fract(sin(dot(texCoord, vec2(12.9898, 78.233))) * 43758.5453);
    float stepLength = reach / 16.0;
    float sum = 0.0;
    // Два досрочных выхода, оба без изменения результата.
    //
    // Высота слоя вдоль луча меняется монотонно. Выше h = 3.2 полоса меньше exp(-6.72) ~ 0.0012,
    // ниже h = -1.25 она ноль — в обоих случаях mistDensity отсекает её порогом 0.002, и раз луч
    // ушёл туда, дальше по нему он только удаляется: оставшиеся отсчёты дали бы нули. Запас до
    // порогов — десятые доли h, так что округление шага ничего не решает.
    //
    // Потолок мглы — 0.94, это 1 - exp(-2.81). Набрав оптическую толщу 3.0 (1 - exp(-3) = 0.95),
    // луч уже упёрся в потолок, и добавка плотности ничего не меняет: плотность не бывает
    // отрицательной, а clamp срежет итог ровно до того же 0.94.
    float sumCap = 3.0 / (stepLength * MistAmount * 0.085);

    // Отсчёты лежат на прямой с ровным шагом, а аргументы синусов формы линейны по точке —
    // значит от отсчёта к отсчёту каждый аргумент прирастает на одну и ту же величину. Синусы
    // поэтому не считаются заново на каждом шаге (шесть на отсчёт, девяносто шесть на пиксель
    // каждого кадра во всех режимах погоды), а поворачиваются: sin(x + d) и cos(x + d) из
    // sin(x), cos(x) и заранее посчитанных sin(d), cos(d). Значения те же, с точностью до
    // округления; трансцендентных операций на пиксель — двенадцать пар вместо девяноста шести.
    float invHeight = 1.0 / max(MistHeight, 0.25);
    vec3 windShift = vec3(MistWind.x * time * 0.55, 0.0, MistWind.y * time * 0.55);
    vec3 firstPos = CameraPosition + direction * (jitter * stepLength);
    vec3 q0 = (firstPos - windShift) * vec3(1.0, 1.7, 1.0) * 0.34;
    vec3 dq = direction * stepLength * vec3(1.0, 1.7, 1.0) * 0.34;
    vec3 angleA = mistArgA(q0) + MIST_A0;
    vec3 angleB = mistArgB(q0) + MIST_B0;
    vec3 sinA = sin(angleA);
    vec3 cosA = cos(angleA);
    vec3 sinB = sin(angleB);
    vec3 cosB = cos(angleB);
    vec3 stepA = mistArgA(dq);
    vec3 stepB = mistArgB(dq);
    vec3 stepSinA = sin(stepA);
    vec3 stepCosA = cos(stepA);
    vec3 stepSinB = sin(stepB);
    vec3 stepCosB = cos(stepB);

    for (int i = 0; i < 16; i++) {
        float sampleY = firstPos.y + direction.y * (float(i) * stepLength);
        float h = (sampleY - MistBase) * invHeight;
        if ((direction.y > 0.0 && h > 3.2) || (direction.y < 0.0 && h < -1.25)) {
            break;
        }
        // Взвесь лежит слоем: у земли густо, вверх сходит на нет по экспоненте, ниже опорной
        // высоты её тоже нет — там уже земля.
        float band = exp(-max(h, 0.0) * 2.1) * smoothstep(-1.2, 0.05, h);
        if (band >= 0.002) {
            float wisp = clamp(dot(sinA * sinB, MIST_WEIGHTS) * 0.35 + 0.5, 0.0, 1.0);
            // Клочья, а не ровная взвесь: без этого туман читается однотонной пеленой.
            wisp = smoothstep(0.34, 0.92, wisp) * 0.85 + 0.15;
            sum += band * wisp;
        }
        if (sum >= sumCap) {
            break;
        }
        // Следующий отсчёт: поворот на шаг аргумента.
        vec3 nextSinA = sinA * stepCosA + cosA * stepSinA;
        cosA = cosA * stepCosA - sinA * stepSinA;
        sinA = nextSinA;
        vec3 nextSinB = sinB * stepCosB + cosB * stepSinB;
        cosB = cosB * stepCosB - sinB * stepSinB;
        sinB = nextSinB;
    }

    float optical = sum * stepLength * MistAmount * 0.085;
    return mix(color, MistTint, clamp(1.0 - exp(-optical), 0.0, 0.94));
}

void main() {
    camPitchCos = cos(SkyPitch);
    camPitchSin = sin(SkyPitch);
    camYawCos = cos(SkyYaw);
    camYawSin = sin(SkyYaw);

    vec4 source = texture(InSampler, texCoord);
    float depth = texture(DepthSampler, texCoord).r;
    // Коррекция мира — по кадру до снега (небо она не трогает); синусы камеры выше уже готовы.
    source.rgb = gradeScene(source.rgb, texCoord, depth);
    if (depth >= 0.999999) {
        // Луч неба нужен только здесь: раньше он считался для каждого пикселя кадра.
        vec2 ndcSky = texCoord * 2.0 - 1.0;
        vec3 skyRay = normalize(toWorld(vec3(ndcSky.x * Aspect * TanHalfFov, ndcSky.y * TanHalfFov, -1.0)));
        fragColor = vec4(clamp(applyMist(source.rgb, CameraPosition + skyRay * 110.0), 0.0, 1.0),
                source.a);
        return;
    }

    vec4 gloss = texture(GlossSampler, texCoord);
    vec3 color = source.rgb * (1.0 - gloss.a) + gloss.rgb;

    // Зеркало — только вода и только вблизи: дальше работает туман, а глубина там уже дрожит
    // квантованием, и марш по ней ловит призраков.
    float viewZ = linearDepth(depth);
    // Точка в камере и та же точка в мире — один раз: ниже они же нужны как posV и worldPos,
    // и раньше считались второй раз теми же выражениями.
    vec3 posV = viewAt(texCoord, depth);
    vec3 mistHit = CameraPosition + toWorld(posV);
    // Снег сюда доходит: искры на нём считаются здесь, а не в маске, потому что между маской
    // и этим проходом лежит размытие — оно бы их размазало в ровную серость.
    if (WetAmount <= 0.01 || viewZ > FadeEnd || (Frost <= 0.5 && ReflectAmount <= 0.01)) {
        fragColor = vec4(clamp(applyMist(color, mistHit), 0.0, 1.0), source.a);
        return;
    }

    vec3 viewDir = normalize(posV);

    // Нормаль по соседям, а не производными: у производных одно значение на квадрат два на
    // два пикселя, и по краям граней зеркало ступенчато — тот же пунктир, что и в маске.
    float depthLeft = texture(DepthSampler, texCoord - vec2(oneTexel.x, 0.0)).r;
    float depthRight = texture(DepthSampler, texCoord + vec2(oneTexel.x, 0.0)).r;
    float depthDown = texture(DepthSampler, texCoord - vec2(0.0, oneTexel.y)).r;
    float depthUp = texture(DepthSampler, texCoord + vec2(0.0, oneTexel.y)).r;

    float zLeft = linearDepth(depthLeft);
    float zRight = linearDepth(depthRight);
    float zDown = linearDepth(depthDown);
    float zUp = linearDepth(depthUp);

    vec3 alongX = abs(zRight - viewZ) < abs(viewZ - zLeft)
            ? viewAt(texCoord + vec2(oneTexel.x, 0.0), depthRight) - posV
            : posV - viewAt(texCoord - vec2(oneTexel.x, 0.0), depthLeft);
    vec3 alongY = abs(zUp - viewZ) < abs(viewZ - zDown)
            ? viewAt(texCoord + vec2(0.0, oneTexel.y), depthUp) - posV
            : posV - viewAt(texCoord - vec2(0.0, oneTexel.y), depthDown);

    vec3 normalV = normalize(cross(alongX, alongY));
    normalV *= -sign(dot(normalV, viewDir));

    vec3 worldNormal = toWorld(normalV);
    vec3 worldPos = mistHit;

    // Лужи стоят только на ровных верхних гранях: со стены и с наклона вода стекает.
    // Порог по наклону расходится с дистанцией: нормаль восстановлена из квантованной
    // глубины и вдали дрожит, а пороговое решение на дрожащей величине щёлкает от пикселя
    // к пикселю и сыплет кромки пунктиром.
    if (TerrainValid > 0.5) {
        float terrainRaw = texture(TerrainDepthSampler, texCoord).r;
        if (terrainRaw < 0.999999) {
            float terrainViewZ = linearDepth(terrainRaw);
            if (terrainViewZ - viewZ > 0.04 + viewZ * 0.012) {
                fragColor = vec4(clamp(applyMist(color, mistHit), 0.0, 1.0), source.a);
                return;
            }
        }
    }

    float normalBlur = smoothstep(18.0, 55.0, viewZ);
    float upness = smoothstep(mix(0.86, 0.30, normalBlur), mix(0.99, 1.30, normalBlur),
            worldNormal.y);
    if (upness <= 0.002) {
        fragColor = vec4(clamp(applyMist(color, mistHit), 0.0, 1.0), source.a);
        return;
    }

    // Кромка — по второй разности глубины: у наклонной плоскости она нулевая при любом
    // наклоне, и дальняя земля под скользящим взглядом больше не считается обрывом.
    float curvature = abs(zLeft + zRight - 2.0 * viewZ) + abs(zDown + zUp - 2.0 * viewZ);
    float edgeMask = 1.0 - smoothstep(viewZ * 0.006, viewZ * 0.030, curvature);

    if (Frost > 0.5) {
        // ------------------------------------------------------------------------------
        // Свет на снегу. Зеркала ему не положено — снег рассеивает во все стороны, — но
        // рассеивает он не ровно, и три неровности и делают его снегом, а не белой краской.
        // ------------------------------------------------------------------------------
        float coverFade = CoverRadius > 0.5
                ? smoothstep(CoverRadius * 0.70, CoverRadius, viewZ)
                : 1.0;
        float lay = smoothstep(mix(0.22, -0.35, normalBlur), mix(0.72, 1.25, normalBlur),
                worldNormal.y) * edgeMask * coverFade;
        // Та же привязка к сетке блоков, что в проходе побелки, и с тем же допуском по
        // фактическому дрожанию высоты: дальше сорока блоков она снимается совсем.
        float yFract = fract(worldPos.y);
        float gridDist = min(min(yFract, 1.0 - yFract), abs(yFract - 0.5));
        float jitter = clamp(fwidth(worldPos.y), 0.0, 0.5);
        float onBlock = 1.0 - smoothstep(0.02 + jitter * 2.0, 0.07 + jitter * 3.0, gridDist);
        lay *= mix(onBlock, 1.0, smoothstep(22.0, 48.0, viewZ));
        if (lay <= 0.002) {
            fragColor = vec4(clamp(applyMist(color, mistHit), 0.0, 1.0), source.a);
            return;
        }

        vec3 sunDir = normalize(SunDir);
        // Взгляд в системе мира: светило и нормаль покрова живут там же, и считать их с
        // камерным вектором значило бы вращать свет вместе с головой.
        vec3 viewW = toWorld(viewDir);
        float snowNear = 1.0 - smoothstep(FadeEnd * 0.6, FadeEnd, viewZ);

        // 1. Обратное рассеяние. Снег заметно ярче, когда светило за спиной: свет уходит в
        //    толщу, отражается от кристаллов внутри и возвращается назад почти по тому же
        //    пути. Отсюда и слепящая белизна поля, когда солнце сзади, и серая муть, когда
        //    оно в лицо, — эффект сильный, и без него снег выглядит равномерно закрашенным.
        float retro = pow(max(dot(-viewW, sunDir), 0.0), 2.0);
        color += SunColor * retro * lay * WetAmount * snowNear * 0.13;

        // 2. Отсвет по скользящему углу. У снега нет зеркала, но есть широкий отблеск: к
        //    горизонту поверхность светлеет, потому что видно её верхний, самый рыхлый слой
        //    под малым углом. Это то, чем дальнее поле отличается от того, что под ногами.
        float grazing = pow(1.0 - abs(dot(viewW, worldNormal)), 4.0);
        vec3 skyAbove = weatherSkyReflect(vec3(0.0, 1.0, 0.0));
        color += skyAbove * grazing * lay * WetAmount * snowNear * 0.16;

        // 3. Искры. Кристаллы сидят в мировой сетке по паре сантиметров: сетка мировая,
        //    поэтому искра остаётся на своём кристалле, когда мимо проходят, а не бежит по
        //    экрану вместе с головой. Ячейка одна на все три оси — на наклонной грани сетка
        //    не должна плыть.
        vec3 cell = floor(worldPos * 34.0);
        float grain = hash21(cell.xz + cell.y * 31.7);

        // Вспыхивает не всё подряд и не одновременно: у каждого кристалла своя фаза, и в
        // каждый момент светится примерно один из полусотни.
        float twinkle = 0.5 + 0.5 * sin(time * 1.6 + grain * 6.2831);
        float spark = smoothstep(0.93, 1.0, grain) * smoothstep(0.62, 1.0, twinkle);

        // Кристалл — зеркальце, и ярче всего он там, где отражает светило прямо в глаз.
        // Без этого искры сыпались ровным ковром по всему полю независимо от того, откуда
        // свет, и читались шумом на текстуре, а не блеском на снегу.
        float mirror = max(dot(reflect(viewW, worldNormal), sunDir), 0.0);
        spark *= 0.35 + 0.65 * pow(mirror, 6.0);

        // Вдали искра гаснет, и это не экономия. Кристалл мельче пикселя: дальше двух-трёх
        // десятков блоков сетка перестаёт попадать в пиксели и рассыпается в шум, который
        // ползёт при каждом повороте головы.
        spark *= 1.0 - smoothstep(14.0, 34.0, viewZ);

        if (spark > 0.001) {
            // Тот же купол в зените, что skyAbove выше: аргумент один, и второй полный расчёт
            // неба на пиксель искры был повтором.
            vec3 skyLight = skyAbove;
            color += (skyLight * 0.55 + SunColor * 0.45) * spark * lay * WetAmount * 0.40;
        }
        fragColor = vec4(clamp(applyMist(color, mistHit), 0.0, 1.0), source.a);
        return;
    }

    // Та же формула, что в проходе маски: зеркало обязано лежать ровно в тех лужах, которые
    // там посчитаны, иначе отражение окажется рядом с мокрым пятном, а не в нём.
    float patchNoise = valueNoise2(worldPos.xz * 0.34) * 0.60 + valueNoise2(worldPos.xz * 0.87) * 0.40;
    float coverage = mix(1.0, smoothstep(0.55, 0.72, patchNoise), PuddleAmount);
    float puddle = coverage * upness * edgeMask;
    puddle *= 1.0 - smoothstep(FadeEnd * 0.5, FadeEnd, viewZ);
    puddle *= WetAmount;
    if (puddle <= 0.01) {
        fragColor = vec4(clamp(applyMist(color, mistHit), 0.0, 1.0), source.a);
        return;
    }

    // Рябь наклоняет поверхность лужи. Наклон считается в мире и возвращается в пространство
    // камеры: круги от капель обязаны стоять на воде, а не ездить по экрану вместе с головой.
    // Шершавость воды. Лужа стоит и отражает почти зеркально; плёнка в доли миллиметра
    // повторяет рельеф блока под собой, и отражение в ней размывается. Без этого сплошная
    // вода превращала весь мир в каток: земля отражала соседние блоки резче, чем зеркало.
    float roughness = mix(0.24, 0.08, PuddleAmount);

    // Рябь считает девять ячеек с экспонентами — без дождя её считать незачем.
    vec2 slope = RainAmount > 0.001
            ? ripple(worldPos.xz, time) * RainAmount * (1.0 + roughness)
            : vec2(0.0);
    vec3 rippled = normalize(toView(normalize(worldNormal + vec3(slope.x, 0.0, slope.y))));

    // Блик по гребню волны. Одного наклона нормали мало: он меняет то, что отражается, но на
    // тёмной воде под тучей отражается тёмное, и круги от капель пропадают. Гребень же ловит
    // свет всегда — по нему удар и виден, даже когда отражать нечего.
    float wave = min(length(slope) * 6.0, 1.0);

    vec3 rayDir = reflect(viewDir, rippled);

    // Френель в чистом виде меняется от почти нуля под ногами до единицы у горизонта. На
    // сплошной воде это значит, что стоит подпрыгнуть — и вся земля разом вспыхивает и гаснет:
    // высота глаза поменялась, угол вместе с ней. Физика верная, но полметра прыжка не должны
    // перекрашивать мир.
    //
    // Поэтому кривая поджимается: у плёнки от угла зависит меньше половины отражения,
    // остальное — постоянная доля. У лужи разброс оставлен почти полным, там перепад блеска
    // по её поверхности и есть то, из-за чего лужа выглядит лужей.
    float spread = mix(0.72, 0.90, PuddleAmount);
    float level = mix(0.40, 0.88, PuddleAmount);
    float fres = ((1.0 - spread) + spread * fresnel(dot(-viewDir, rippled))) * level;

    // Заведомо невидимое отражение не трассируется вовсе: двадцать два шага по буферу
    // глубины плюс уточнение — самая дорогая часть кадра, и запускать её ради вклада в сотую
    // долю тона незачем. Порог берётся до марша, по той же формуле, что и итоговая сила.
    float budget = fres * ReflectAmount * puddle * (1.0 - roughness * 0.55) * (1.0 - 0.40 * Flash);
    if (budget <= 0.012) {
        fragColor = vec4(clamp(applyMist(color, mistHit), 0.0, 1.0), source.a);
        return;
    }

    // Марш. Шаг растёт с расстоянием: вблизи нужна точность, вдали — дальнобойность, а
    // постоянный шаг даёт либо первое, либо второе.
    vec3 reflection = vec3(0.0);
    float hit = 0.0;
    vec3 position = posV + rippled * 0.05;
    float step = max(0.12, viewZ * 0.035);

    for (int i = 0; i < STEPS; i++) {
        position += rayDir * step;
        step *= 1.28;
        if (position.z > -NearPlane) {
            break;
        }
        vec2 uv = project(position);
        if (uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0) {
            break;
        }
        float sampled = texture(DepthSampler, uv).r;
        // Луч ушёл в небо: там нет поверхности, и «попадание» в неё дало бы яркое небо в
        // отражении ровно по контуру силуэта — светлая обводка вдоль каждого края.
        if (sampled >= 0.999999) {
            break;
        }
        float sceneDepth = linearDepth(sampled);
        float rayDepth = -position.z;
        float diff = rayDepth - sceneDepth;
        // Толщина: без неё луч «протыкает» тонкую геометрию и цепляется за небо за ней.
        if (diff > 0.0 && diff < step * 2.5 + 0.6) {
            // Уточнение половинным делением: без него отражение ступенчатое, шагами марша.
            vec3 back = position;
            vec3 forward = position - rayDir * step / 1.28;
            for (int r = 0; r < REFINE; r++) {
                vec3 mid = (back + forward) * 0.5;
                vec2 midUv = project(mid);
                float midScene = linearDepth(texture(DepthSampler, midUv).r);
                if (-mid.z > midScene) {
                    back = mid;
                } else {
                    forward = mid;
                }
            }
            uv = project(back);
            // Размытие отражения по шершавости: четыре отсчёта крестом вокруг попадания.
            // Полноценного конуса тут не нужно — вода не металл, её отражение и так слабое,
            // а четыре отсчёта уже убирают из него резкость, которой у мокрого блока не бывает.
            float spread = roughness * 0.010;
            reflection = sceneAt(uv) * 0.40
                    + sceneAt(uv + vec2(spread, 0.0)) * 0.15
                    + sceneAt(uv - vec2(spread, 0.0)) * 0.15
                    + sceneAt(uv + vec2(0.0, spread)) * 0.15
                    + sceneAt(uv - vec2(0.0, spread)) * 0.15;
            // Край экрана: отражению неоткуда взяться за его пределами, и обрыв там виден
            // сильнее, чем недостача отражения.
            vec2 border = smoothstep(vec2(0.0), vec2(0.12), uv)
                    * (1.0 - smoothstep(vec2(0.88), vec2(1.0), uv));
            hit = border.x * border.y;
            break;
        }
    }

    // Промах — не повод остаться без отражения. Луч ушёл в небо, и небо в луже и отражается:
    // тот же купол, те же тучи, та же вспышка. Ровный оттенок вместо него читался краской.
    // Небо — по конусу шершавости, между отражённым лучом и нормалью: та же причина, что и в
    // проходе маски. Небо низкочастотное, усреднение по конусу почти не меняет его цвет, зато
    // убирает ползание облаков по земле при ходьбе. Найденная же лучом геометрия остаётся как
    // есть — она и должна двигаться, иначе это не отражение.
    float specularity = mix(0.30, 0.85, PuddleAmount);
    vec3 skyDir = normalize(mix(toWorld(rippled), toWorld(rayDir), specularity));
    vec3 skyColor = weatherSkyReflect(skyDir);
    reflection = mix(skyColor, reflection, hit);

    // Дорожка светила по воде — по тому же полустатичному направлению: иначе она одна
    // продолжала бы скользить по земле, и её было бы видно как раз сильнее всего.
    float sunAlign = max(dot(skyDir, normalize(SunDir)), 0.0);
    reflection += SunColor * pow(sunAlign, 40.0) * GlintAmount * (1.0 - hit * 0.7);

    // Шершавая вода отражает слабее гладкой: часть лучей уходит в микрорельеф и не
    // возвращается. Отсюда и разница между лужей и мокрым блоком — не в том, что у блока нет
    // отражения, а в том, что оно тусклее и мутнее.
    // Та же поправка на разряд, что и в маске: вода не должна перебивать вспышку.
    float mirror = clamp(fres * ReflectAmount * puddle * (0.75 + 0.35 * hit)
            * (1.0 - roughness * 0.55) * (1.0 - 0.40 * Flash), 0.0, 0.94);
    mirror *= mix(0.5, 1.0, hit);
    color = mix(color, reflection, mirror);

    // Сам гребень: еле заметная добавка неба по кругу от каждой капли.
    color += skyColor * wave * puddle * RainAmount * 0.10 * (1.0 - 0.40 * Flash);

    fragColor = vec4(clamp(applyMist(color, mistHit), 0.0, 1.0), source.a);
}
