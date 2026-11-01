#version 330 core

in vec2 handUv;
out vec4 outColor;

uniform sampler2D ColorTexture;
uniform sampler2D DepthTexture;

void main() {
    vec4 color = texture(ColorTexture, handUv);
    float depth = texture(DepthTexture, handUv).r;
    if (depth >= 0.9999 || color.a < 0.02) {
        discard;
    }
    outColor = vec4(color.rgb, 1.0);
}
