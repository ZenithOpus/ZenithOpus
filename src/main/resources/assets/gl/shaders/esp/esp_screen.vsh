#version 150

// Вершинник под фрагментники пост-эффекта ESP (esp_seed, shader_esp), когда их гоняют вручную
// из меню, а не цепочкой пост-обработки. Выдаёт ровно те же varying, что vanilla post/sobel:
// иначе пришлось бы держать вторую копию фрагментника под превью, а она бы разошлась с игрой.
//
// Квад приходит уже в отсечённых координатах (-1..1), поэтому ProjMat/OutSize не нужны —
// texCoord считается прямо из позиции.

in vec3 Position;

uniform vec2 InSize;

out vec2 texCoord;
out vec2 oneTexel;

void main() {
    gl_Position = vec4(Position.xy, 0.0, 1.0);
    texCoord = Position.xy * 0.5 + 0.5;
    oneTexel = 1.0 / InSize;
}
