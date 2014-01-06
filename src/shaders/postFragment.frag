#version 330 core

// texture coordinates from vertex shaders
in vec2 st;

// post process texture sampler
uniform sampler2D fbTex;

// output fragment colour RGBA
out vec4 frag_colour;

void main (void) {
	// invert colour of right-hand side
	vec3 colour;
	if (st.s >= 0.5) {
		colour = 1.0 - texture(fbTex, st).rgb;
	} else {
		colour = texture(fbTex, st).rgb;
	}
	frag_colour = vec4(colour, 1.0);
}