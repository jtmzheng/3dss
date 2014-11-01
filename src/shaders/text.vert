#version 330

in vec2 in_Position;

// texture (uv) coordinates for the fragment shader to sample.
in vec2 in_UV;
out vec2 UV;

// dimensions of the display
uniform vec2 screenDimensions;

void main(){
	// convert screen space to homogenous space (to [-1..1][-1..1]).
	vec2 homogenous = in_Position;
	homogenous -= screenDimensions/2;
	homogenous /= screenDimensions/2;
	gl_Position =  vec4(homogenous,0,1);
	
	UV = in_UV;
}