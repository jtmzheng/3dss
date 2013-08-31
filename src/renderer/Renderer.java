package renderer;

import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Matrix4f;

/**
 * Interface for our renderer.
 * In progress.
 * @author Adi
 */
public interface Renderer {
	
	/*
	 * Sets the background color.
	 */
	public void setBGColor (Color c);
	
	/*
	 * Clears the frame buffer (or parts of it like color, depth, etc).
	 */
	public void clearBuffers (/*which buffers to clear*/);
	
	/*
	 * Sets our view port, centered at x,y with a given width and height.
	 */
	public void setViewPort (int x, int y, int width, int height);
	
	/*
	 * Sets the view matrix to use.
	 */
	public void setViewMatrix (Matrix4f view);
	
	/*
	 * Sets the projection matrix to use.
	 */
	public void setProjectionMatrix (Matrix4f proj);
	
	/*
	 * Sets the lighting with a provided LightSet (relative to world space)
	 */
	public void setLighting (/*insert a LightSet class here*/);
	
	/*
	 * Called after a new frame has been rendered
	 */
	public void onFrame ();
}
