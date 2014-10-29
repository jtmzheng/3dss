package renderer.shader.types;

/**
 * Supported uniforms for shaders
 * @author Max
 *
 */
public enum Uniforms {
	// Matrices
	PROJECTION_MATRIX,
	VIEW_MATRIX,
	MODEL_MATRIX,
	MODEL_VIEW_MATRIX,
	MODEL_VIEW_PROJECTION_MATRIX,
	NORMAL_MATRIX,
	
	// Samplers
	DIFFUSE_SAMPLER,
	
	// Fog
	FOG_ON,
	FOG_COLOUR,
	FOG_MIN_DISTANCE,
	FOG_MAX_DISTANCE,
	
	// Dynamics Lights (will follow strict template for now)
	LIGHTS_COUNT,
	
	// Selection (picking)
	SELECTED_MODEL
}
