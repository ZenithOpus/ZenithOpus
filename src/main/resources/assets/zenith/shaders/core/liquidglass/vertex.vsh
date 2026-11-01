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
    vec4 Tab;
    float TabRadius;
    float MainHeight;
    float Joint;
    vec4 Radius;
    float Smoothness;
    float CornerSmoothness;
    float GlobalAlpha;
    float FresnelPower;
    vec4 FresnelColor4;
    float FresnelAlpha;
    float BaseAlpha;
    float FresnelInvert;
    float FresnelMix;
    float DistortStrength;
    float Time;
    vec4 GlareColor4;
    float GlareAlpha;
    float GlareSpeed;
    float GlareMode;
    float GlareTexture;
};
// vec3 в std140 трактуется драйверами по-разному: тут он хранится как vec4,
// а имя остаётся прежним, чтобы не трогать тела шейдеров.
#define FresnelColor FresnelColor4.xyz
#define GlareColor GlareColor4.xyz


in vec3 Position; // POSITION_TEXTURE_COLOR vertex attributes
in vec2 UV0;
in vec4 Color;

out vec2 FragCoord;
out vec2 TexCoord;
out vec4 FragColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    FragCoord = rvertexcoord(gl_VertexID);
    TexCoord = UV0;
    FragColor = Color;
} 