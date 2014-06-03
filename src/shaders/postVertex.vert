#version 330

uniform mat4 gFrustumCorners;

layout(location = 0) in vec2 vp;
layout(location = 1) in vec2 vt;
layout(location = 2) in int iFrustum;

// texture coordinates to be interpolated to fragment shaders
out vec2 st;
out vec3 vToFarPlane;

void main (void) {
	// interpolate texture coordinates
	st = vt;
	
	// interpolate the corners
	vToFarPlane = gFrustumCorners[iFrustum].xyz;
	
	// transform vertex position to clip space (camera view and perspective)
	gl_Position = vec4(vp, 0.0, 1.0);
}