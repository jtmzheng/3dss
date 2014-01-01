package renderer;

import java.util.HashMap;
import java.util.Map;

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

}
