package renderer;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Camera class takes care of the view matrix.
 * @author Max 
 * @author Adi
 */
public class Camera {
	
	// View matrix the camera maintains.
	protected Matrix4f viewMatrix;
		
	// Location of our camera.
	private Vector3f cameraPosition = null;
	
	// Euler angles to keep track of our orientation.
	private float yaw = 3.14f;
	private float pitch = 0.0f; 
	private float roll = 0.0f;
	
	// Direction vector of our camera.
	private Vector3f cameraDirection = new Vector3f(
			(float)(Math.cos(pitch) * Math.sin(yaw)),
			(float)(Math.sin(pitch)),
			(float)(Math.cos(pitch) * Math.cos(yaw))); 
	
	// Right vector of our camera.
	private Vector3f cameraRight = new Vector3f(
			(float)(Math.sin(yaw - 3.14f/2.0f)),
			(float)(0f),
			(float)(Math.cos(yaw - 3.14f/2.0f)));
	
	// Sensitivity of our camera to mouse movements.
	private float cameraSensitivity = 0.005f;
	
	/**
	 *  Constructor with a given position.
	 *  @param pos The initial position of the camera.
	 */
	public Camera (Vector3f pos) {
		cameraPosition = pos;
		viewMatrix = new Matrix4f();
		applyTransformations();
	}
	
	
	/**
	 * Gets the location of the camera in world space.
	 * @return the location of the camera in world space.
	 */
	public Vector3f getLocation () {
		return cameraPosition;
	}
	
	/**
	 * Sets the location of the camera in world space.
	 */
	public void setLocation (Vector3f loc) {
		this.cameraPosition = loc;
		applyTransformations();
	}
	
	/**
	 * Gets the direction where the camera is looking at.
	 * @return the direction vector of the camera
	 */
	public Vector3f getDirection () {
		return cameraDirection;
	}
	
	/**
	 * Returns the view matrix the Camera controls.
	 * @return the view matrix
	 */
	public Matrix4f getViewMatrix(){
		if(viewMatrix == null){
			return null;
		}
		
		return viewMatrix;
	}
	
	/**
	 * Strafes the camera (left and right movement).
	 * Negative speed for left, positive speed for right.
	 * @param speed Speed to move the camera at.
	 */
	public void strafe (float speed) {
		applyTranslation(new Vector3f(cameraRight.x * speed,
				cameraRight.y * speed,
				cameraRight.z * speed));
	}
	
	/**
	 * Moves the camera forwards and backwards.
	 * Negative speed for back, positive speed for forward.
	 * @param speed Speed to move the camera at.
	 */
	public void moveFrontBack (float speed) {
		applyTranslation(new Vector3f(cameraDirection.x * speed,
				cameraDirection.y * speed,
				cameraDirection.z * speed));
	}

	/**
	 * Rotates the camera given a change in mouse position.
	 * @param deltaX The change in x.
	 * @param deltaY The change in y.
	 */
	public void rotateCamera(int deltaX, int deltaY){
		pitch -= deltaY * cameraSensitivity;
		yaw += deltaX * cameraSensitivity;

		cameraDirection.x = -(float)(Math.cos(pitch) * Math.sin(yaw));
		cameraDirection.y = (float)(Math.sin(pitch));
		cameraDirection.z = (float)(Math.cos(pitch) * Math.cos(yaw));
		
		cameraRight.x = (float)(Math.sin(yaw - 3.14f/2.0f));
		cameraRight.z = -(float)(Math.cos(yaw - 3.14f/2.0f));
				
		applyTransformations();
	}
	
	/**
	 * Applies matrix transformations based on our pitch, yaw, and roll.
	 * This first creates a new matrix (at origin), applies the rotations, 
	 * then applies the translation to return it back to its position.
	 */
	public void applyTransformations() {
		viewMatrix = new Matrix4f();
	    Matrix4f.rotate(pitch, new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
	    Matrix4f.rotate(yaw, new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
	    Matrix4f.rotate(roll, new Vector3f(0, 0, 1), viewMatrix, viewMatrix);
	    
	    Matrix4f.translate(new Vector3f(cameraPosition.x,
	    		cameraPosition.y,
	    		cameraPosition.z),
	    		viewMatrix,
	    		viewMatrix);
	}
	
	/**
	 * Translates the camera position by a given vector.
	 * This also handles the view matrix logic associated with the translation.
	 * @param translationVector The vector to translate cameraPosition by.
	 */
	public void applyTranslation(Vector3f translationVector) {
		cameraPosition = Vector3f.add(cameraPosition,
				new Vector3f(translationVector.x,
						translationVector.y,
						translationVector.z),
						null);
		
		Matrix4f.translate(new Vector3f(translationVector.x,
				translationVector.y,
				translationVector.z),
				viewMatrix,
				viewMatrix);
	}
}
