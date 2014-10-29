package renderer;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * This class takes care of the view matrix.
 *
 * @author Max 
 * @author Adi
 */
public class Camera {
	// View matrix the camera maintains.
	protected Matrix4f viewMatrix;
	protected Matrix4f rotMatrix;
	
	// Location of our camera.
	private Vector3f cameraPosition;

	// Euler angles to keep track of our orientation.
	private float yaw = -0.4f;
	private float pitch = 0.0f; 
	@SuppressWarnings("unused")
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
	 * Constructs a camera with a given position.
	 * @param pos The initial position of the camera.
	 */
	public Camera (Vector3f pos) {
		cameraPosition = pos;	
		viewMatrix = new Matrix4f();
		rotMatrix = new Matrix4f();
		applyTransformations();
	}
	
	/**
	 * Constructs a camera with a given position, facing a certain direction.
	 * @param pos The initial position of the camera.
	 * @param direction The initial direction the camera should face.
	 */
	public Camera (Vector3f pos, Vector3f direction){
		cameraPosition = pos;
		
		direction.normalise(direction);

		yaw = (float) Math.atan2((double) direction.x, (double) direction.z);
		pitch = (float) Math.asin(direction.y);

		viewMatrix = new Matrix4f();
		rotMatrix = new Matrix4f();
		recalculateCameraVectors();
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
	public void setLocation (Vector3f cameraPosition) {
		this.cameraPosition = cameraPosition;
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
	 * Gets the right vector for the camera
	 * @return
	 */
	public Vector3f getRight() {
		return cameraRight;
	}
	
	/**
	 * Returns the view matrix the Camera controls.
	 * @return the view matrix
	 */
	public Matrix4f getViewMatrix(){
		return viewMatrix;
	}
	
	public Matrix4f getRotationMatrix(){
		return rotMatrix;
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
		pitch += deltaY * cameraSensitivity;
		yaw -= deltaX * cameraSensitivity; // why inverted???
		
		recalculateCameraVectors();
		applyTransformations();
	}
	
	/**
	 * Recalculates camera direction and right vectors.
	 */
	private void recalculateCameraVectors() {
		cameraDirection.x = (float)(Math.cos(pitch) * Math.sin(yaw));
		cameraDirection.y = (float)(Math.sin(pitch));
		cameraDirection.z = (float)(Math.cos(pitch) * Math.cos(yaw));
		
		cameraRight.x = (float)(Math.sin(yaw - 3.14f/2.0f));
		cameraRight.y = 0f;
		cameraRight.z = (float)(Math.cos(yaw - 3.14f/2.0f));
	}
	
	/**
	 * Applies matrix transformations based on our pitch, yaw, and roll.
	 * This first creates a new matrix (at origin), applies the rotations, 
	 * then applies the translation to return it back to its position.
	 */
	public void applyTransformations() {
		Matrix4f orientation = new Matrix4f();
		Matrix4f rotationMatrix = new Matrix4f();
		
		Vector3f right = new Vector3f(cameraRight);
		Vector3f forwards = new Vector3f(cameraDirection);
		
		right.x = cameraRight.x / cameraRight.length();
		right.y = cameraRight.y / cameraRight.length();
		right.z = cameraRight.z / cameraRight.length();
		
		forwards.x = -cameraDirection.x / cameraDirection.length(); // ????? why reversed
		forwards.y = -cameraDirection.y / cameraDirection.length();
		forwards.z = -cameraDirection.z / cameraDirection.length();
		
		Vector3f up = Vector3f.cross(forwards, right, null);
		Vector3f pos = new Vector3f(cameraPosition);
		
		// Column major matrix
		orientation.m00 = right.x; 		
		orientation.m10 = right.y; 		
		orientation.m20 = right.z; 		
		orientation.m30 = -Vector3f.dot(right, pos); 
		orientation.m01 = up.x;			
		orientation.m11 = up.y;			
		orientation.m21 = up.z; 	  		
		orientation.m31 = -Vector3f.dot(up,  pos);
		orientation.m02 = forwards.x; 	
		orientation.m12 = forwards.y;	
		orientation.m22 = forwards.z; 	
		orientation.m32 = -Vector3f.dot(forwards, pos);
		
		rotationMatrix.m00 = right.x; 		
		rotationMatrix.m10 = right.y; 		
		rotationMatrix.m20 = right.z; 		 
		rotationMatrix.m01 = up.x;			
		rotationMatrix.m11 = up.y;			
		rotationMatrix.m21 = up.z; 	  		
		rotationMatrix.m02 = forwards.x; 	
		rotationMatrix.m12 = forwards.y;	
		rotationMatrix.m22 = forwards.z; 	
		
		rotMatrix = rotationMatrix;
		viewMatrix = orientation; // translation built in 
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
		
		applyTransformations();
	}
}