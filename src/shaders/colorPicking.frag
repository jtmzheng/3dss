#version 320
uniform vec3 uniqueId;
out vec4 outColour;

void main () {
	outColour = vec4 (uniqueId, 1.0);
}