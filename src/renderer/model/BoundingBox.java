package renderer.model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

public class BoundingBox {

	public BoundingBox() {
		lowerLeftFront = null;
		upperRightBack = null;
		vaoId = -1;
		vboIndId = -1;
		isBound = false;
	}
	
	public void addVertex(float [] point) {		
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
	}
	
	/**
	 * Binds the object (becomes immutable)
	 */
	public void bind() {
		this.vaoId = GL30.glGenVertexArrays();
		
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(0); 
		
		float [] boxVertices =  
			{ 
			   lowerLeftFront.x, lowerLeftFront.y, lowerLeftFront.z, 1.0f,
			   upperRightBack.x, lowerLeftFront.y, lowerLeftFront.z, 1.0f,
			   lowerLeftFront.x, upperRightBack.y, lowerLeftFront.z, 1.0f,
			   upperRightBack.x, upperRightBack.y, lowerLeftFront.z, 1.0f,
			   lowerLeftFront.x, lowerLeftFront.y, upperRightBack.z, 1.0f,
			   upperRightBack.x, lowerLeftFront.y, upperRightBack.z, 1.0f,
			   lowerLeftFront.x, upperRightBack.y, upperRightBack.z, 1.0f,
			   upperRightBack.x, upperRightBack.y, upperRightBack.z, 1.0f
			};  
		
		int posVboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, posVboId);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(boxVertices.length);
		buffer.put(boxVertices);
		buffer.flip();
		
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 16, 0);
		
		vboIndId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndId);
		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(INDICES.length);
		indicesBuffer.put(INDICES);
		indicesBuffer.flip();
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);	
		
		GL30.glBindVertexArray(0);
	}

	/**
	 * Computes all vertices of this bounding box given the lower left front and
	 * upper right back. This is used for frustrum culling.
	 */
	public void computeVertices() {
		if (lowerLeftFront != null && upperRightBack != null) {
			vertexList = new ArrayList<Vector3f>();		
			float height = upperRightBack.y - lowerLeftFront.y;
			float width = upperRightBack.z - lowerLeftFront.z;
			float length = upperRightBack.x - lowerLeftFront.x;
			
			vertexList.add(lowerLeftFront);
			vertexList.add(new Vector3f(lowerLeftFront.x, lowerLeftFront.y + height, lowerLeftFront.z));
			vertexList.add(new Vector3f(lowerLeftFront.x, lowerLeftFront.y, lowerLeftFront.z + width));
			vertexList.add(new Vector3f(lowerLeftFront.x, lowerLeftFront.y + height, lowerLeftFront.z + width));

			vertexList.add(upperRightBack);
			vertexList.add(new Vector3f(lowerLeftFront.x + length, lowerLeftFront.y, lowerLeftFront.z));
			vertexList.add(new Vector3f(lowerLeftFront.x + length, lowerLeftFront.y + height, lowerLeftFront.z));
			vertexList.add(new Vector3f(upperRightBack.x, upperRightBack.y + height, upperRightBack.z));
		}
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
	
	@Override
	public String toString() {
		return "Upper right back: "  + upperRightBack + "\nLower left front: " + lowerLeftFront;
	}
	
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
	private List<Vector3f> vertexList;
	
}
