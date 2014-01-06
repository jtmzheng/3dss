#version 330 core

// vertex positions input attribute
in vec2 vp;

// per-vertex texture coordinates input attribute
in vec2 vt;

// texture coordinates to be interpolated to fragment shaders
out vec2 st;

uniform mat4 projectionMatrix;

void main (void) {
	// interpolate texture coordinates
	mat4 test = projectionMatrix;
	st = vt;
	// transform vertex position to clip space (camera view and perspective)
	gl_Position = vec4(vp, 0.0, 1.0);
}