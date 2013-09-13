package renderer;

import org.lwjgl.util.vector.Vector3f;

/*
 * May rename Triangle in the future
 * @author Max
 */
public class Face {

	public Vector3f vertex; //vertex index
	public Vector3f normal; //vertex normal index
	public Vector3f texture; //texture index
	
	public Face(Vector3f vertex, Vector3f normal /*, Vector3f texture*/){
		this.vertex = vertex;
		this.normal = normal;
	//	this.texture = texture;
	}
	
}
