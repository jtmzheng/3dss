#version 330

in vec2 UV;
out vec4 color;

uniform sampler2D textureSampler;

void main(){
	color = texture2D( textureSampler, UV );
}
