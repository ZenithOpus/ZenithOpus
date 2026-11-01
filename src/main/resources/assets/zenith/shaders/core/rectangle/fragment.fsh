#version 330

#moj_import <zenith:common.glsl>

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};
layout(std140) uniform ZenithData {
    vec2 Size;
    vec4 Radius;
    float Smoothness;
    vec4 Tab;
    float TabRadius;
    float MainHeight;
    float Joint;
};

in vec2 FragCoord; // normalized fragment coord relative to the primitive
in vec4 FragColor;


out vec4 OutColor;

void main() {
    vec2 local = FragCoord * Size;
    vec2 main_size = vec2(Size.x, MainHeight > 0.0 ? min(MainHeight, Size.y) : Size.y);
    vec2 center = main_size * 0.5;
    float distance = roundedBoxSDF(center - local, center - 1.0, Radius);

    if (Tab.w > 0.0) {
        vec2 tab_center = Tab.xy + Tab.zw * 0.5;
        vec2 tab_half = max(Tab.zw * 0.5 - 1.0, vec2(0.01));
        vec2 q = abs(local - tab_center) - tab_half + TabRadius;
        float tab = min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - TabRadius;
        float k = max(Joint, 0.001);
        float blend = clamp(0.5 + 0.5 * (tab - distance) / k, 0.0, 1.0);
        distance = mix(tab, distance, blend) - k * blend * (1.0 - blend);
    }

    float alpha = 1.0 - smoothstep(1.0 - Smoothness, 1.0, distance);
    vec4 finalColor = vec4(FragColor.rgb, FragColor.a * alpha);

    if (finalColor.a == 0.0) { // alpha test
        discard;
    }

    OutColor = finalColor * ColorModulator;
}
