#version 330

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

in vec4 in_Position;
in vec4 in_Color;
in vec2 in_Texture;
in vec4 in_Normal;

in vec3 Ks; 
in vec3 Ka;

out vec2 pass_texture;
out vec3 position_eye, normal_eye;
out vec3 sKs;
out vec3 sKd;
out vec3 sKa;

void main(void) {

	mat4 vm = viewMatrix * modelMatrix;
	position_eye = (vm * in_Position).xyz;
	normal_eye = (vm * vec4(in_Normal.xyz, 0.0)).xyz; 
	
	sKs = Ks;
	sKd = in_Color.rgb; 
	sKa = Ka;
	
    gl_Position = projectionMatrix * vm * in_Position;
	pass_texture = in_Texture;
}
