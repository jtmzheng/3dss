package renderer.shader;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;

import system.Settings;

/**
 * This is the default shader program used for rendering
 * @author Max
 *
 */
public class DefaultShaderProgram extends ShaderProgram {

	public DefaultShaderProgram(Map<String, Integer> shaders) {
		super(shaders);
	}
	
	protected void setupAttributes() {
		shaderAttributes = new HashMap<>();
		shaderAttributes.put("in_Position", Settings.getInteger("in_Position"));
		shaderAttributes.put("in_Color", Settings.getInteger("in_Color"));
		shaderAttributes.put("in_TextureCoord", Settings.getInteger("in_TextureCoord"));
		shaderAttributes.put("in_Normal", Settings.getInteger("in_Normal"));
		shaderAttributes.put("Ks", Settings.getInteger("Ks"));
		shaderAttributes.put("Ka", Settings.getInteger("Ka"));
		shaderAttributes.put("specExp", Settings.getInteger("specExp"));
		shaderAttributes.put("texture", Settings.getInteger("texture"));		
	}
	
	protected void setupUniformLocations() {
		shaderUniformLocations = new HashMap<>();
		// Get matrices uniform locations
		shaderUniformLocations.put("projectionMatrix", GL20.glGetUniformLocation(getProgram(), "projectionMatrix"));
		shaderUniformLocations.put("modelMatrix", GL20.glGetUniformLocation(getProgram(), "modelMatrix"));
		shaderUniformLocations.put("viewMatrix", GL20.glGetUniformLocation(getProgram(), "viewMatrix"));
		shaderUniformLocations.put("viewMatrixFrag", GL20.glGetUniformLocation(getProgram(), "viewMatrixFrag"));

		// Light uniform location
		shaderUniformLocations.put("La", GL20.glGetUniformLocation(getProgram(), "La"));

		// Texture uniform locations
		shaderUniformLocations.put("textureSampler", GL20.glGetUniformLocation(getProgram(), "textureSampler"));
		shaderUniformLocations.put("textureSamplers[0]", GL20.glGetUniformLocation(getProgram(), "textureSamplers[0]"));
		shaderUniformLocations.put("textureSamplers[1]", GL20.glGetUniformLocation(getProgram(), "textureSamplers[1]"));
		shaderUniformLocations.put("textureSamplers[2]", GL20.glGetUniformLocation(getProgram(), "textureSamplers[2]"));

		// Fog uniform locations
		shaderUniformLocations.put("fogOn", GL20.glGetUniformLocation(getProgram(), "fogOn"));
		shaderUniformLocations.put("fogColor", GL20.glGetUniformLocation(getProgram(), "fogColor"));
		shaderUniformLocations.put("fogMinDistance", GL20.glGetUniformLocation(getProgram(), "fogMinDistance"));
		shaderUniformLocations.put("fogMaxDistance", GL20.glGetUniformLocation(getProgram(), "fogMaxDistance"));
		
		// Color picking locations
		shaderUniformLocations.put("selectedModel", GL20.glGetUniformLocation(getProgram(), "selectedModel"));
	}
}
