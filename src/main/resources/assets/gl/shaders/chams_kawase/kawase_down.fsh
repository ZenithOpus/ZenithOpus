#version 150

in vec2 uv;
out vec4 fragColor;

uniform sampler2D image;
uniform float offset;
uniform vec2 resolution;

void main() {
    vec2 halfpixel = resolution * 2.0;
    vec3 sum = texture(image, uv).rgb * 4.0;

    sum += texture(image, uv - halfpixel * offset).rgb;
    sum += texture(image, uv + halfpixel * offset).rgb;
    sum += texture(image, uv + vec2(halfpixel.x, -halfpixel.y) * offset).rgb;
    sum += texture(image, uv - vec2(halfpixel.x, -halfpixel.y) * offset).rgb;

    fragColor = vec4(sum / 8.0, 1.0);
}
