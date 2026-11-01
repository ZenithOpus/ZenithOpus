#version 330 core

in vec2 handUv;
out vec4 outColor;

uniform sampler2D ColorTexture;
uniform sampler2D DepthTexture;
uniform vec2 resolution;
uniform float time;
uniform vec2 speed;
uniform float shift;
uniform vec2 iMouse;

#define surfacePosition ((handUv - handMotion) * 2.0)

float handDepthMask(vec2 sampleUv) {
    if (sampleUv.x < 0.0 || sampleUv.y < 0.0 || sampleUv.x > 1.0 || sampleUv.y > 1.0) {
        return 0.0;
    }

    float depthValue = texture(DepthTexture, sampleUv).r;
    return smoothstep(0.999, 0.990, depthValue);
}

float sampleMask(vec2 sampleUv) {
    if (sampleUv.x < 0.0 || sampleUv.y < 0.0 || sampleUv.x > 1.0 || sampleUv.y > 1.0) {
        return 0.0;
    }

    return max(texture(ColorTexture, sampleUv).a, handDepthMask(sampleUv));
}

vec4 handTexture(vec2 sampleUv) {
    vec4 sampledColor = texture(ColorTexture, sampleUv);
    sampledColor.a = max(sampledColor.a, sampleMask(sampleUv));
    return sampledColor;
}

#define texture2D(sourceTexture, sampleUv) handTexture(sampleUv)
uniform vec2 handMotion;
uniform float effectAlpha;

vec2 handEffectCoord(vec2 sampleUv) {
    return (sampleUv - handMotion + 0.5) * resolution.xy;
}

vec4 handFragCoord4() {
    return vec4(handEffectCoord(handUv), 0.0, 1.0);
}

// Converted from Sirius chams shader: golden.frag
#define iterations 15

#define formuparam 0.330

#define volsteps 12
#define stepsize 0.10

#define zoom   2.0
#define tile   0.40
#define speed  .01

#define brightness 0.0019
#define darkmatter 0.40
#define distfading 0.99
#define saturation 1.60

void main() {
	vec4 centerCol = texture2D(texture, handUv);

    if (centerCol.a <= 0.001) {
        discard;
    }

	//get coords and direction
	vec2 uv=handFragCoord4().xy/resolution.xy-.5;
	uv.y*=resolution.y/resolution.x;
	vec3 dir=vec3(uv*zoom,1.);
	
	float a2=time*speed+.5;
	float a1=5.0;
	mat2 rot1=mat2(cos(a1),sin(a1),-sin(a1),cos(a1));
	mat2 rot2=rot1;//mat2(cos(a2),sin(a2),-sin(a2),cos(a2));
	dir.xz*=rot1;
	dir.xy*=rot2;
	
	vec3 from=vec3(0.,0.,0.);
	from+=vec3(.0*time,.1*time, 0);
		
	from.xz*=rot1;
	from.xy*=rot2;
	
	//volumetric rendering
	float s=.1,fade=.05;
	vec3 v=vec3(.05);
	for (int r=0; r<volsteps; r++) {
		vec3 p=from+s*dir*1.5;
		p = abs(vec3(tile)-mod(p,vec3(tile*2.))); // tiling fold
		p.x+=float(r*r)*0.01;
		p.y+=float(r)*0.02;
		float pa,a=pa=0.;
		//Creates the Particles
		for (int i=0; i<iterations; i++) { 
			p=abs(p)/dot(p,p)-formuparam; // the magic formula
			a+=abs(length(p)-pa*0.1); // absolute sum of average change
			pa=length(p);
		}
		float dm=max(0.1,darkmatter-a*a*.9); //dark matter
		a*=a*a*2.; // add contrast
		if (r>3) fade*=1.-dm; // dark matter, don't render near
		v+=vec3(dm,dm*.0,0.6);
		v+=fade;
		v+=vec3(s,s*s,s*s*s*s*s)*a*brightness*fade; // coloring based on distance
		fade*=distfading; // distance fading
		s+=stepsize;
	}
	v=mix(vec3(length(v)),v,saturation); //color adjust

	float alpha = 1.0;

	outColor = vec4(v*.01, alpha);
	outColor.a *= effectAlpha;
}
