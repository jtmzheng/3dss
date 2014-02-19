package texture;

import java.nio.ByteBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import renderer.shader.ShaderController;


public class CubeTexture extends Texture {

	public CubeTexture(int width, int height, List<ByteBuffer> imgBuffers, String name, boolean alpha) {
		super(width, height, name, alpha);
		this.imgBuffers = imgBuffers;
	}

	@Override
	public void bind(int unitId) {
		// If not already bound and valid unit Id
		if(!isBound && unitId > 0) {			
			texId = GL11.glGenTextures();

			// Activate the texture unit
			GL13.glActiveTexture(unitId);
			
			// Set uniform variable of texture slot
			GL20.glUseProgram(ShaderController.getCurrentProgram());
			GL20.glUniform1i(ShaderController.getCubeTextureLocation(), unitId - GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texId);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

			int side = GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
			for(ByteBuffer buffer : imgBuffers) {
				GL11.glTexImage2D(side++, 0, colorFormat, width, height, 0, colorFormat, GL11.GL_UNSIGNED_BYTE, buffer);
			}

			// Set texture parameters
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

			isBound = true;
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
			GL20.glUseProgram(0);
		}

	}

	private List<ByteBuffer> imgBuffers;
	
}
