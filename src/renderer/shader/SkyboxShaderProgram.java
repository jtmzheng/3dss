package renderer.shader;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;

import renderer.shader.types.ShaderTypes;

public class SkyboxShaderProgram extends ShaderProgram {

	public SkyboxShaderProgram(Map<String, Integer> shaders) {
		super(shaders);
	}

	@Override
	protected void setupAttributes() {
		shaderAttributes = new HashMap<>();
		shaderAttributes.put("vp", 0);
	}

	@Override
	protected void setupUniformLocations() {
		shaderUniformLocations = new HashMap<>();

		System.out.println(programId);
		shaderUniformLocations.put("viewMatrix", GL20.glGetUniformLocation(getProgram(), "viewMatrix"));
		shaderUniformLocations.put("projectionMatrix", GL20.glGetUniformLocation(getProgram(), "projectionMatrix"));
		shaderUniformLocations.put("cubeTexture", GL20.glGetUniformLocation(getProgram(), "cubeTexture"));
		System.out.println(shaderUniformLocations.values());
	}

	@Override
	public ShaderTypes getShaderType() {
		return ShaderTypes.SKYBOX_SHADER;
	}
}
