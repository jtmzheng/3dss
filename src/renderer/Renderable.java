package renderer;

import org.lwjgl.util.vector.Matrix4f;

// @TODO: Use this interface for anything that can be rendered
public interface Renderable {

	public void setup();
	public void render(Matrix4f viewMatrix);
	public boolean bind(); 
	
	// @TODO: Possible extensions
	// public void setUniforms();
	
}
