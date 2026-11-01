#version 330 core

in vec2 handUv;
out vec4 color;

uniform sampler2D SourceTex;

void main() {
    vec4 source = texture(SourceTex, handUv);
    float channel = max(max(source.r, source.g), max(source.b, source.a));
    float mask = smoothstep(0.004, 0.045, channel);
    color = vec4(vec3(mask), mask);
}
