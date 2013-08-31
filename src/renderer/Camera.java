package renderer;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	/*
	 * Location of our camera in world space
	 */
	protected Vector3f location;
	
	/*
	 * View and projection matrices
	 */
	protected Matrix4f projMatrix;
	protected Matrix4f viewMatrix;
	
	/*
	 * Camera ID
	 */
	protected String ID;
}
