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
    vec4 Radius;
    float Smoothness;
};

in vec3 Position;
in vec2 UV0;
in vec4 Color;

out vec2 FragCoord;
out vec2 TexCoord;
out vec2 FragSize;
out vec4 FragColor;

void main() {
    vec4 clipPos = ProjMat * ModelViewMat * vec4(Position, 1.0);
    gl_Position = clipPos;

    FragCoord = rvertexcoord(gl_VertexID);
    TexCoord = clipPos.xy * 0.5 + 0.5;
    FragSize = UV0;
    FragColor = Color;
}
