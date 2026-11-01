#version 330

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};
layout(std140) uniform ZenithData {
    float Range;
    float Thickness;
    float Smoothness;
    float Outline;
    float OutlineThickness;
    vec4 OutlineColor;
    float EnableFadeout;
    float FadeoutStart;
    float FadeoutEnd;
    float MaxWidth;
    float TextPosX;
};

in vec2 TexCoord;
in vec4 FragColor;
in vec2 GlobalPos;

uniform sampler2D Sampler0;

out vec4 OutColor;

float median(vec3 color) {
    return max(min(color.r, color.g), min(max(color.r, color.g), color.b));
}

void main() {
    float dist = median(texture(Sampler0, TexCoord).rgb) - 0.5 + Thickness;

    // Distance field width in screen pixels (screenPxRange, per the msdfgen reference).
    // This used to take the length of the derivative vector instead of the per-axis
    // mean, which is exactly sqrt(2) too small and stretched the edge transition to
    // 1.27 pixels instead of one - at any size. Small text has stems about one pixel
    // wide, so the whole glyph became a single soft ramp: that was the blur.
    // fwidth also stays correct under rotation and non-uniform scale; the floor on the
    // denominator guards degenerate quads.
    vec2 unitRange = vec2(Range) / vec2(textureSize(Sampler0, 0));
    vec2 screenTexSize = vec2(1.0) / max(fwidth(TexCoord), vec2(1e-6));
    // Below one the field is shorter than a pixel: without the clamp such text does not
    // get sharper, it just fades out evenly.
    float pixels = max(0.5 * dot(unitRange, screenTexSize), 1.0);

    // Stem darkening for small sizes. Coverage antialiasing eats thin stems and the text
    // comes out pale, which reads as blurry - so the edge is pushed out further the
    // smaller the glyph is. The amount is expressed in screen pixels and divided by
    // pixels because dist is multiplied by it below, so the edge shift depends on neither
    // the size nor the atlas parameters. Threshold is in units of pixels: every atlas in
    // the client is size=64 with distanceRange=10, so glyph size in pixels is roughly
    // pixels * 6.4 - darkening starts around 14 px and is fully applied below 7 px.
    dist += (1.0 - smoothstep(1.1, 2.2, pixels)) * 0.10 / pixels;

    // Linear transition instead of smoothstep: this is the reference form and its edge is
    // noticeably crisper. Smoothness keeps its old meaning - half the transition width in
    // pixels - so existing values stay valid.
    float ramp = max(2.0 * Smoothness, 0.0001);
    float alpha = clamp(dist * pixels / ramp + 0.5, 0.0, 1.0);
    vec4 color = vec4(FragColor.rgb, FragColor.a * alpha);

    if (Outline > 0.5) {
        color = mix(OutlineColor, FragColor, alpha);
        color.a *= clamp((dist + OutlineThickness) * pixels / ramp + 0.5, 0.0, 1.0);
    }


    if (EnableFadeout > 0.5) {
        float fadeAlpha = 1.0;
        float invWidth = 1.0 / max(MaxWidth, 0.0001);
        float normalizedX = (GlobalPos.x - TextPosX) * invWidth;
        if (normalizedX > FadeoutStart) {
            fadeAlpha = 1.0 - smoothstep(FadeoutStart, FadeoutEnd, normalizedX);
        }
        color.a *= fadeAlpha;
    }

    OutColor = color * ColorModulator;
}