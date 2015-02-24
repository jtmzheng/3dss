package renderer.model;

import org.lwjgl.util.vector.Vector3f;

public interface Boundable {
	public Vector3f getMin();
	public Vector3f getMax();
	public Vector3f getCentre();
	public Vector3f getWidth();
	
	// For convinience
	public float getWidth(int dim);
	public float getCentre(int dim);
}
