// weather_sky_color.glsl
// Небо погоды как функция направления — одна на всех, кто его рисует.
//
// Небо нужно двум местам сразу, и это не дублирование ради удобства. Купол над головой рисует
// weather_sky, а в лужах на земле отражается он же: отражение, подменённое ровным оттенком,
// сразу выдаёт подделку — в настоящей воде видно те самые тучи, что висят над ней, и они
// движутся вместе с ними. Держать для этого второй, приближённый вариант неба значило бы
// получить два разных неба в одном кадре.
//
// Все ветвления идут по uniform-ам, то есть одинаковы для всего кадра: варп не расходится, и
// выключенный слой стоит ровно одно сравнение, а не выполненный впустую цикл.

uniform float time;
uniform vec3 CameraPosition;
uniform float SkyPitch;
uniform float SkyYaw;
uniform float TanHalfFov;
uniform float Aspect;

uniform vec3 SkyZenith;
uniform vec3 SkyHorizon;
uniform vec3 SkyNadir;

uniform vec3 SunDir;
uniform vec3 SunColor;
uniform float SunSize;
uniform float SunGlare;

uniform float CloudCover;
uniform float CloudDark;
uniform float CloudSpeed;
uniform float CloudScale;
uniform vec3 CloudLit;
uniform vec3 CloudShadow;

uniform float StarAmount;
uniform float BandAmount;
uniform float AuroraAmount;
uniform float MeteorAmount;

uniform float HazeAmount;
uniform vec3 HazeColor;

// Куда светит на дымку. В сумерках это не то же, что SunDir: диск в небе к этому времени
// лунный и стоит с другой стороны, а зарево держится над той, где село солнце. GlowFocus —
// сколько его осталось: единица днём и в сумерках, ноль глухой ночью, когда горизонт светится
// ровно по кругу и никакой стороны у него нет.
uniform vec3 GlowDir;
uniform float GlowFocus;

// Сколько ледяных игл в воздухе, 0..1. Ими живёт вся арктическая оптика: гало, ложные
// солнца, столб света и белая мгла у горизонта.
uniform float FrostAmount;

uniform float Flash;
uniform vec3 FlashColor;

const float TAU = 6.28318530718;
const float HALF_PI_INV = 0.63661977;
const vec3 BAND_NORMAL = vec3(0.7796, 0.2999, -0.5497);
const vec3 STAR_COLD = vec3(0.72, 0.82, 1.00);
const vec3 STAR_WARM = vec3(1.00, 0.86, 0.66);
const vec3 MW_COLD = vec3(0.60, 0.72, 1.00);
const vec3 MW_WARM = vec3(1.00, 0.84, 0.62);
const vec3 AURORA_BASE = vec3(0.10, 1.00, 0.42);
const vec3 AURORA_MID = vec3(0.16, 0.82, 0.96);
const vec3 AURORA_TIP = vec3(0.74, 0.33, 1.00);
const vec3 METEOR_HOT = vec3(1.00, 0.97, 0.90);
const vec3 METEOR_COOL = vec3(0.34, 0.78, 1.00);
const vec3 METEOR_EMBER = vec3(1.00, 0.72, 0.42);

// --- Направление взгляда ---

vec3 getSkyDirection(vec3 viewDirection) {
    float pitchCos = cos(SkyPitch);
    float pitchSin = sin(SkyPitch);
    vec3 pitchedDirection = normalize(vec3(
            viewDirection.x,
            viewDirection.y * pitchCos + viewDirection.z * pitchSin,
            viewDirection.z * pitchCos - viewDirection.y * pitchSin
    ));
    float yawCos = cos(SkyYaw);
    float yawSin = sin(SkyYaw);
    return normalize(vec3(
            pitchedDirection.x * yawCos + pitchedDirection.z * yawSin,
            pitchedDirection.y,
            pitchedDirection.z * yawCos - pitchedDirection.x * yawSin
    ));
}

// Ниже линии горизонта небо подменяется целиком: пост-эффект стоит в кадре сразу за проходом
// неба, поэтому там ещё нет ни земли, ни сущностей — закрашивать нечего, кроме тумана.
float fullSkyAmount(float skyHeight, float amount) {
    float lowerSky = 1.0 - smoothstep(-0.18, 0.18, skyHeight);
    float enabled = smoothstep(0.02, 0.18, amount);
    return clamp(mix(amount, max(amount, 0.96), lowerSky * enabled), 0.0, 1.0);
}

