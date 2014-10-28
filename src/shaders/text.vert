#version 330

// vertex position input attribute
in vec2 in_Position;

// texture coordinates input attribute
in vec2 in_UV;

// texture coordinates to be interpolated
out vec2 UV;

// TODO: account for generic resolutions
void main(){

	// maps [0..800][0..600] -> [-400..400][-300..300]
	vec2 vp_screen = in_Position - vec2(400,300);
	
	// maps [-400..400][-300..300] -> [-1..1][-1..1]
	vp_screen /= vec2(400,300);
	gl_Position =  vec4(vp_screen, 0, 1);
	
	UV = in_UV;
}
