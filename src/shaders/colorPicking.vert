#version 320

in vec3 vp;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main () {
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4 (vp, 1.0);
}