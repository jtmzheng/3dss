#version 330

/*----------------------------------------------------------------------------*/
float linearizeDepth(in float depth, in float n, in float f) {
	return (2.0f * n) / (f + n - depth * (f - n));
}

/*----------------------------------------------------------------------------*/
vec3 encodeNormal(in vec3 raw) {
	return raw * 0.5f + 0.5f; // Maps normal to [0, 1]
}

/*----------------------------------------------------------------------------*/
vec3 decodeNormal(in vec3 tNormal) {
	return tNormal * 2.0f - 1.0f; // Maps back to [-1, 1]
}

/*----------------------------------------------------------------------------*/
float log10(in float x) {
	return log2(x) / log2(10);
}

/*----------------------------------------------------------------------------*/
vec2 powv2(in vec2 b, in float e) {
	return vec2(pow(b.x, e), pow(b.y, e));
}
/*----------------------------------------------------------------------------*/
vec3 powv3(in vec3 b, in float e) {
	return vec3(pow(b.x, e), pow(b.y, e), pow(b.z, e));
}
/*----------------------------------------------------------------------------*/
vec4 powv4(in vec4 b, in float e) {
	return vec4(pow(b.x, e), pow(b.y, e), pow(b.z, e), pow(b.w, e));
}

/*----------------------------------------------------------------------------*/
float luminance(in vec3 rgb) {
	const vec3 kLum = vec3(0.2126f, 0.7152f, 0.0722f);
	return max(dot(rgb, kLum), 0.0001f); // prevent zero result
}