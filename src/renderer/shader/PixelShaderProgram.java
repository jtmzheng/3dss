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
		shaderAttributes.put("iFrustum", 2);
	}
	
	protected void setupUniformLocations() {
		shaderUniformLocations = new HashMap<>();
		
		shaderUniformLocations.put("gFrustumCorners", GL20.glGetUniformLocation(getProgram(), "gFrustumCorners"));
		shaderUniformLocations.put("viewMatrix", GL20.glGetUniformLocation(getProgram(), "viewMatrix"));
		shaderUniformLocations.put("nearPlane", GL20.glGetUniformLocation(getProgram(), "near_plane"));
		shaderUniformLocations.put("farPlane", GL20.glGetUniformLocation(getProgram(), "far_plane"));
		shaderUniformLocations.put("fbTex", GL20.glGetUniformLocation(getProgram(), "fbTex"));
		shaderUniformLocations.put("depthBuffTex", GL20.glGetUniformLocation(getProgram(), "depthBuffTex"));
		shaderUniformLocations.put("normalTex", GL20.glGetUniformLocation(getProgram(), "normalTex"));
		shaderUniformLocations.put("noiseTex", GL20.glGetUniformLocation(getProgram(), "noiseTex"));
		
		System.out.println("gFrustumCorners: " + shaderUniformLocations.get("gFrustumCorners"));
 	}

	@Override
	public ShaderTypes getShaderType() {
		return ShaderTypes.POST_PROCESS_SHADER;
	}

}
