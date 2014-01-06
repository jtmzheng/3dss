#version 330 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

in vec4 in_Position;

void main (void) {
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * in_Position;
}