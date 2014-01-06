package texture;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDeleteTextures;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import renderer.shader.ShaderController;

/**
 * Basic texture class used by the Renderer.
 *
 * @author Adi
 * @author Max
 */
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
	
	// Whether the texture has alpha
	private boolean hasAlpha;
	
	// RGB or RGBA
	private int colorFormat;

	/**
	 * Constructs a texture
	 * @param width the width of the texture
	 * @param height the height of the texture 
	 * @param buffer the ByteBuffer with the image data
	 * @param name the name associated with the texture
	 * @param alpha boolean for whether the texture has alpha
	 */
	public Texture (int width, int height, ByteBuffer buffer, String name, boolean alpha) {
		this.width = width;
		this.height = height;
		this.isBound = false;
		this.buffer = buffer;
		this.name = name;
		this.hasAlpha = alpha;
		this.colorFormat = this.hasAlpha ? GL11.GL_RGBA : GL11.GL_RGB;
		System.out.println("HasAlpha?" + this.hasAlpha + " , " + colorFormat);
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
	 * Returns whether the texture supports alpha (transparency)
	 * @return hasAlpha - boolean value of whether the texture has alpha
	 */
	public boolean hasAlpha() {
		return hasAlpha;
	}
	
	/**
	 * Returns an integer representing the color format (RGB or RGBA
	 * @return colorFormat - integer value representing the color format
	 */
	public int getColorFormat() {
		return colorFormat;
	}

	/**
	 * Binds this texture to the GL_TEXTURE_2D target.
	 * @param unitId - an OpenGL texture unit index
	 */
	public void bind (int unitId) {        
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

	/**
	 *  Unbinds this texture. It's up to the driver's implementation to free the actual memory.
	 */
	public void unbindAndDestroy () {
		glDeleteTextures(texId);
		texId = -1;
		isBound = false;
	}
}