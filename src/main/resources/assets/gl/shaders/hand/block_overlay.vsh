#version 150

in vec3 Position;

out vec2 handUv;

void main() {
    gl_Position = vec4(Position, 1.0);
    handUv = (Position.xy + 1.0) * 0.5;
}
