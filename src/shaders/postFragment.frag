#version 330

// texture coordinates from vertex shaders
in vec2 st;

uniform mat4 projectionMatrix;

// post process texture samplers
uniform sampler2D fbTex;
uniform sampler2D depthBuffTex;

// output fragment colour RGBA
out vec4 frag_colour;

float linearizeDepth(float depth, mat4 projectionMatrix);

void main (void) {
	// invert colour of right-hand side
	vec3 colour;
	if (st.s >= 0.5) {
		colour = linearizeDepth(texture(depthBuffTex, st).x, projectionMatrix).rrr;
	} else {
		colour = texture(fbTex, st).rgb;
	}

	frag_colour = vec4(colour, 1.0);
}