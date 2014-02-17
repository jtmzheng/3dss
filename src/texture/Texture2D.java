package texture;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import renderer.shader.ShaderController;

public class Texture2D extends Texture {

	public Texture2D(int width, int height, ByteBuffer buffer, String name, boolean alpha) {
		super(width, height, name, alpha);
		this.buffer = buffer;
	}

	public void bind(int unitId) {
		// If not already bound and valid unit Id
		if(!isBound && unitId > 0) {		    
			texId = GL11.glGenTextures();

			// Activate the texture unit
			GL13.glActiveTexture(unitId);
			// Set uniform variable of texture slot
			GL20.glUseProgram(ShaderController.getCurrentProgram());
			GL20.glUniform1i(ShaderController.getTexSamplerLocation(), unitId - GL13.GL_TEXTURE0);
			GL20.glUseProgram(0);
			GL11.glBindTexture(GL_TEXTURE_2D, texId);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

			GL11.glTexImage2D (
					GL_TEXTURE_2D,
					0,
					colorFormat,
					width,
					height,
					0,
					colorFormat,
					GL11.GL_UNSIGNED_BYTE,
					buffer
					);

			// Set texture parameters
			GL11.glTexParameteri (GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri (GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri (GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri (GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

			isBound = true;
			GL11.glBindTexture(GL_TEXTURE_2D, 0);
		}
	}
	
	
	// Image data
	private ByteBuffer buffer;
	
}
