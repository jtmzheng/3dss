package renderer;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;

/**
 * PixelShaderProgram 
 * @author Max
 *
 */
public class PixelShaderProgram extends ShaderProgram{

	public PixelShaderProgram(Map<String, Integer> shaders) {
		super(shaders);
	}
	
	protected void setupAttributes() {
		shaderAttributes = new HashMap<>();
		shaderAttributes.put("vp", 0);
		shaderAttributes.put("vt", 1);
	}
	
	protected void setupUniformLocations() {
		shaderUniformLocations = new HashMap<>();
		
		// Sampler for the FB texture
		shaderUniformLocations.put("fbTex", GL20.glGetUniformLocation(getProgram(), "fbTex"));
		shaderUniformLocations.put("projectionMatrix", GL20.glGetUniformLocation(getProgram(), "projectionMatrix"));
 	}

}
