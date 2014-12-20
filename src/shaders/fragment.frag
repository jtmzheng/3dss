#version 330

//@TODO: This needs cleanup and consistent style

const float SPOT_ARC = cos(13.66 / 90.0);
const int MAX_NUM_LIGHTS = 30;

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

uniform vec3 La = vec3(0.2, 0.2, 0.2); // grey ambient
uniform mat4 viewMatrix;

// Fog variables
uniform vec3 fogColor = vec3(0.2, 0.2, 0.2); // grey
uniform float fogMinDistance = 2.0;
uniform float fogMaxDistance = 10.0;
uniform int fogOn = 0;

// Texturing
uniform sampler2D textureSampler;
// uniform sampler2D textures [3];

uniform lightSrc lights[MAX_NUM_LIGHTS];
uniform int selectedModel = 0;

in vec3 sKs, sKd, sKa;
in vec2 pass_texture;
in vec3 posEye, normEye;
in vec3 liAmbSphHarm;

layout(location = 0) out vec4 out_Color;
layout(location = 1) out vec4 out_Normal;

vec3 encodeNormal(in vec3 raw);

// Calculate the fog factor
float getFogFactor(float dist) {
	float fogFactor = (dist - fogMinDistance) / (fogMaxDistance - fogMinDistance);
	return clamp(fogFactor, 0.0, 1.0);
} 

void main(void) {
	vec3 Ia = La * sKa / 2;
	vec3 Id = vec3(0, 0, 0), Is = vec3(0, 0, 0);
	vec3 liAmbDiff = Ia; // light component for ambient and diffuse lighting
	vec3 liSpec = vec3(0, 0, 0);

	for(int index = 0; index < MAX_NUM_LIGHTS; index++) {			
	    if(lights[index].isUsed > 0.5) {    
	    	vec3 tId = vec3(0, 0, 0), tIs = vec3(0, 0, 0); // diffuse and specular component of this light
	    	
			vec3 posLiEye = vec3(viewMatrix * vec4(lights[index].position, 1.0));
			vec3 dposLiEye = posLiEye - posEye;
			vec3 ndposLiEye = normalize(dposLiEye); // direction from light to surface 
	    
	    	float dotLightEye = dot(ndposLiEye, normalize(normEye));
			dotLightEye = max(dotLightEye, 0.0); //clamp to 0
	    
	    	vec3 reflectionEye = reflect(-ndposLiEye, normEye);
			vec3 surfaceViewerEye = normalize(-posEye);
	    
	    	float dotSpecular = abs(dot(normalize(reflectionEye), surfaceViewerEye));
			dotSpecular = min(dotSpecular, 1.0);
	
			float specFactor = pow(dotSpecular, lights[index].specExp);
	
			// Attenuation of light over distance
   			float fDist = length(dposLiEye); // distance between light and position of fragment
   			float constAtt = lights[index].attenuation[0];
   			float linearAtt = lights[index].attenuation[1];
   			float quadAtt = lights[index].attenuation[2];
   			    
   		    float fAttTotal = 1.0; // total attenuation    
   			fAttTotal = constAtt + linearAtt * fDist + quadAtt * fDist * fDist;
   			
			// Get light component due to current light
			tId = lights[index].Ld * sKd * dotLightEye;	
			tIs = lights[index].Ls * sKs * specFactor;	
			
			// If directional lighting is enabled
			if(lights[index].isDirectional > 0.5){
				vec3 posLiLookAt = lights[index].position + lights[index].direction;
				vec3 posLiLookAtEye = vec3(viewMatrix * vec4(posLiLookAt, 1.0));
				vec3 ndposLiSpotEye = normalize(posLiEye - posLiLookAtEye);
				
				float spot_dot = dot(ndposLiSpotEye, ndposLiEye);
				
				float spot_factor = clamp((spot_dot - SPOT_ARC)/(1.0 - SPOT_ARC), 0.0, 1.0);
				if(spot_dot < SPOT_ARC) {
					spot_factor = 0.0;
				} 
				
				tId *= spot_factor; // zero if outside of spotlight
  				tIs *= spot_factor;
			}
		
			liAmbDiff += (tId / fAttTotal);
			liSpec += (tIs / fAttTotal);
	    } 
	    
	}
	
	if(selectedModel == 0) {
		// Get the textured color
		vec4 texel = texture(textureSampler, pass_texture);
		
		// Mix the color with the fog color if fog is enabled
		if(fogOn == 1) {  
			float fogFactor = getFogFactor(length(posEye));
			out_Color = mix(vec4(liAmbDiff + liAmbSphHarm, 1.0) * texel + vec4(liSpec, 0.0), vec4(fogColor, 1.0), fogFactor); 
		} else {
			// Use modulus with late add and spherical harmonic lighting ambient term
			out_Color = vec4(liAmbDiff + liAmbSphHarm, 1.0) * texel + vec4(liSpec, 0.0);
		}
	} else { 
		// Color red if selected (picked)
		out_Color = vec4(1.0, 0.0, 0.0, 1.0);
	}
	
	// Output the normal to texture
	out_Normal = vec4(encodeNormal(normEye), 0.0f);
		
}