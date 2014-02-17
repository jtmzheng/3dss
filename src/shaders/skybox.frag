#version 330

uniform samplerCube cubeTexture;

in vec3 vt;
out vec4 out_Color;

void main () {
  out_Color = texture(cubeTexture, vt);
}