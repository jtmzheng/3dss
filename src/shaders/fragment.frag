#version 330 core

const float SPOT_ARC = 25.0 / 90.0;
const int MAX_NUM_LIGHTS = 32;

uniform vec3 light_position = vec3(10.0, 10.0, 10.0);
uniform vec3 Ls = vec3(1.0, 1.0, 1.0); //white specular colour
uniform vec3 Ld = vec3(0.7, 0.7, 0.7); //dull white diffuse
uniform vec3 La = vec3(0.2, 0.2, 0.2); //grey ambient
uniform mat4 viewMatrixFrag;
uniform vec3 spotDirection;

// Fields set to default value by OpenGL
struct lightSrc 
{
	vec3 position;
	vec3 direction; // is (0, 0, 0) if not directional
	vec3 Ls;
	vec3 Ld;
	float specExp;
	float isUsed; // set to 0 by default
	float isDirectional; // is it a spotlight
};

uniform lightSrc lights[MAX_NUM_LIGHTS];

in vec3 sKs, sKd, sKa;
in vec4 pass_Color;
in vec3 position_eye, normal_eye;

out vec4 out_Color;

void main(void) {

	vec3 Ia = La * sKa;
	vec3 Id = vec3(0, 0, 0), Is = vec3(0, 0, 0);
	
	for(int index = 0; index < MAX_NUM_LIGHTS; index++) {
	    if(lights[index].isUsed > 0.5){
			vec3 light_position_eye = vec3(viewMatrixFrag * vec4(lights[index].position, 1.0));
			vec3 sLightEye = light_position_eye - position_eye;
			vec3 dirLightEye = normalize(sLightEye);	    
	    
	    	float dotLightEye = dot(dirLightEye, normalize(normal_eye));
			dotLightEye = max(dotLightEye, 0.0); //clamp to 0
	    
	    	vec3 reflectionEye = reflect(-dirLightEye, normal_eye);
			vec3 surfaceViewerEye = normalize(-position_eye);
	    
	    	float dotSpecular = dot(normalize(reflectionEye), surfaceViewerEye);
			dotSpecular = max(dotSpecular, 0.0);
	
			float specFactor = pow(dotSpecular, lights[index].specExp);
	
			Id += Id + lights[index].Ld * sKd * dotLightEye;	
			Is += Is + lights[index].Ls * sKs * specFactor;	
			
			if(lights[index].isDirectional > 0.5){
				// Directional lighting
			}
	    } 
	    else {
	    	// Testing code for when no lights
	    }
	}
	
	out_Color = vec4(Is + Id + Ia, 1.0);
	
}




/*
void main(void) {

	
	out_Color = pass_Color;
	//vec4(Is + Id + Ia, 1.0);
	
}
*/