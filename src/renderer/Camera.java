package renderer;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/*
 * Camera class takes care of the view Matrix
 * @author Max 
 */
public class Camera {
	
	protected Vector3f location;
	protected Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
	protected Vector3f direction;

	protected Matrix4f viewMatrix;
	
	protected String ID;
	
	private Vector3f cameraPosition = null;
	private float yaw = 3.14f;
	private float pitch = 0.0f; 
	private float roll = 0.0f;
	
	private Vector3f cameraDirection = new Vector3f(
			(float)(Math.cos(pitch) * Math.sin(yaw)),
			(float)(Math.sin(pitch)),
			(float)(Math.cos(pitch) * Math.cos(yaw))); 
	private Vector3f cameraRight = new Vector3f(
			(float)(Math.sin(yaw - 3.14f/2.0f)),
			(float)(0f),
			(float)(Math.cos(yaw - 3.14f/2.0f)));
	
	private float cameraSensitivity = 0.005f;
	
	/*
	 *  Constructor with a given position.
	 */
	public Camera (Vector3f pos) {
		cameraPosition = pos;
		viewMatrix = new Matrix4f();
		applyTransformations();
	}
	
	
	/*
	 * Gets the location of the camera in world space.
	 */
	public Vector3f getLocation () {
		return cameraPosition;
	}
	
	/*
	 * Sets the location of the camera in world space.
	 */
	public void setLocation (Vector3f loc) {
		this.cameraPosition = loc;
		applyTransformations();
	}
	
	/*
	 * Gets the direction where the camera is looking at.
	 */
	public Vector3f getDirection () {
		return cameraDirection;
	}
	
	public Matrix4f getViewMatrix(){
		if(viewMatrix == null){
			return null;
		}
		
		return viewMatrix;
	}
	
	public void strafe (float speed) {
		applyTranslation(new Vector3f(cameraRight.x * speed,
				cameraRight.y * speed,
				cameraRight.z * speed));
	}
	
	public void moveFrontBack (float speed) {
		applyTranslation(new Vector3f(cameraDirection.x * speed,
				cameraDirection.y * speed,
				cameraDirection.z * speed));
	}

	public void moveBackwards(float speed){
		applyTranslation(new Vector3f(-cameraDirection.x * speed,
				-cameraDirection.y * speed,
				-cameraDirection.z * speed));
	}
	
	public void rotateCamera(int deltaX, int deltaY){
		pitch -= deltaY * cameraSensitivity;
		yaw -= deltaX * cameraSensitivity;

		cameraDirection.x = -(float)(Math.cos(pitch) * Math.sin(yaw));
		cameraDirection.y = (float)(Math.sin(pitch));
		cameraDirection.z = (float)(Math.cos(pitch) * Math.cos(yaw));
		
		cameraRight.x = (float)(Math.sin(yaw - 3.14f/2.0f));
		cameraRight.z = -(float)(Math.cos(yaw - 3.14f/2.0f));
				
		applyTransformations();
	}
	
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
