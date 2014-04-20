package renderer.shader;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;

import renderer.shader.types.ShaderTypes;


/**
 * This is the shader program used to render a texture to screen 
 * @author Max
 *
 */
public class PixelShaderProgram extends ShaderProgram {

	public PixelShaderProgram(Map<String, Integer> shaders) {
		super(shaders, true);
	}
	
	protected void setupAttributes() {
		shaderAttributes = new HashMap<>();
		shaderAttributes.put("vp", 0);
		shaderAttributes.put("vt", 1);
	}
	
	protected void setupUniformLocations() {
		shaderUniformLocations = new HashMap<>();
		
		// Sampler for the FB texture
		shaderUniformLocations.put("nearPlane", GL20.glGetUniformLocation(getProgram(), "near_plane"));
		shaderUniformLocations.put("farPlane", GL20.glGetUniformLocation(getProgram(), "far_plane"));
		shaderUniformLocations.put("fbTex", GL20.glGetUniformLocation(getProgram(), "fbTex"));
		shaderUniformLocations.put("depthBuffTex", GL20.glGetUniformLocation(getProgram(), "depthBuffTex"));
		

 	}

	@Override
	public ShaderTypes getShaderType() {
		return ShaderTypes.POST_PROCESS_SHADER;
	}

}
