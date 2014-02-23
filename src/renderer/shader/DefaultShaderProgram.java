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
		Settings settings = Settings.getInstance();

		shaderAttributes.put("in_Position", settings.get("attributes", "in_Position", int.class));
		shaderAttributes.put("in_Color", settings.get("attributes", "in_Color", int.class));
		shaderAttributes.put("in_TextureCoord", settings.get("attributes", "in_TextureCoord", int.class));
		shaderAttributes.put("in_Normal", settings.get("attributes", "in_Normal", int.class));
		shaderAttributes.put("Ks", settings.get("attributes", "Ks", int.class));
		shaderAttributes.put("Ka", settings.get("attributes", "Ka", int.class));
		shaderAttributes.put("specExp", settings.get("attributes", "specExp", int.class));
		shaderAttributes.put("texture", settings.get("attributes", "texture", int.class));	
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
