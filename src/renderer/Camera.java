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

	protected Matrix4f viewMatrix; //this is the only matrix associated with the camera
	
	protected String ID;
	
	//Camera variables (TODO: will be moved to a camera class in the future)
	private Vector3f cameraPosition = null;
	private float cameraHorizontalAngle = 3.14f; //arbitrarily defined right now
	private float cameraVerticalAngle = 0.0f; 
	private Vector3f cameraDirection = new Vector3f(
			(float)(Math.cos(cameraVerticalAngle) * Math.sin(cameraHorizontalAngle)),
			(float)(Math.sin(cameraVerticalAngle)),
			(float)(Math.cos(cameraVerticalAngle) * Math.cos(cameraHorizontalAngle))); 
	private Vector3f cameraRight = new Vector3f(
			(float)(Math.sin(cameraHorizontalAngle - 3.14f/2.0f)),
			(float)(0f),
			(float)(Math.cos(cameraHorizontalAngle - 3.14f/2.0f))); //NOTE: Vector3f has built in cross product
	
	private float cameraSensitivity = 0.005f; //Larger equals more sensitive
	
	/*
	 * Default camera constructor.
	 * Sets location to origin, up to (0,1,0), direction to (1, 0, 0)
	 */
	public Camera () {
		cameraPosition = new Vector3f ( 0 , 0 , 0 );
		up = new Vector3f ( 0 , 1 , 0 );
		viewMatrix = new Matrix4f();
		lookAt (cameraPosition, Vector3f.add(cameraPosition, cameraDirection, null), up);
	}
	
	
	/*
	 * Constructor with input (scalar position, vector direction, vector up)
	 */
	public Camera (float x, float y, float z, Vector3f dir, Vector3f up) {
		cameraPosition = new Vector3f ( x , y , z );
		cameraDirection = dir;
		this.up = up;
		viewMatrix = new Matrix4f();
		lookAt (cameraPosition, Vector3f.add(cameraPosition, cameraDirection, null), up);
	}
	
	/*
	 *  Constructor with input (vector position, vector direction, vector up)
	 */
	public Camera (Vector3f pos, Vector3f dir, Vector3f up) {
		cameraPosition = pos;
		cameraDirection = dir;
		this.up = up;
		viewMatrix = new Matrix4f();
		lookAt (cameraPosition, Vector3f.add(cameraPosition, cameraDirection, null), up);
	}
	
	/*
	 *  Constructor with input (vector position, default direction, default up)
	 */
	public Camera (Vector3f pos) {
		cameraPosition = pos;
		viewMatrix = new Matrix4f();
		lookAt (cameraPosition, Vector3f.add(cameraPosition, cameraDirection, null), up);
	}
	
	
	/*
	 * Gets the location of the camera in world space
	 */
	public Vector3f getLocation () {
		return cameraPosition;
	}
	
	/*
	 * Sets the location of the camera in world space
	 */
	public void setLocation (Vector3f loc) {
		this.cameraPosition = loc;
		lookAt (cameraPosition, Vector3f.add(cameraPosition, cameraDirection, null), up);
	}
	
	/*
	 * Gets the direction where the camera is looking at 
	 */
	public Vector3f getDirection () {
		return cameraDirection;
	}
	
	public Matrix4f getViewMatrix(){
		if(viewMatrix == null){
			return null;
		}
		
		System.out.println("POSITION: " + cameraPosition);
		System.out.println("DIRECTION: " + cameraDirection);
		return viewMatrix;
	}
	
	/*
	 * Moves the camera in the specified (normalized) direction with a given speed.
	 */
	public void move (Vector3f normalizedDirection, float speed) {
		cameraPosition.x += normalizedDirection.x * speed;
		cameraPosition.y += normalizedDirection.y * speed;
		cameraPosition.z += normalizedDirection.z * speed;
		
		lookAt(cameraPosition, Vector3f.add(cameraPosition, cameraDirection, null), up);
	}
	
	/*
	 * Translates the camera by a certain vector
	 */
	public void translate (Vector3f translation) {
		cameraPosition.x += translation.x;
		cameraPosition.y += translation.y;
		cameraPosition.z += translation.z;
		
		lookAt(cameraPosition, Vector3f.add(cameraPosition, cameraDirection, null), up);
	}
	
	
	//NOTE THESE BELOW METHODS ARE FOR TESTING. THEY WILL BE MOVED TO A PLAYER CLASS (EVENTUALLY)
	public void strafeLeft(float speed){
		cameraPosition = Vector3f.add(cameraPosition, 
				new Vector3f(-cameraRight.x * speed, 
						-cameraRight.y * speed, 
						-cameraRight.z * speed), 
						null);
		lookAt(cameraPosition, Vector3f.add(cameraPosition, cameraDirection, null), up);
	}
	
	public void strafeRight(float speed){
		cameraPosition = Vector3f.add(cameraPosition, 
				new Vector3f(cameraRight.x * speed, 
						cameraRight.y * speed, 
						cameraRight.z * speed), 
						null);
		lookAt(cameraPosition, Vector3f.add(cameraPosition, cameraDirection, null), up);
	}
	
	public void moveForwards(float speed){
		cameraPosition = Vector3f.add(cameraPosition, 
				new Vector3f(cameraDirection.x * speed, 
						cameraDirection.y * speed, 
						cameraDirection.z * speed), 
						null);
		lookAt(cameraPosition, Vector3f.add(cameraPosition, cameraDirection, null), up);
	}
	
	public void moveBackwards(float speed){
		cameraPosition = Vector3f.add(cameraPosition, 
				new Vector3f(-cameraDirection.x * speed, 
						-cameraDirection.y * speed, 
						-cameraDirection.z * speed), 
						null);
		lookAt(cameraPosition, Vector3f.add(cameraPosition, cameraDirection, null), up);
	}
	
	public void rotateCamera(int deltaX, int deltaY){
		cameraVerticalAngle -= deltaY * cameraSensitivity;
		cameraHorizontalAngle += deltaX * cameraSensitivity;
		
		cameraDirection.x = (float)(Math.cos(cameraVerticalAngle) * Math.sin(cameraHorizontalAngle));
		cameraDirection.y = (float)(Math.sin(cameraVerticalAngle));
		cameraDirection.z = (float)(Math.cos(cameraVerticalAngle) * Math.cos(cameraHorizontalAngle));
		
		cameraRight.x = (float)(Math.sin(cameraHorizontalAngle - 3.14f/2.0f));
		cameraRight.y = (float)(Math.sin(cameraVerticalAngle));
		cameraRight.z = (float)(Math.cos(cameraHorizontalAngle - 3.14f/2.0f));
		
		up = Vector3f.cross(cameraRight, cameraDirection, null);
		
		lookAt(cameraPosition, Vector3f.add(cameraPosition, cameraDirection, null), up);
	}
	
	/*
	 * Looks at the specified location in world space.
	 */
	public void lookAt(Vector3f cam, Vector3f center, Vector3f up) {
		Vector3f f = normalize(Vector3f.sub(center, cam, null));
		Vector3f u = normalize(up);
		Vector3f s = normalize(Vector3f.cross(f, u, null));
		//u = Vector3f.cross(s, f, null);

		Matrix4f result = new Matrix4f();
		result.m00 = s.x;
		result.m10 = s.y;
		result.m20 = s.z;
		result.m01 = u.x;
		result.m11 = u.y;
		result.m21 = u.z;
		result.m02 = -f.x;
		result.m12 = -f.y;
		result.m22 = -f.z;
		
		viewMatrix = Matrix4f.translate(new Vector3f(-cam.x, -cam.y, -cam.z),  result, null);
	}
	
	
	/*
	 * Normalizes at input vector
	 */
	private Vector3f normalize(Vector3f v){
		float mag = (float)(Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z));
		return new Vector3f(v.x/mag, v.y/mag, v.z/mag);
	}
	

}
