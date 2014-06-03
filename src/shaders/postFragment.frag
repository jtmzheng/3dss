#version 330

// This will be a reference post processing fragment shader

// TODO: Replace with uniforms
const int SAMPLE = 16;
const float invSamples = 1.0f/ 16.0f;
const vec3 pSphere[SAMPLE] = vec3[](vec3(0.53812504, 0.18565957, -0.43192),vec3(0.13790712, 0.24864247, 0.44301823),vec3(0.33715037, 0.56794053, -0.005789503),vec3(-0.6999805, -0.04511441, -0.0019965635),vec3(0.06896307, -0.15983082, -0.85477847),vec3(0.056099437, 0.006954967, -0.1843352),vec3(-0.014653638, 0.14027752, 0.0762037),vec3(0.010019933, -0.1924225, -0.034443386),vec3(-0.35775623, -0.5301969, -0.43581226),vec3(-0.3169221, 0.106360726, 0.015860917),vec3(0.010350345, -0.58698344, 0.0046293875),vec3(-0.08972908, -0.49408212, 0.3287904),vec3(0.7119986, -0.0154690035, -0.09183723),vec3(-0.053382345, 0.059675813, -0.5411899),vec3(0.035267662, -0.063188605, 0.54602677),vec3(-0.47761092, 0.2847911, -0.0271716));

const float gOcclusionRadius = 0.5f;
const float gOcclusionFadeStart = 0.2f;
const float gOcclusionFadeEnd = 2.0f;
const float gSurfaceEpsilon = 0.05f;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform float near_plane; // Default is 0.1
uniform float far_plane; // Default is 100

// post process texture samplers
uniform sampler2D fbTex;
uniform sampler2D depthBuffTex;
uniform sampler2D normalTex;
uniform sampler2D noiseTex;

// ssao properties
uniform float totStrength = 1.38;
uniform float strength = 0.7;
uniform float offset = 1.0; // noise offset (ratio of fb texture to noise texture)
uniform float falloff = 0.00002;
uniform float rad = 0.06;

// texture coordinates from vertex shaders
in vec2 st;
in vec3 vToFarPlane;

// output fragment colour RGBA
layout(location = 0) out vec4 frag_colour;

float linearizeDepth(in float depth, in float n, in float f);
vec3 decodeNormal(in vec3 normal);

float getOcclusion(float distZ) {
	float occlusion = 0.0f;
	if(distZ > gSurfaceEpsilon) {
		float fadeLength = gOcclusionFadeEnd - gOcclusionFadeStart;
		occlusion = clamp((gOcclusionFadeEnd - distZ) / fadeLength, 0.0f, 1.0f);
	}
	
	return occlusion;
}

void main(void) {
	float depth = texture(depthBuffTex, st).x;
	float linDepth = linearizeDepth(depth, near_plane, far_plane);
	
	vec3 norm = decodeNormal(texture(normalTex, st).xyz);
	vec3 pos = linDepth / vToFarPlane.z * vToFarPlane;
	
	vec3 randVec = texture(noiseTex, st).xyz;
	
	float occlusionSum = 0.0f;
	for(int i = 0; i < SAMPLE; ++i) {
		vec3 offs = reflect(pSphere[i], randVec);
		float flip = sign(dot(offs, norm));
		
		// Sample point
		vec3 q = pos + flip * gOcclusionRadius * offs;
		
		vec4 projQ = projectionMatrix * vec4(q, 1.0f);
		projQ /= projQ.w;
		
		float rz = texture(depthBuffTex, projQ.xy).x;
		vec3 r = (rz / q.z) * q;
		
		float distZ = pos.z - r.z;
		float dp = max(dot(norm, normalize(r - pos)), 0.0f);
		float occlusion = dp * getOcclusion(-distZ); //@TODO(MZ): Find src of error

		occlusionSum += occlusion;
	}
	
	occlusionSum /= SAMPLE;
	float access = 1.0f - occlusionSum;
	
	// invert colour of right-hand side
	vec3 colour;
	
	if(st.t >= 0.5) {
		colour = clamp(pow(access, 4.0f), 0.0f, 1.0f) * texture(fbTex, st).rgb; 
	} else {
		colour = texture(fbTex, st).rgb;
	}

	frag_colour = vec4(colour, 1.0f);
}

/*
void main (void) {
	float depth = texture(depthBuffTex, st).x;
	float linDepth = linearizeDepth(depth, near_plane, far_plane);

	vec3 pos = (linDepth / vToFarPlane.z) * vToFarPlane;
	vec3 norm = decodeNormal(texture(normalTex, st).rgb);
	norm = normalize(norm);
	
	vec3 fres = normalize(decodeNormal(texture(noiseTex, st * offset).xyz));
	
    // adjust for the depth (?)
    float radD = rad / linDepth;
 
    vec3 ray, se, occNorm;
    float occluderDepth, depthDifference, normDiff;
	
	float occlusion = 0.0;
	for (int i = 0; i < SAMPLE; ++i) {
	  // get sample in view space
	  vec4 pSphereV = viewMatrix * vec4(pSphere[i], 1.0);
	  
	  // get a vector (randomized inside of a sphere with radius 1.0) from a texture and reflect it
      ray = radD * reflect(pSphereV.xyz, fres);
 
      // if the ray is outside the hemisphere then change direction
      se = pos + sign(dot(ray, norm)) * ray;
 
      // get the depth of the occluder fragment
      vec4 occluderFragment = texture(normalTex, se.xy);
 
      // get the normal of the occluder fragment
      occNorm = decodeNormal(occluderFragment.xyz);
 
      // if depthDifference is negative = occluder is behind current fragment
      depthDifference = linDepth - occluderFragment.a;
 
      // calculate the difference between the normals as a weight
      normDiff = (1.0 - dot(occNorm, norm));
      
      // the falloff equation, starts at falloff and is kind of 1/x^2 falling
      occlusion += step(falloff, depthDifference) * normDiff * (1.0 - smoothstep(falloff, strength, depthDifference));
	}
	
	occlusion = 1.0 - (totStrength * occlusion * invSamples);
	
	// invert colour of right-hand side
	vec3 colour;
	
	if(st.t >= 0.5) {
		colour = occlusion * texture(fbTex, st).rgb; 
	} else {
		colour = texture(fbTex, st).rgb;
	}

	frag_colour = vec4(colour, 1.0);
}
*/
