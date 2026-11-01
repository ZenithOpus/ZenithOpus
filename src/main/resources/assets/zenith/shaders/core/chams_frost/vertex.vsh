#version 330

#moj_import <minecraft:fog.glsl>

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
    float iTime;
    vec4 PrimaryColor4;
    vec4 SecondaryColor4;
    float AlphaMul;
    float FresnelPower;
    float Freq;
    float ColorTransfer;
    float Brightness;
    float MirrorEnabled;
    vec2 ScreenSize;
};
// vec3 в std140 трактуется драйверами по-разному: тут он хранится как vec4,
// а имя остаётся прежним, чтобы не трогать тела шейдеров.
#define PrimaryColor PrimaryColor4.xyz
#define SecondaryColor SecondaryColor4.xyz


in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec3 Normal;

out float sphericalVertexDistance;
out float cylindricalVertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;
out vec3 viewNormal;
out vec3 viewPos;
out vec3 modelPos;

void main() {
    vec4 mvPos = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * mvPos;

    sphericalVertexDistance = fog_spherical_distance(Position);
    cylindricalVertexDistance = fog_cylindrical_distance(Position);
    vertexColor = Color;
    texCoord0 = UV0;
    viewNormal = normalize(mat3(ModelViewMat) * Normal);
    viewPos = mvPos.xyz;
    modelPos = Position;
}