// --- Шум ---

// Хеши без синуса. Синус в хеше — самая дорогая строчка во всём небе: один шум трёх
// измерений зовёт хеш восемь раз, объёмные облака зовут шум по три раза на срез, срезов
// семь — под две сотни синусов на пиксель, и всё это на полный экран. Перемешивание дробными
// частями даёт ту же неразличимую глазом случайность за считаные умножения.
float hash11(float p) {
    p = fract(p * 0.1031);
    p *= p + 33.33;
    p *= p + p;
    return fract(p);
}

float hash21(vec2 p) {
    vec3 q = fract(vec3(p.xyx) * 0.1031);
    q += dot(q, q.yzx + 33.33);
    return fract((q.x + q.y) * q.z);
}

float hash13(vec3 p) {
    vec3 q = fract(p * 0.1031);
    q += dot(q, q.zyx + 31.32);
    return fract((q.x + q.y) * q.z);
}

float valueNoise2(vec2 p) {
    vec2 i = floor(p);
    vec2 f = p - i;
    vec2 u = f * f * (3.0 - 2.0 * f);
    float a = hash21(i);
    float b = hash21(i + vec2(1.0, 0.0));
    float c = hash21(i + vec2(0.0, 1.0));
    float d = hash21(i + vec2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float valueNoise3(vec3 p) {
    vec3 ip = floor(p);
    vec3 fp = p - ip;
    vec3 sp = fp * fp * (3.0 - 2.0 * fp);
    float n000 = hash13(ip);
    float n100 = hash13(ip + vec3(1.0, 0.0, 0.0));
    float n010 = hash13(ip + vec3(0.0, 1.0, 0.0));
    float n110 = hash13(ip + vec3(1.0, 1.0, 0.0));
    float n001 = hash13(ip + vec3(0.0, 0.0, 1.0));
    float n101 = hash13(ip + vec3(1.0, 0.0, 1.0));
    float n011 = hash13(ip + vec3(0.0, 1.0, 1.0));
    float n111 = hash13(ip + vec3(1.0, 1.0, 1.0));
    return mix(
            mix(mix(n000, n100, sp.x), mix(n010, n110, sp.x), sp.y),
            mix(mix(n001, n101, sp.x), mix(n011, n111, sp.x), sp.y),
            sp.z
    );
}

float fbm3(vec3 p) {
    float total = 0.500 * valueNoise3(p);
    p *= 2.03;
    total += 0.250 * valueNoise3(p);
    p *= 2.11;
    total += 0.125 * valueNoise3(p);
    return total / 0.875;
}

float fbm2(vec3 p) {
    float total = 0.620 * valueNoise3(p);
    p *= 2.07;
    total += 0.310 * valueNoise3(p);
    return total / 0.930;
}

vec2 nextOctave(vec2 p) {
    mat2 turn = mat2(0.80, 0.60, -0.60, 0.80);
    return turn * p * 2.07 + vec2(31.7, 17.3);
}

float cloudFbmCoarse(vec2 p) {
    vec2 q = p;
    float sum = 0.0;
    float amp = 0.5;
    for (int i = 0; i < 2; i++) {
        sum += amp * valueNoise2(q);
        q = nextOctave(q);
        amp *= 0.5;
    }
    return sum / 0.75;
}

// --- Слои неба ---

vec3 baseGradient(vec3 dir) {
    float up = clamp(dir.y, 0.0, 1.0);
    vec3 sky = mix(SkyHorizon, SkyZenith, pow(up, 0.58));
    sky = mix(sky, SkyNadir, smoothstep(0.0, 0.62, -dir.y));
    return sky;
}

// Дымка у горизонта: то, чем дождливый день отличается от ясного сильнее, чем цветом зенита.
// Держится узкой полосой вокруг линии горизонта и не поднимается в зенит.
vec3 horizonHaze(vec3 dir) {
    if (HazeAmount <= 0.001) {
        return vec3(0.0);
    }
    float band = exp(-abs(dir.y) * (3.4 - 1.6 * HazeAmount));
    float drift = 0.86 + 0.14 * valueNoise2(vec2(atan(dir.z, dir.x) * 1.8, dir.y * 3.0) + vec2(time * 0.02, 0.0));
    float glowAlign = max(dot(dir, normalize(GlowDir)), 0.0);
    float sun = 0.75 + 0.55 * pow(glowAlign, 4.0) * GlowFocus;
    return HazeColor * (HazeAmount * band * drift * sun);
}

vec3 sunDisc(vec3 dir, float sunAlign) {
    if (SunGlare <= 0.001 && SunSize <= 0.0001) {
        return vec3(0.0);
    }
    // Диск считается по угловому расстоянию, а не по степени косинуса: степень растягивает
    // солнце в овал у краёв экрана, а угол одинаков в любой точке кадра.
    float angle = acos(clamp(sunAlign, -1.0, 1.0));
    float edge = max(fwidth(angle) * 1.5, 0.0007);
    float disc = 1.0 - smoothstep(SunSize - edge, SunSize + edge, angle);
    float glare = pow(sunAlign, 7.0) * 0.16 + pow(sunAlign, 140.0) * 0.5;
    float horizonFade = 0.30 + 0.70 * smoothstep(-0.20, 0.10, dir.y);
    return SunColor * ((disc * 2.4 + glare * SunGlare) * horizonFade);
}

vec3 milkyWay(vec3 dir, out float density) {
    float structure = fbm3(dir * 3.4 + vec3(4.7, 1.3, 8.9));
    vec3 lanePos = dir * 4.6 + BAND_NORMAL * (dot(dir, BAND_NORMAL) * 9.0) + vec3(17.3, 5.1, 31.7);
    float dust = fbm2(lanePos);

    float offset = dot(dir, BAND_NORMAL) + (structure - 0.5) * 0.26;
    float coreQ = offset / 0.155;
    float haloQ = offset / 0.430;
    float core = exp(-coreQ * coreQ);
    float halo = exp(-haloQ * haloQ);

    float clumps = 0.30 + 1.40 * structure * structure;
    float laneMask = clamp(core * 0.80 + halo * 0.45, 0.0, 1.0);
    float lanes = 1.0 - 0.88 * smoothstep(0.40, 0.78, dust) * laneMask;

    density = (core * clumps * 0.395 + halo * 0.058) * lanes;

    vec3 tone = mix(MW_COLD, MW_WARM, smoothstep(0.36, 0.84, dust) * 0.80);
    tone = mix(tone, vec3(0.90, 0.93, 1.00), core * 0.20);
    return tone * density;
}

vec3 starLayer(vec3 dir, float scale, float density, float radius, float rate, float seed, float shimmer) {
    vec3 p = dir * scale;
    vec3 cell = floor(p);
    vec3 local = fract(p) - 0.5;

    float h = hash13(cell + vec3(seed, seed * 2.13, seed * 5.71));
    // Пустая ячейка — это девятнадцать из двадцати: итог там всё равно умножается на ноль
    // присутствия, поэтому выходим сразу, не считая ни формы, ни мерцания. Условие — ровно
    // то, при котором step ниже дал бы ноль.
    if (h < 1.0 - density) {
        return vec3(0.0);
    }
    float present = step(1.0 - density, h);

    float ha = fract(h * 137.13);
    float hb = fract(h * 733.71);
    float hc = fract(h * 2311.17);
    float hd = fract(h * 5417.53);

    float mag = ha * ha * hb;
    float r = min(radius * (0.50 + 0.70 * mag), 0.46);
    vec3 jitter = (vec3(ha, hb, hc) - 0.5) * max(1.0 - 2.0 * r, 0.0);
    float d = length(local - jitter);

    float core = 1.0 - smoothstep(r * 0.20, r, d);
    float halo = exp(-d * d * 38.0) * (0.16 + 0.44 * mag);

    float twinkle = 1.0 - shimmer * 0.30 * (0.5 + 0.5 * sin(time * rate + hd * 61.7));
    vec3 tone = mix(STAR_COLD, STAR_WARM, smoothstep(0.30, 0.88, hd));

    return tone * (0.46 + 1.35 * mag + 3.40 * mag * mag) * present * twinkle * (core + halo);
}

// ring — (cos az, sin az): одна пара на все три занавеса, а не по две пары на каждый.
vec3 auroraCurtain(float az, vec2 ring, float ev, float pixAz, float seed, float baseHeight, float spread, float gain) {
    float wobble = 0.078 * sin(az + seed * 3.7 + time * 0.055);
    wobble += 0.043 * sin(az * 2.0 - time * 0.041 + seed * 6.1);
    wobble += 0.019 * sin(az * 3.0 + time * 0.033 + seed * 2.9);
    wobble += 0.052 * (valueNoise2(ring * 1.7 + vec2(seed * 19.0, time * 0.047)) - 0.5);

    float d = ev - (baseHeight + wobble);
    // Ниже подола занавеса rise ровно ноль, и вместе с ним ноль весь итог: лучи, сгустки и
    // огибающая там считались только чтобы умножиться на него.
    if (d <= -0.014) {
        return vec3(0.0);
    }

    float crown = smoothstep(0.56, 1.0, ev);

    float rise = smoothstep(-0.014, 0.048, d);
    float fall = exp(-max(d, 0.0) / (spread * (1.0 + 2.2 * crown)));
    float body = rise * fall;

    float lobe = 0.5 + 0.5 * sin(az + seed * 4.2 + time * 0.021);
    lobe *= 0.66 + 0.34 * (0.5 + 0.5 * sin(az * 2.0 - seed * 2.1 - time * 0.017));
    float envelope = max(smoothstep(0.16, 0.70, lobe), 0.30 * crown);

    float rayNoise = valueNoise2(ring * 6.4 + vec2(seed * 7.3, time * 0.085));
    float rays = 0.42 + 0.58 * (0.5 + 0.5 * sin(az * 46.0 + rayNoise * 7.0 + ev * 1.6 + time * 0.05));
    rays = mix(0.71, rays, 1.0 - smoothstep(0.55, 1.90, pixAz * 46.0));

    float clump = 0.58 + 0.42 * (0.5 + 0.5 * sin(az * 5.0 - time * 0.065 + seed * 1.9));
    float top = max(1.0 - smoothstep(0.58, 0.96, ev), 0.42 * crown);
    float low = smoothstep(-0.01, 0.09, ev);

    float t = clamp(max(d, 0.0) / (spread * 2.4), 0.0, 1.0);
    vec3 tone = mix(AURORA_BASE, AURORA_MID, smoothstep(0.05, 0.52, t));
    tone = mix(tone, AURORA_TIP, smoothstep(0.46, 1.0, t));

    return tone * (body * envelope * rays * clump * top * low * gain);
}

vec3 aurora(vec3 dir) {
    float ev = asin(clamp(dir.y, -1.0, 1.0)) * HALF_PI_INV;
    float azX = dir.x + step(abs(dir.x) + abs(dir.z), 1.0e-6);
    float az = atan(dir.z, azX);
    float cosEl = sqrt(max(1.0 - dir.y * dir.y, 0.0001));
    float pixAz = length(fwidth(dir)) / max(cosEl, 0.06);
    // Под горизонтом множитель low у каждого занавеса — ровно ноль. Выход стоит после fwidth:
    // производные обязаны считаться всеми пикселями квадрата, иначе соседу они врут.
    if (ev <= -0.01) {
        return vec3(0.0);
    }
    float breathe = 0.87 + 0.13 * sin(time * 0.09) + 0.05 * sin(time * 0.23 + 1.7);
    vec2 ring = vec2(cos(az), sin(az));

    vec3 sum = auroraCurtain(az, ring, ev, pixAz, 0.0, 0.09, 0.17, 1.00);
    sum += auroraCurtain(az, ring, ev, pixAz, 2.7, 0.19, 0.24, 0.70);
    sum += auroraCurtain(az, ring, ev, pixAz, 5.3, 0.31, 0.31, 0.46);
    return sum * (1.45 * breathe);
}

vec3 meteorStreak(vec3 dir, float seed, float rate, float phase) {
    float stream = time * rate + phase;
    float id = floor(stream);
    float life = fract(stream);

    float r1 = hash11(id * 1.371 + seed);
    float r5 = fract(r1 * 6113.79 + seed * 3.1);

    float span = 0.46 + 0.30 * r5;
    float u = clamp(life / span, 0.0, 1.0);
    float env = smoothstep(0.0, 0.07, u) * (1.0 - smoothstep(0.62, 1.0, u));
    // Метеор между пролётами: огибающая — ноль, и весь итог умножается на неё в power.
    // Огибающая зависит только от времени, так что ветка одна на весь кадр, без расхождения.
    if (env <= 0.0) {
        return vec3(0.0);
    }

    float r2 = fract(r1 * 197.31 + seed);
    float r3 = fract(r1 * 731.17 + seed * 1.7);
    float r4 = fract(r1 * 2311.53 + seed * 2.3);

    float azimuth = r2 * TAU;
    float elevation = 0.10 + 0.82 * r3;
    float ring = sqrt(max(0.0, 1.0 - elevation * elevation));
    vec3 origin = vec3(ring * cos(azimuth), elevation, ring * sin(azimuth));

    vec3 side = normalize(cross(vec3(0.0, 1.0, 0.0), origin));
    vec3 rise = cross(origin, side);
    float roll = r4 * TAU;
    vec3 course = side * cos(roll) + rise * sin(roll);
    course = mix(course, -course, step(0.0, course.y));
    vec3 axis = cross(origin, course);

    float arc = 0.58 + 0.86 * r5;
    float travelled = arc * u;
    float tail = 0.17 + 0.31 * r4;
    float tailStart = max(0.0, travelled - tail);

    float alongX = dot(dir, origin);
    float alongY = dot(dir, course);
    float angle = atan(alongY, alongX + 0.000001);
    float offAxis = dot(dir, axis);

    float outer = max(0.0, tailStart - angle) + max(0.0, angle - travelled);
    float s = clamp((travelled - angle) / tail, 0.0, 1.0);

    float width = (0.0026 + 0.0024 * r3) * (0.40 + 0.78 * (1.0 - s));
    float radial = (offAxis * offAxis + outer * outer) / (width * width);
    float trail = exp(-radial) * exp(-s * 1.65);

    float sheathWidth = width * 3.6;
    float sheath = exp(-(offAxis * offAxis + outer * outer) / (sheathWidth * sheathWidth)) * exp(-s * 2.1) * 0.30;

    vec3 head = origin * cos(travelled) + course * sin(travelled);
    float headDist = length(dir - head);
    float headSize = 0.0044 + 0.0034 * r2;
    float core = exp(-(headDist * headDist) / (headSize * headSize * 0.45));
    float bloom = exp(-headDist / (headSize * 3.0)) * 0.26;

    float ember = smoothstep(0.78, 0.99, r4);
    vec3 hot = mix(METEOR_HOT, METEOR_EMBER, ember);
    vec3 tone = mix(hot, METEOR_COOL, 0.86 * smoothstep(0.08, 0.82, s));
    float power = (1.35 + 1.55 * r2 * r2 + 1.60 * ember) * env;

    return (tone * (trail * 1.05 + sheath) + METEOR_HOT * core + mix(hot, METEOR_COOL, 0.45) * bloom) * power;
}

// --- Облака ---
//
// Слой имеет толщину, и марш идёт сквозь неё. Плоская проекция шума на купол, что стояла
// здесь раньше, выдавала себя с первого взгляда: рисунок был один и тот же на всей высоте,
// у туч не было ни донышка, ни бока, и при повороте головы небо ехало обоями. Толща даёт
// то, чего плоскости взять неоткуда, — параллакс между дном и верхом, тяжёлые тёмные брюха
// и просветы, сквозь которые видно вглубь.
//
// Марш идёт сверху вниз, и это не мелочь: свет пасмурного неба приходит сверху, поэтому
// накопленная по пути плотность — это ровно то, сколько его осталось до текущего среза.
// Самозатенение получается даром, без второго марша к светилу.

/** Верх и низ облачного слоя в единицах «высота над камерой». */
const float CLOUD_BOTTOM = 1.00;
const float CLOUD_TOP = 2.15;
const int CLOUD_STEPS = 7;

/**
 * Плотность толщи. {@code hf} — доля высоты внутри слоя: ноль у дна, единица у верха. Форма
 * по высоте важнее рисунка: у слоистой облачности плоское тяжёлое дно и размытый рваный верх,
 * и именно этим она отличается от ватных шаров.
 */
// coverBias, floorSoft и erosionDrift зависят только от uniform-ов: их считает cloudLayer один
// раз до марша, а не каждый срез заново. Значения ровно те же, что считались здесь.
float cloudDensity(vec3 p, float hf, float detail, float coverBias, float floorSoft, vec3 erosionDrift) {
    // Форма — две октавы, а не три: третья на срезе всё равно тонет в размытии между
    // срезами, а стоит столько же, сколько первые две вместе с эрозией.
    float d = fbm2(p) - coverBias;
    d -= 0.34 * smoothstep(0.50, 1.0, hf);
    // Плоское донышко. У кучевых облаков хорошей погоды низ лежит на одной высоте у всех до
    // единого: это уровень, где поднимающийся воздух остывает до точки росы, а он один на всю
    // округу. Потому при малом покрытии низ режется резко — и облака сразу читаются
    // отдельными телами, а не клубами тумана. У сплошного фронта низ, наоборот, размазан:
    // там ярусов несколько, и нижний тонет в собственных лохмотьях.
    d *= smoothstep(0.0, floorSoft, hf);
    if (d <= 0.0) {
        return 0.0;
    }
    // Эрозия краёв мелким шумом — только там, где есть что разъедать, и только вблизи:
    // на дальних тучах эта частота всё равно ложится в пиксель и даёт рябь, а считается на
    // каждом срезе. Порог по detail снимает её с половины неба даром.
    if (detail > 0.04) {
        d -= 0.11 * detail * valueNoise3(p * 3.9 + erosionDrift);
    }
    return max(d, 0.0);
}

vec3 cloudLayer(vec3 dir, float sunAlign, out float alpha) {
    alpha = 0.0;
    // Ниже этой высоты слой гасится в ноль хвостовым smoothstep — марш там был чистой
    // тратой, а это половина экрана в любом взгляде вперёд.
    if (CloudCover <= 0.001 || dir.y < 0.021) {
        return vec3(0.0);
    }

    float dy = max(dir.y, 0.055);
    vec2 drift = mod(CameraPosition.xz * 0.0022, 128.0)
            + vec2(time * CloudSpeed, time * CloudSpeed * 0.55);
    float scale = CloudScale * 0.80;

    // Шаг сдвигается на свою долю в каждом пикселе: без этого срезы читаются кольцами
    // вокруг зенита — марш виден как марш.
    //
    // Сдвиг обязан быть разным у СОСЕДНИХ пикселей. Раньше координата экрана масштабировалась
    // перед хешем, и у соседей выходило почти одно и то же значение: сдвиг менялся плавно по
    // кадру, срезы выстраивались вдоль его линий уровня, и небо расчерчивалось наклонными
    // полосами. Здесь взята последовательность с низким расхождением: соседи расходятся
    // всегда, а разброс по кадру при этом ровный, без зернистости белого шума.
    float jitter = fract(dot(gl_FragCoord.xy, vec2(0.7548776662, 0.5698402909)));

    vec3 shadow = CloudShadow * (1.0 - 0.55 * CloudDark);
    vec3 body = vec3(0.0);
    float opticalAbove = 0.0;

    // Всё, что от среза не зависит, — до марша: степени светила, покрытие, снос эрозии.
    // Выражения те же и в том же порядке операций, поэтому и биты те же.
    float cover = clamp(CloudCover, 0.0, 1.0);
    float coverBias = mix(0.66, 0.27, cover);
    float floorSoft = mix(0.035, 0.20, cover);
    vec3 erosionDrift = vec3(0.0, time * CloudSpeed * 0.8, 0.0);
    float litShare = 1.0 - 0.45 * CloudDark;
    vec3 silver = SunColor * pow(sunAlign, 6.0);
    vec3 sunTint = SunColor * 0.06 * pow(sunAlign, 3.0) * (1.0 - CloudDark);
    float absorb = mix(9.5, 5.2, clamp(CloudCover, 0.0, 1.0));

    for (int i = CLOUD_STEPS - 1; i >= 0; i--) {
        float hf = (float(i) + jitter) / float(CLOUD_STEPS);
        float altitude = mix(CLOUD_BOTTOM, CLOUD_TOP, hf);
        float t = altitude / dy;
        float detail = 1.0 - smoothstep(7.0, 26.0, t);
        vec2 hp = (dir.xz * t) * scale + drift;
        vec3 p = vec3(hp.x, hf * 1.7, hp.y);

        float density = cloudDensity(p, hf, detail, coverBias, floorSoft, erosionDrift);
        if (density <= 0.0) {
            continue;
        }

        // Свет, дошедший сверху до этого среза, — по всему, что уже пройдено выше.
        float lightAbove = exp(-opticalAbove * 2.2);
        opticalAbove += density;

        vec3 lit = CloudLit * (0.90 + 0.20 * hf);
        vec3 slice = mix(shadow, lit, lightAbove * litShare);
        // Тёмное брюхо: у дна плотной тучи свет теряется почти весь, и это главный признак
        // пасмурного неба — не цвет, а перепад между донышком и подсвеченным верхом.
        slice *= 0.52 + 0.48 * smoothstep(0.0, 0.60, hf);
        // Серебряная кромка: рассеяние вперёд там, где толща тонкая и светило за ней.
        slice += silver * lightAbove * 0.34 * (1.0 - CloudDark);
        slice += sunTint;

        // Композит от дальнего среза к ближнему: марш идёт с верха слоя, то есть издалека.
        // Кучевое облако плотное: сквозь него не видно неба, и край у него определённый.
        // Сплошная пелена, наоборот, полупрозрачна по всей толще.
        float a = 1.0 - exp(-density * absorb);
        body = slice * a + body * (1.0 - a);
        alpha = a + alpha * (1.0 - a);
    }

    if (alpha <= 0.001) {
        return vec3(0.0);
    }

    float distant = 1.0 - smoothstep(0.06, 0.62, dir.y);
    body = mix(body, SkyHorizon, distant * 0.58);
    alpha *= smoothstep(0.020, 0.185, dir.y);
    return body;
}


// --- Арктика ---------------------------------------------------------------------------------

/**
 * Арктической оптики и ледяной мглы здесь больше нет.
 *
 * <p>Гало, ложные солнца, световой столб, белая мгла у горизонта — явления настоящие, и в
 * мороз все они бывают. Но на экране каждое выдаёт себя формой: кольцо читается нарисованным
 * обручем вокруг солнца, столб — белой полосой поперёк кадра, мгла — молочной лентой по всей
 * дали. В жизни их спасает слабость и то, что глаз ловит их краем; ровная геометрия в кадре
 * так не работает, и каждое из них приходилось объяснять как ошибку. Небо обходится дымкой
 * горизонта, которая есть у любой погоды.
 */

vec3 weatherSky(vec3 dir) {
    float sunAlign = max(dot(dir, normalize(SunDir)), 0.0);

    vec3 color = baseGradient(dir);

    // Звёзды и сияние — под облаками: сплошная облачность их прячет, как и должна.
    float nightClarity = 0.16 + 0.84 * smoothstep(-0.14, 0.30, dir.y);
    if (StarAmount > 0.005 || BandAmount > 0.005) {
        float bandDensity = 0.0;
        vec3 band = vec3(0.0);
        if (BandAmount > 0.005) {
            band = milkyWay(dir, bandDensity) * BandAmount;
        }
        if (StarAmount > 0.005) {
            float shimmer = 1.0 - 0.45 * nightClarity;
            vec3 stars = starLayer(dir, 146.0, 0.050, 0.50, 2.3, 1.0, shimmer) * (0.55 + 4.5 * bandDensity);
            stars += starLayer(dir, 72.0, 0.050, 0.34, 1.5, 37.0, shimmer) * 0.85;
            stars += starLayer(dir, 29.0, 0.050, 0.19, 0.9, 91.0, shimmer);
            band += stars * StarAmount;
        }
        color += band * nightClarity;
    }

    if (AuroraAmount > 0.005) {
        color += aurora(dir) * AuroraAmount;
    }

    if (MeteorAmount > 0.005) {
        float meteorRate = MeteorAmount * 0.22;
        float meteorGlow = min(1.0, MeteorAmount * 2.4);
        for (int i = 0; i < 8; i++) {
            float fi = float(i);
            float rate = (0.30 + 0.36 * fract(fi * 0.6180339 + 0.137)) * meteorRate;
            float phase = fract(fi * 0.7548777 + 0.311);
            color += meteorStreak(dir, 7.31 + fi * 23.17, rate, phase) * meteorGlow;
        }
    }

    color += sunDisc(dir, sunAlign);

    float cloudAlpha;
    vec3 clouds = cloudLayer(dir, sunAlign, cloudAlpha);
    color = mix(color, clouds, cloudAlpha);

    // Молния бьёт в облака и оттуда заливает небо: без привязки к их плотности вспышка выглядит
    // выкрученной яркостью на весь кадр, а не разрядом за тучей.
    if (Flash > 0.001) {
        float glow = 0.35 + 0.65 * cloudAlpha;
        color += FlashColor * (Flash * glow);
    }

    color += horizonHaze(dir);

    color = vec3(1.0) - exp(-color * 1.9);
    color += (hash21(gl_FragCoord.xy) - 0.5) * 0.0045;
    return clamp(color, 0.0, 1.0);
}

/**
 * Небо для отражения. То же самое, но без звёзд, сияния и метеоров: в воде их всё равно не
 * разглядеть, а стоят они дороже всего остального вместе взятого — и считались бы на каждый
 * пиксель каждой лужи.
 */
/**
 * Облака для отражения: две октавы вместо четырёх, без завихрения и без кромок.
 *
 * <p>Отражение в воде всё равно усредняется по конусу шершавости и потом размывается, поэтому
 * мелкая деталь облака в него не доходит — а стоит она дороже всего остального неба вместе
 * взятого: четыре октавы шума на каждый мокрый пиксель кадра. Крупная форма и цвет остаются
 * теми же, и подмену видно только если поставить два кадра рядом и искать.
 */
vec3 cloudLayerCoarse(vec3 dir, out float alpha) {
    alpha = 0.0;
    if (CloudCover <= 0.001) {
        return vec3(0.0);
    }

    vec2 plane = dir.xz / max(dir.y, 0.055);
    vec2 camDrift = mod(CameraPosition.xz * 0.0022, 128.0);
    vec2 q = plane * CloudScale + camDrift + vec2(time * CloudSpeed, time * CloudSpeed * 0.55);

    float density = cloudFbmCoarse(q);
    float threshold = mix(0.62, 0.14, clamp(CloudCover, 0.0, 1.0));

    float cover = smoothstep(threshold - 0.06, threshold + 0.14, density);
    float bulk = smoothstep(threshold - 0.05, threshold + 0.32, density);

    vec3 shadow = CloudShadow * (1.0 - 0.55 * CloudDark);
    vec3 body = mix(shadow, CloudLit * (0.92 + 0.17 * bulk), bulk * (1.0 - 0.45 * CloudDark));

    float distant = 1.0 - smoothstep(0.06, 0.62, dir.y);
    body = mix(body, SkyHorizon, distant * 0.58);

    alpha = cover * smoothstep(0.020, 0.185, dir.y);
    return body;
}

vec3 weatherSkyReflect(vec3 dir) {
    // Луч, ушедший под горизонт, отражать нечем: неба там нет. Заворачиваем его наверх —
    // в мелкой воде под скользящим углом видно ровно то, что над ней.
    dir.y = abs(dir.y) * 0.85 + 0.02;
    dir = normalize(dir);

    float sunAlign = max(dot(dir, normalize(SunDir)), 0.0);
    vec3 color = baseGradient(dir);
    color += sunDisc(dir, sunAlign);

    float cloudAlpha;
    vec3 clouds = cloudLayerCoarse(dir, cloudAlpha);
    color = mix(color, clouds, cloudAlpha);

    if (Flash > 0.001) {
        color += FlashColor * (Flash * (0.35 + 0.65 * cloudAlpha));
    }
    color += horizonHaze(dir);
    color = vec3(1.0) - exp(-color * 1.9);
    return clamp(color, 0.0, 1.0);
}
