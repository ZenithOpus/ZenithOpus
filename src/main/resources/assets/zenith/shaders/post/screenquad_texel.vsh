#version 330

// Полноэкранный квад с размером текселя.
//
// В 1.21.4 эти проходы брали вершинный шейдер minecraft:post/sobel, который отдавал texCoord и
// oneTexel. В 1.21.11 таких шейдеров в ванили не осталось — есть только core/screenquad, а он
// даёт одни координаты. Размер текселя теперь считается из блока SamplerInfo, который движок
// заполняет сам.

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

out vec2 texCoord;
out vec2 oneTexel;

void main() {
    vec2 uv = vec2((gl_VertexID << 1) & 2, gl_VertexID & 2);
    gl_Position = vec4(uv * 2.0 - 1.0, 0.0, 1.0);
    texCoord = uv;
    oneTexel = 1.0 / InSize;
}
