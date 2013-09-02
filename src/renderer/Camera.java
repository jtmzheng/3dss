package renderer;

import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

// TODO: Need some kind of rotation mechanism that works with user mouse movement.
public class Camera {
	
	/*
	 * Location of our camera in world space
	 */
	protected Vector3f location;
	
	/*
	 * "Up" vector for context
	 */
	protected Vector3f up;
	
	/*
	 * Direction the camera is looking at
	 */
	protected Vector3f direction;
	
	/*
	 * View and projection matrices
	 * I know I put this in... but do we need references to the actual matrices?
	 */
	protected Matrix4f modelMatrix;
	protected Matrix4f viewMatrix;
	protected Matrix4f projMatrix;
	
	/*
	 * Camera ID
	 */
	protected String ID;
	
	/*
	 * Default camera constructor.
	 * Sets location to origin, up to (0,1,0), direction to (1, 0, 0)
	 */
	public Camera () {
		location = new Vector3f (0,0,0);
		direction = new Vector3f (1, 0, 0);
		up = new Vector3f (0, 1, 0);
		this.lookAt (direction);
	}
	
	/*
	 * Sets a new camera to the specified (x,y,0) location.
	 * Sets direction to (1, 0, 0), up vector to (0, 1, 0)
	 */
	public Camera (float x, float y) {
		location = new Vector3f (x,y,0);
		direction = new Vector3f (1, 0, 0);
		up = new Vector3f (0, 1, 0);
		this.lookAt (direction);
	}
	
	/*
	 * Sets the new camera to the specified (x,y,z) location with a given up & direction vector.
	 */
	public Camera (float x, float y, float z, Vector3f dir, Vector3f up) {
		location = new Vector3f (x, y, z);
		this.direction = dir;
		this.up = up;
		this.lookAt (direction);
	}
	
	/*
	 * Gets the location of the camera in world space
	 */
	public Vector3f getLocation () {
		return location;
	}
	
	/*
	 * Sets the location of the camera in world space
	 */
	public void setLocation (Vector3f loc) {
		this.location = loc;
	}
	
	/*
	 * Gets the direction where the camera is looking at 
	 */
	public Vector3f getDirection () {
		return direction;
	}
	
	/*
	 * Looks at the specified location in world space.
	 * Only rotates the camera, doesn't translate.
	 */
	public void lookAt (Vector3f loc) {
		this.direction = loc;
		GLU.gluLookAt(location.x, location.y, location.z, 
				loc.x, loc.y, loc.z, up.x, up.y, up.z);
	}
	
	/*
	 * Moves the camera in the specified (normalized) direction with a given speed.
	 */
	public void move (Vector3f normalizedDirection, float speed) {
		location.x += normalizedDirection.x * speed;
		location.y += normalizedDirection.y * speed;
		location.z += normalizedDirection.z * speed;
	}
	
	/*
	 * Translates the camera by a certain vector
	 */
	public void translate (Vector3f translation) {
		location.x += translation.x;
		location.y += translation.y;
		location.z += translation.z;
	}
}
