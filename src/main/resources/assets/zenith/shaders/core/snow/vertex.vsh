#version 330

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
    mat4 InverseView;
    vec4 Origin4;
    vec4 SunDir4;
    vec4 SunColor4;
    vec4 SkyColor4;
    float GrainScale;
    float WrapPeriod;
    float GrainStrength;
    float CrustAmount;
    float SparkleAmount;
    float Wetness;
    float Sheen;
};
// vec3 в std140 трактуется драйверами по-разному: тут он хранится как vec4,
// а имя остаётся прежним, чтобы не трогать тела шейдеров.
#define Origin Origin4.xyz
#define SunDir SunDir4.xyz
#define SunColor SunColor4.xyz
#define SkyColor SkyColor4.xyz


in vec3 Position;
in vec4 Color;

out vec3 WorldPos;
out vec3 ViewPos;
out vec4 FragColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vec3 relative = (InverseView * vec4(Position, 1.0)).xyz;
    WorldPos = relative + Origin;
    ViewPos = relative;
    FragColor = Color;
}
