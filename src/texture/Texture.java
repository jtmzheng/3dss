package texture;

import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class Texture {
	// Unique texture ID assigned by OpenGL.
	private int GLuid;
	
	// Pixel dimensions.
	private int width, height;
	
	/**
	 * Constructs a texture given a width, height, and unique ID assigned by OpenGL.
	 * @param width
	 * @param height
	 * @param id
	 */
	public Texture (int width, int height, int id) {
		this.width = width;
		this.height = height;
		this.GLuid = id;
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
		return GLuid;
	}
	
	/**
	 * Binds this texture to the GL_TEXTURE_2D target.
	 */
	public void bind () {
        glBindTexture(GL_TEXTURE_2D, this.GLuid); // Bind texture ID.
	}
	
	/**
	 *  Unbinds this texture. It's up to the driver's implementation to free the actual memory.
	 */
	public void unbindAndDestroy () {
		glDeleteTextures(this.GLuid);
	}
}