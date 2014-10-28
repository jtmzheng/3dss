package renderer.shader;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;


/**
 * This shader program is used for text rendering.
 * @author Adi
 *
 */
public class TextShaderProgram extends ShaderProgram {

	public TextShaderProgram(Map<String, Integer> shaders) {
		super(shaders);
	}
	
	protected void setupAttributes() {
		shaderAttributes = new HashMap<>();
		shaderAttributes.put("in_Position", 0);
		shaderAttributes.put("in_UV", 1);
	}

	protected void setupUniformLocations() {
		shaderUniformLocations = new HashMap<>();
		shaderUniformLocations.put("textureSampler", GL20.glGetUniformLocation(getProgram(), "textureSampler"));
	}
}
