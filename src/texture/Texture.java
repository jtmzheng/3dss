package texture;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

public class Texture {
	// Unique texture ID assigned by OpenGL.
	private int texId;
	
	// Texture unit ID 
	private int unitId;
	
	// Pixel dimensions.
	private int width, height;

	// Whether bound or not 
	private boolean isBound;
	
	// Image data
	private ByteBuffer buffer;
	
	/**
	 * Constructs a texture given a width, height, and unique ID assigned by OpenGL.
	 * @param width
	 * @param height
	 * @param id
	 * @param unitId
	 */
	public Texture (int width, int height, int id, int unitId, ByteBuffer buffer) {
		this.width = width;
		this.height = height;
		this.texId = id;
		this.unitId = unitId;
		this.isBound = false;
		this.buffer = buffer;
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
	 * Binds this texture to the GL_TEXTURE_2D target.
	 */
	public void bind () {
        glBindTexture(GL_TEXTURE_2D, texId); // Bind texture ID. 
        
        texId = GL11.glGenTextures();
        GL13.glActiveTexture(unitId);
        GL11.glBindTexture(GL_TEXTURE_2D, texId);
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
        
        isBound = true;
        
        // Set texture parameters
        GL11.glTexParameteri (GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri (GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri (GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri (GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	}
	
	/**
	 * Returns whether the texture is bound
	 * @return
	 */
	public boolean isBound() {
		return isBound;
	}
	
	/**
	 *  Unbinds this texture. It's up to the driver's implementation to free the actual memory.
	 */
	public void unbindAndDestroy () {
		glDeleteTextures(texId);
		isBound = false;
	}
}