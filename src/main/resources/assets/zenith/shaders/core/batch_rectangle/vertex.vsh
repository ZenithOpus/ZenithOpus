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

out vec2 fragRectCoord;
out vec2 fragSize;
out vec4 FragColor;

void main() {

    fragRectCoord = rvertexcoord(gl_VertexID);
    fragSize = UV0;
    FragColor = Color;
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
}
