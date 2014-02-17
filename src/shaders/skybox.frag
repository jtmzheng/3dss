#version 400

uniform samplerCube cubeTexture;

in vec3 vt;
out vec4 fragColour;

void main () {
  fragColour = texture(cubeTexture, vt);
}