package texture;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public class Texture {
	// Unique texture ID assigned by OpenGL.
	private int GLuid;
	
	// Texture file name, parsed from the .mtl file.
	private String fileName;
	
	// Pixel dimensions.
	private int width, height;
	
	public Texture (String name, int width, int height, int id) {
		this.fileName = name;
		this.width = width;
		this.height = height;
		this.GLuid = id;
	}
	
	public int getWidth () {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getID() {
		return GLuid;
	}
	
	public String getName() {
		return fileName;
	}
	
	// Binds this texture to the target.
	public void bind () {
        glBindTexture(GL_TEXTURE_2D, this.GLuid); // Bind texture ID.
	}
}