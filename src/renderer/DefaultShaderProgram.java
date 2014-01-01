package renderer;

import java.util.HashMap;
import java.util.Map;

import system.Settings;

public class DefaultShaderProgram extends ShaderProgram{

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
}
