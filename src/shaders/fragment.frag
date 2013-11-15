#version 330 core

uniform vec3 light_position = vec3(10.0, 10.0, 10.0);
uniform vec3 Ls = vec3(1.0, 1.0, 1.0); //white specular colour
uniform vec3 Ld = vec3(0.7, 0.7, 0.7); //dull white diffuse
uniform vec3 La = vec3(0.2, 0.2, 0.2); //grey ambient
uniform mat4 viewMatrixFrag;

in vec3 sKs, sKd, sKa;
in float sSpecExp;
in vec4 pass_Color;
in vec3 position_eye, normal_eye;

out vec4 out_Color;

void main(void) {

	vec3 light_position_eye = vec3(viewMatrixFrag * vec4(light_position, 1.0));
	vec3 sLightEye = light_position_eye - position_eye;
	vec3 dirLightEye = normalize(sLightEye);
	
	float dotLightEye = dot(dirLightEye, normalize(normal_eye));
	dotLightEye = max(dotLightEye, 0.0); //clamp to 0
	
	vec3 reflectionEye = reflect(-dirLightEye, normal_eye);
	vec3 surfaceViewerEye = normalize(-position_eye);
	
	float dotSpecular = dot(normalize(reflectionEye), surfaceViewerEye);
	dotSpecular = max(dotSpecular, 0.0);
	
	float specFactor = pow(dotSpecular, sSpecExp);
	
	vec3 Id = Ld * sKd * dotLightEye;	
	vec3 Ia = La * sKa;
	vec3 Is = Ls * sKs * specFactor;
	
	out_Color = vec4(Is + Id + Ia, 1.0);
	
}




/*
void main(void) {

	
	out_Color = pass_Color;
	//vec4(Is + Id + Ia, 1.0);
	
}
*/