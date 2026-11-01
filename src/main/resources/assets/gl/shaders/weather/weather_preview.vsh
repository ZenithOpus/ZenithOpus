#version 150

// Вершинник под weather_sky.fsh, когда небо гоняют вручную в карточке меню, а не цепочкой
// пост-обработки. Отличие от vanilla post/sobel одно: квад занимает не весь экран, а только
// прямоугольник превью, и texCoord надо растянуть на него, а не на кадр. Прямоугольник
// приходит в QuadRect уже в отсечённых координатах (x, y, ширина, высота).
//
// Через uniform, а не через второй вершинный атрибут: Shader связывает по имени только
// Position, и добавлять ему разбор атрибутов ради одной карточки незачем.

in vec3 Position;

uniform vec4 QuadRect;
uniform vec2 InSize;

out vec2 texCoord;
out vec2 oneTexel;

void main() {
    gl_Position = vec4(Position.xy, 0.0, 1.0);
    texCoord = (Position.xy - QuadRect.xy) / QuadRect.zw;
    oneTexel = 1.0 / InSize;
}
