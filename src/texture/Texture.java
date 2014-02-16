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
public abstract class Texture {
	// Unique texture ID assigned by OpenGL.
	protected int texId;

	// Pixel dimensions.
	protected int width, height;

	// Whether bound or not 
	protected boolean isBound;

	// The name of the texture image 
	protected String name;
	
	// Whether the texture has alpha
	protected boolean hasAlpha;
	
	// RGB or RGBA
	protected int colorFormat;

	/**
	 * Constructs a texture
	 * @param width the width of the texture
	 * @param height the height of the texture 
	 * @param buffer the ByteBuffer with the image data
	 * @param name the name associated with the texture
	 * @param alpha boolean for whether the texture has alpha
	 */
	public Texture (int width, int height, String name, boolean alpha) {
		this.width = width;
		this.height = height;
		this.isBound = false;
		this.name = name;
		this.hasAlpha = alpha;
		this.colorFormat = this.hasAlpha ? GL11.GL_RGBA : GL11.GL_RGB;
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
	public abstract void bind (int unitId);

	/**
	 *  Unbinds this texture. It's up to the driver's implementation to free the actual memory.
	 */
	public void unbindAndDestroy () {
		glDeleteTextures(texId);
		texId = -1;
		isBound = false;
	}
}