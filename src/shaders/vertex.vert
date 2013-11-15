#version 330 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

in vec4 in_Position;
in vec4 in_Color;
in vec2 in_Texture;
in vec4 in_Normal;

in vec3 Ks; 
in vec3 Ka;
in float specExp; //specular "power"

out vec4 pass_Color;
out vec3 position_eye, normal_eye;
out vec3 sKs;
out vec3 sKd;
out vec3 sKa;
out float sSpecExp;

void main(void) {

	mat4 vm = viewMatrix * modelMatrix;
	position_eye = vec3(vm * in_Position);
	normal_eye = vec3(vm * vec4(vec3(in_Normal), 0.0)); //TODO, might as well pass normals as vec3
	
	sKs = Ks;
	sKd = vec3(pass_Color); //TODO should be pass_Color
	sKa = Ka;
	sSpecExp = specExp;
	
    gl_Position = projectionMatrix * vm * in_Position;
	pass_Color = in_Color;
}
