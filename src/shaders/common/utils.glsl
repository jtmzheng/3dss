#version 330

/*----------------------------------------------------------------------------*/
float linearizeDepth(float depth, mat4 projMatrix) {
	return projMatrix[3][2] / (depth - projMatrix[2][2]);
}

