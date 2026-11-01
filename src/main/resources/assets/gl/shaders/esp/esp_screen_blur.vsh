#version 150

// То же самое для esp_blur: этот фрагментник ждёт шаг выборки готовым, как vanilla post/blur.

in vec3 Position;

uniform vec2 InSize;
uniform vec2 BlurDir;

out vec2 texCoord;
out vec2 sampleStep;

void main() {
    gl_Position = vec4(Position.xy, 0.0, 1.0);
    texCoord = Position.xy * 0.5 + 0.5;
    sampleStep = BlurDir / InSize;
}
