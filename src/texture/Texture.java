package texture;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDeleteTextures;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import renderer.ShaderController;

public class Texture {
	// Unique texture ID assigned by OpenGL.
	private int texId;

	// Pixel dimensions.
	private int width, height;

	// Whether bound or not 
	private boolean isBound;

	// Image data
	private ByteBuffer buffer;
	
	// The name of the texture image 
	private String name;

	/**
	 * Constructs a texture given a width, height, and unique ID assigned by OpenGL.
	 * @param width
	 * @param height
	 * @param id
	 * @param unitId
	 */
	public Texture (int width, int height, ByteBuffer buffer, String name) {
		this.width = width;
		this.height = height;
		this.isBound = false;
		this.buffer = buffer;
		this.name = name;
	}

	/**
	 * Gets the pixel width of the texture.
	 * @return width
	 */
	public int getWidth () {
		return width;
	}

	/**
	 * Gets the pixel height of the texture.
	 * @return height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the unique ID of the texture assigned by OpenGL.
	 * @return GLuid
	 */
	public int getID() {
		return texId;
	}
	
	/**
	 * Gets the name of the texture 
	 * @return String name of the texture
	 */
	public String getName() {
		return name;
	}
	

	/**
	 * Returns whether the texture is bound
	 * @return isBound - boolean value of whether the texture is bound
	 */
	public boolean isBound() {
		return isBound;
	}
	

	/**
	 * Binds this texture to the GL_TEXTURE_2D target.
	 * @param unitId - an OpenGL texture unit index
	 */
	public void bind (int unitId) {        
		// If not already bound and valid unit Id
		if(!isBound && unitId > 0) {
			texId = GL11.glGenTextures();
			
			// Set uniform variable of texture slot
			GL20.glUseProgram(ShaderController.getCurrentProgram());
			GL20.glUniform1i(ShaderController.getTexSamplerLocation(), unitId - GL13.GL_TEXTURE0);
			GL20.glUseProgram(0);
			
			GL13.glActiveTexture(unitId); 

			GL11.glBindTexture(GL_TEXTURE_2D, texId);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

			GL11.glTexImage2D (
					GL_TEXTURE_2D,
					0,
					GL11.GL_RGBA,
					width,
					height,
					0,
					GL11.GL_RGBA,
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
		}

	}

	/**
	 *  Unbinds this texture. It's up to the driver's implementation to free the actual memory.
	 */
	public void unbindAndDestroy () {
		glDeleteTextures(texId);
		texId = -1;
		isBound = false;
	}

}