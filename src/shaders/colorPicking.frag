#version 330

uniform vec3 uniqueId;

out vec4 outColour;

void main (void) {
	outColour = vec4(uniqueId, 1.0);
}