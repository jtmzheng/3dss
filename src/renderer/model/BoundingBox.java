package renderer.model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

/**
 * A bounding box used to wrap objects to optimize things such as
 * object picking and frustrum culling.
 * @TODO(MZ): Can implement Renderable (or maybe OctObj), might make more sense for this to be an OctObj, and key to a Renderable
 * @author Max
 * @author Adi
 */
public class BoundingBox {
	public static final int [] INDICES =  
		{ 
			0, 1, 2, 3, 8, // Front wall
			4, 5, 6, 7, 8, // Back wall
			4, 0, 6, 2, 8, // Left wall
			1, 5, 3, 7, 8, // Right wall
			2, 3, 6, 7, 8, // Top wall
			0, 1, 4, 5     // Bottom wall
		}; 
	public static final int PRIMITIVE_RESTART_INDEX = 8;
	
	private Vector3f lowerLeftFront;
	private Vector3f upperRightBack;
	private Integer vaoId;
	private Integer vboIndId;
	private boolean isBound;
	private float[] vertexList = {};

	public BoundingBox(Vector3f p1, Vector3f p2) {
		lowerLeftFront = p1;
		upperRightBack = p2;
		vaoId = -1;
		vboIndId = -1;
		isBound = false;
	}

	public BoundingBox() {
		lowerLeftFront = null;
		upperRightBack = null;
		vaoId = -1;
		vboIndId = -1;
		isBound = false;
	}
	
	public boolean addVertex(float [] point) {		
		if(isBound)
			return false;
		
		if(lowerLeftFront == null) {
			lowerLeftFront = new Vector3f(point[0], point[1], point[2]);
		} else {
			lowerLeftFront.x = Math.min(lowerLeftFront.x, point[0]);
			lowerLeftFront.y = Math.min(lowerLeftFront.y, point[1]);
			lowerLeftFront.z = Math.max(lowerLeftFront.z, point[2]);
		}
		
		if(upperRightBack == null) {
			upperRightBack = new Vector3f(point[0], point[1], point[2]);
		} else {
			upperRightBack.x = Math.max(upperRightBack.x, point[0]);
			upperRightBack.y = Math.max(upperRightBack.y, point[1]);
			upperRightBack.z = Math.min(upperRightBack.z, point[2]);
		}
		
		return true;
	}
	
	/**
	 * Binds the object (becomes immutable)
	 * @return false if bound already
	 */
	public boolean bind() {
		if(isBound)
			return false;
		
		this.vaoId = GL30.glGenVertexArrays();
		
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(0); 
		
		vertexList = currentBounds();

		int posVboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, posVboId);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(vertexList.length);
		buffer.put(vertexList);
		buffer.flip();
		
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 16, 0);
		
		vboIndId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndId);
		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(INDICES.length);
		indicesBuffer.put(INDICES);
		indicesBuffer.flip();
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		
		isBound = true;
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);	
		GL30.glBindVertexArray(0);
		return isBound;
	}

	public boolean isBound() {
		return isBound;
	}
	
	public Integer getVAO() {
		return vaoId;
	}
	
	public Integer getVBOInd() {
		return vboIndId;
	}
	
	public float[] getVertexList() {
		return vertexList;
	}
	
	public Vector3f getCentre() {
		return new Vector3f(
				(lowerLeftFront.x + upperRightBack.x) / 2,
				(lowerLeftFront.y + upperRightBack.y) / 2,
				(lowerLeftFront.z + upperRightBack.z) / 2);
	}
	
	public Vector3f getWidth() {
		return new Vector3f(
				(upperRightBack.x - lowerLeftFront.x),
				(upperRightBack.y - lowerLeftFront.y),
				(upperRightBack.z - lowerLeftFront.z));
	}
	
	public float getCentre(int dim) {
		if(dim == 0) return (lowerLeftFront.x + upperRightBack.x) / 2;
		if(dim == 1) return (lowerLeftFront.y + upperRightBack.y) / 2;
		if(dim == 2) return (lowerLeftFront.z + upperRightBack.z) / 2;
		return 0;
	}
	
	public float getWidth(int dim) {
		if(dim == 0) return Math.abs(upperRightBack.x - lowerLeftFront.x);
		if(dim == 1) return Math.abs(upperRightBack.y - lowerLeftFront.y);
		if(dim == 2) return Math.abs(upperRightBack.z - lowerLeftFront.z);
		return 0;
	}

	/**
	 * This bisects the bounding box into two bounding boxes by using the plane defined by
	 * the center point of the bounding box in the provided dimension.
	 * @param dim
	 * @return The left side of the bisection.
	 */
	public BoundingBox bisectLeft (int dim) {
		float halfWidth = getWidth(dim) / 2;

		if (dim == 0) {
			return new BoundingBox(
				lowerLeftFront,
				new Vector3f(upperRightBack.x - halfWidth, upperRightBack.y, upperRightBack.z)
			);
		} else if (dim == 1) {
			return new BoundingBox(
				lowerLeftFront,
				new Vector3f(upperRightBack.x, upperRightBack.y - halfWidth, upperRightBack.z)
			);
		} else {
			return new BoundingBox(
				lowerLeftFront,
				new Vector3f(upperRightBack.x, upperRightBack.y, upperRightBack.z - halfWidth)
			);
		}
	}

	/**
	 * This bisects the bounding box into two bounding boxes by using the plane defined by
	 * the center point of the bounding box in the provided dimension.
	 * @param dim
	 * @return The right side of the bisection.
	 */
	public BoundingBox bisectRight (int dim) {
		float halfWidth = getWidth(dim) / 2;

		if (dim == 0) {
			return new BoundingBox(
				new Vector3f(lowerLeftFront.x + halfWidth, lowerLeftFront.y, lowerLeftFront.z),
				upperRightBack
			);
		} else if (dim == 1) {
			return new BoundingBox(
				new Vector3f(lowerLeftFront.x, lowerLeftFront.y + halfWidth, lowerLeftFront.z),
				upperRightBack
			);
		} else {
			return new BoundingBox(
				new Vector3f(lowerLeftFront.x, lowerLeftFront.y, lowerLeftFront.z  + halfWidth),
				upperRightBack
			);
		}
	}

	/**
	 * Returns the bounds of this BoundingBox.
	 * 
	 * WARNING(adi): the actual bounds of the box are still subject to change, atleast until the
	 * the bind() method is called. This function should only be used when using BoundingBoxes as a
	 * wrapper representing a bounding volume, rather than a renderable object tied to OpenGL.
	 * @return
	 */
	public float[] currentBounds() {
		float[] ret = { 
			   lowerLeftFront.x, lowerLeftFront.y, lowerLeftFront.z, 1.0f,
			   upperRightBack.x, lowerLeftFront.y, lowerLeftFront.z, 1.0f,
			   lowerLeftFront.x, upperRightBack.y, lowerLeftFront.z, 1.0f,
			   upperRightBack.x, upperRightBack.y, lowerLeftFront.z, 1.0f,
			   lowerLeftFront.x, lowerLeftFront.y, upperRightBack.z, 1.0f,
			   upperRightBack.x, lowerLeftFront.y, upperRightBack.z, 1.0f,
			   lowerLeftFront.x, upperRightBack.y, upperRightBack.z, 1.0f,
			   upperRightBack.x, upperRightBack.y, upperRightBack.z, 1.0f
			};  
		return ret;
	}

	@Override
	public String toString() {
		return "Upper right back: "  + upperRightBack + "\nLower left front: " + lowerLeftFront;
	}

}
