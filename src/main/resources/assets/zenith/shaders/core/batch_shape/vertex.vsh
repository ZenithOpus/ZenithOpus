#version 330

#moj_import <zenith:common.glsl>

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};
layout(std140) uniform Projection {
    mat4 ProjMat;
};
layout(std140) uniform ZenithData {
    vec2 Smoothness;
};

// Единый батч фигур: заливки, контуры и уголки лежат в одном буфере.
// Раньше у каждого вида был свой шейдер и свой вызов, а всё, что различается от фигуры к
// фигуре, жило в uniform — из-за этого пачка сбрасывалась почти на каждой фигуре. Теперь
// различия приезжают вершиной.
in vec3 Position;
in vec4 Color;
in vec2 UV0;    // размер фигуры в пикселях
in vec4 Radii;  // скругления: верхнее левое, нижнее левое, верхнее правое, нижнее правое
in vec2 Params; // x — толщина контура, y — вид фигуры

out vec2 fragRectCoord;
out vec2 fragSize;
out vec4 fragRadius;
out vec4 FragColor;
out float fragThickness;
out float fragMode;

void main() {
    fragRectCoord = rvertexcoord(gl_VertexID);
    fragSize = UV0;
    fragRadius = Radii;
    fragThickness = Params.x;
    fragMode = Params.y;
    FragColor = Color;

    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
}
