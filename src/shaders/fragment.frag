#version 330 core

const float SPOT_ARC = cos(13.66 / 90.0);
const int MAX_NUM_LIGHTS = 30;

uniform vec3 La = vec3(0.2, 0.2, 0.2); //grey ambient
uniform mat4 viewMatrixFrag;

// Texturing
uniform sampler2D textureSampler;
// uniform sampler2D textures [3];

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
	
	// Attenuation
	vec3 attenuation;
};

uniform lightSrc lights[MAX_NUM_LIGHTS];

in vec3 sKs, sKd, sKa;
in vec2 pass_texture;
in vec3 position_eye, normal_eye;

out vec4 out_Color;

void main(void) {
	vec3 Ia = La * sKa / 2;
	vec3 Id = vec3(0, 0, 0), Is = vec3(0, 0, 0), tIa = vec3(0, 0, 0);
	vec3 lightTotal = Ia; // total light adjusted color
	
	for(int index = 0; index < MAX_NUM_LIGHTS; index++) {			
	    if(lights[index].isUsed > 0.5){    
	    	float fAttTotal = 1.0; // total attenuation
	    	vec3 tId = vec3(0, 0, 0), tIs = vec3(0, 0, 0); // diffuse and specular component of this light
	    	
			vec3 light_position_eye = vec3(viewMatrixFrag * vec4(lights[index].position, 1.0));
			vec3 sLightFragmentEye = light_position_eye - position_eye;
			vec3 dirLightFragmentEye = normalize(sLightFragmentEye); // direction from light to surface 
	    
	    	float dotLightEye = dot(dirLightFragmentEye, normalize(normal_eye));
			dotLightEye = max(dotLightEye, 0.0); //clamp to 0
	    
	    	vec3 reflectionEye = reflect(-dirLightFragmentEye, normal_eye);
			vec3 surfaceViewerEye = normalize(-position_eye);
	    
	    	float dotSpecular = abs(dot(normalize(reflectionEye), surfaceViewerEye));
			dotSpecular = min(dotSpecular, 1.0);
	
			float specFactor = pow(dotSpecular, lights[index].specExp);
	
			// Attenuation of light over distance
   			float fDist = length(sLightFragmentEye); // distance between light and position of fragment
   			float constAtt = lights[index].attenuation[0];
   			float linearAtt = lights[index].attenuation[1];
   			float quadAtt = lights[index].attenuation[2];
   			    
   			fAttTotal = constAtt + linearAtt * fDist + quadAtt * fDist * fDist;
   			
			// Get light component due to current light
			tId = lights[index].Ld * sKd * dotLightEye;	
			tIs = lights[index].Ls * sKs * specFactor;	
			tIa = Ia / 2;
			
			// If directional lighting is enabled
			if(lights[index].isDirectional > 0.5){
				vec3 light_look_at = lights[index].position + lights[index].direction;
				vec3 light_look_at_eye = vec3(viewMatrixFrag * vec4(light_look_at, 1.0));
				vec3 dir_eye = normalize(light_position_eye - light_look_at_eye);
				
				float spot_dot = dot(dir_eye, dirLightFragmentEye);
				
				float spot_factor = clamp((spot_dot - SPOT_ARC)/(1.0 - SPOT_ARC), 0.0, 1.0);
				if(spot_dot < SPOT_ARC) {
					spot_factor = 0.0;
				} 
				
				tId *= spot_factor; // zero if outside of spotlight
  				tIs *= spot_factor;
  				tIa *= spot_factor;
			}
		
			lightTotal += ((tId + tIs + tIa) / fAttTotal);
	    } 
	    
	}
	
	vec4 texel = texture(textureSampler, pass_texture);  
	out_Color = vec4(lightTotal, 1.0) + texel;
	
}