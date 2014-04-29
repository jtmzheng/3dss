#version 330

// This will be a reference post processing fragment shader

uniform mat4 projectionMatrix;
uniform float near_plane; // Default is 0.1
uniform float far_plane; // Default is 100

// post process texture samplers
uniform sampler2D fbTex;
uniform sampler2D depthBuffTex;
uniform sampler2D normalTex;
uniform sampler2D noiseTex;

// texture coordinates from vertex shaders
in vec2 st;

// output fragment colour RGBA
layout(location = 0) out vec4 frag_colour;

float linearizeDepth(in float depth, in float n, in float f);
vec3 decodeNormal(in vec3 normal);

void main (void) {
	// invert colour of right-hand side
	vec3 colour;
	if(st.t >= 0.5 && st.s >= 0.5) {
		colour = decodeNormal(texture(normalTex, st).rgb);
	} else if(st.s >= 0.5) {
		float depth = texture(depthBuffTex, st).x;
		float linDepth = linearizeDepth(depth, near_plane, far_plane);
		colour = linDepth.rrr;		
	} else if(st.t >= 0.5) {
		colour = texture(noiseTex, st).rgb;
	} else {
		colour = texture(fbTex, st).rgb;
	}

	frag_colour = vec4(colour, 1.0);
}