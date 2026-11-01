#version 330

// Полноэкранный квад с шагом выборки для размытия по одной оси — замена minecraft:post/blur.
// Блок ZenithPost обязан совпадать с блоком фрагментного шейдера поле в поле, иначе программа
// не слинкуется.

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};
layout(std140) uniform ZenithPost {
    vec2 BlurDir;
    float Radius;
};

out vec2 texCoord;
out vec2 sampleStep;

void main() {
    vec2 uv = vec2((gl_VertexID << 1) & 2, gl_VertexID & 2);
    gl_Position = vec4(uv * 2.0 - 1.0, 0.0, 1.0);
    texCoord = uv;
    sampleStep = BlurDir / InSize;
}
