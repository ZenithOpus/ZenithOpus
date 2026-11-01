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
    vec2 Size;
    vec4 Radius;
    float Smoothness;
};

in vec3 Position; // POSITION_COLOR vertex attributes
in vec4 Color;

out vec2 FragCoord;
out vec2 TexCoord;
out vec4 FragColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    FragCoord = rvertexcoord(gl_VertexID);
    TexCoord = gl_Position.xy * 0.5 + 0.5;
    FragColor = Color;
}
