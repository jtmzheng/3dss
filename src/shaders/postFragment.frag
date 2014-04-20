#version 330

uniform mat4 projectionMatrix;
uniform float near_plane; // Default is 0.1
uniform float far_plane; // Default is 100

// post process texture samplers
uniform sampler2D fbTex;
uniform sampler2D depthBuffTex;

// texture coordinates from vertex shaders
in vec2 st;

// output fragment colour RGBA
out vec4 frag_colour;

float linearizeDepth(float depth, float n, float f);

void main (void) {
	// invert colour of right-hand side
	vec3 colour;
	if (st.s >= 0.5) {
		float depth = texture(depthBuffTex, st).x;
		float linDepth = linearizeDepth(depth, near_plane, far_plane);
		colour = linDepth.rrr;
		
	} else {
		colour = texture(fbTex, st).rgb;
	}

	frag_colour = vec4(colour, 1.0);
}