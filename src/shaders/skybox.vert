#version 330

uniform mat4 projectionMatrix; 
uniform mat4 viewMatrix;

in vec3 vp;
out vec3 vt;

void main () {
  vt = vp;
  gl_Position = projectionMatrix * viewMatrix * vec4 (vp, 1.0);
}