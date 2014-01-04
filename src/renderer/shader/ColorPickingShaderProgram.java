package renderer.shader;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;


/**
 * 
 * @author Max
 *
 */
public class ColorPickingShaderProgram extends ShaderProgram {

	public ColorPickingShaderProgram(Map<String, Integer> shaders) {
		super(shaders);
	}
	
	protected void setupAttributes() {
		shaderAttributes = new HashMap<>();
		shaderAttributes.put("in_Position", 0);
	}
	
	protected void setupUniformLocations() {
		shaderUniformLocations = new HashMap<>();
		shaderUniformLocations.put("projectionMatrix", GL20.glGetUniformLocation(getProgram(), "projectionMatrix"));
		shaderUniformLocations.put("modelMatrix", GL20.glGetUniformLocation(getProgram(), "modelMatrix"));
		shaderUniformLocations.put("viewMatrix", GL20.glGetUniformLocation(getProgram(), "viewMatrix"));
		shaderUniformLocations.put("uniqueId", GL20.glGetUniformLocation(getProgram(), "uniqueId"));
	}

}
