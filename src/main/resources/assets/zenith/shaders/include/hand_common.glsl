// Shared helpers for hand post-effect shaders in zenith:post/hand_*
//
// Provides:
//   - Standard Minecraft post-effect samplers/uniforms (InSampler, DepthSampler, InSize)
//   - Hand-specific uniforms (Time, HandMotion, EffectAlpha, PatternSpeed, PatternShift,
//     GlowColor, GlowRadius)
//   - Convenience aliases (time/resolution/speed/shift/handMotion/effectAlpha/glowColor/glowRadius)
//   - Helper functions that mimic the original Sirius hand-shader API
//     (handDepthMask, handMaskAlpha, handSample, handFragCoord)
//
// The vertex shader is minecraft:post/blit, which provides texCoord in [0..1].
// Every fragment shader must write to the standard fragColor output.

uniform sampler2D InSampler;
uniform sampler2D DepthSampler;
uniform vec2 InSize;
uniform float Time;
uniform vec2 HandMotion;
uniform float EffectAlpha;
uniform vec2 PatternSpeed;
uniform float PatternShift;
uniform vec4 GlowColor;
uniform float GlowRadius;

in vec2 texCoord;

out vec4 fragColor;

#define handUv texCoord
#define resolution InSize
#define time Time
#define handMotion HandMotion
#define effectAlpha EffectAlpha
#define speed PatternSpeed
#define shift PatternShift
#define glowColor GlowColor
#define glowRadius GlowRadius

float handDepthMask(vec2 sampleUv) {
    if (sampleUv.x < 0.0 || sampleUv.y < 0.0 || sampleUv.x > 1.0 || sampleUv.y > 1.0) {
        return 0.0;
    }
    float depthValue = texture(DepthSampler, sampleUv).r;
    return 1.0 - smoothstep(0.990, 0.999, depthValue);
}

float handMaskAlpha(vec2 sampleUv) {
    if (sampleUv.x < 0.0 || sampleUv.y < 0.0 || sampleUv.x > 1.0 || sampleUv.y > 1.0) {
        return 0.0;
    }
    return max(texture(InSampler, sampleUv).a, handDepthMask(sampleUv));
}

vec4 handSample(vec2 sampleUv) {
    vec4 sampled = texture(InSampler, sampleUv);
    sampled.a = max(sampled.a, handMaskAlpha(sampleUv));
    return sampled;
}

vec2 handFragCoord() {
    return (texCoord - HandMotion + 0.5) * InSize;
}

vec4 handFragCoord4() {
    return vec4(handFragCoord(), 0.0, 1.0);
}

