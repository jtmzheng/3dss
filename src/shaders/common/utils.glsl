#version 330

/*----------------------------------------------------------------------------*/
float linearizeDepth(float depth, float n, float f) {
	return (2.0 * n) / (f + n - depth * (f - n));
}

