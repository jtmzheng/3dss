package renderer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;


/**
 * Model class is an abstraction used by Renderer. This will use interleaving for vertex properties.
 * @author Max
 * @author Adi
 */
public class Model {
	
	// VBO (GL_ELEMENT_ARRAY_BUFFER).
	private int vboiID;
	
	// Vertex Array Object.
	private int vaoID;
	
	private int indicesCount = 0;
	
	// The model matrix assosciated with this model.
	private Matrix4f modelMatrix = null;
	
	/**
	 * Creates a model with a list of faces.
	 * @param f The list of faces.
	 */
	public Model(List<Face> f){	
		// Put each 'Vertex' in one FloatBuffer
		ByteBuffer verticesByteBuffer = BufferUtils.createByteBuffer(f.size() * 3 *  VertexData.stride);            
		FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
		HashMap<VertexData, Byte> vboIndexMap = new HashMap<VertexData, Byte>();
		List<Byte> vboIndex = new ArrayList<Byte>();
		VertexData tempVertexData;
		
		byte index = 0;
		int common = 0;
		int newC = 0;
		
		// For each face in the list, process the data and add to
		// the byte buffer.
		for(Face face: f){			
			//Add first vertex of the face
			tempVertexData = face.faceData.get(0);
			if(!vboIndexMap.containsKey(tempVertexData)){
				vboIndexMap.put(tempVertexData, index);
				verticesFloatBuffer.put(tempVertexData.getElements());
				vboIndex.add(index++);
				newC++;
			}
			else{
				vboIndex.add(vboIndexMap.get(tempVertexData));
				common++;
			}
			
			//Add second vertex of the face
			tempVertexData = face.faceData.get(1);
			if(!vboIndexMap.containsKey(tempVertexData)){
				vboIndexMap.put(tempVertexData, index);
				verticesFloatBuffer.put(tempVertexData.getElements());
				vboIndex.add(index++);
				newC++;
			}
			else{
				vboIndex.add(vboIndexMap.get(tempVertexData));
				common++;
			}

			//Add third vertex of the face
			tempVertexData = face.faceData.get(2);
			if(!vboIndexMap.containsKey(tempVertexData)){
				vboIndexMap.put(tempVertexData, index);
				verticesFloatBuffer.put(tempVertexData.getElements());
				vboIndex.add(index++);
				newC++;
			}
			else{
				vboIndex.add(vboIndexMap.get(tempVertexData));
				common++;
			}
			
		}
		
		verticesFloatBuffer.flip();
		byte [] indices = new byte[vboIndex.size()];
		indicesCount = vboIndex.size();
		
		for(int i = 0; i < vboIndex.size(); i++){
			indices[i] = vboIndex.get(i); 
		}
		
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(vboIndex.size());
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		 
		// Create a new Vertex Array Object in memory and select it (bind)
		vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		
		// Create a new Vertex Buffer Object in memory and select it (bind)
		int vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesFloatBuffer, GL15.GL_STATIC_DRAW);
		
		// Put the position coordinates in attribute list 0
		GL20.glVertexAttribPointer(0, VertexData.positionElementCount, GL11.GL_FLOAT,
				false, VertexData.stride, VertexData.positionByteOffset);
		
		// Put the color components in attribute list 1
		GL20.glVertexAttribPointer(1, VertexData.colorElementCount, GL11.GL_FLOAT,
				false, VertexData.stride, VertexData.colorByteOffset);
		
		// Put the texture coordinates in attribute list 2
		GL20.glVertexAttribPointer(2, VertexData.textureElementCount, GL11.GL_FLOAT,
				false, VertexData.stride, VertexData.textureByteOffset);
		
		// Put the normal coordinates in attribute list 3
		GL20.glVertexAttribPointer(3, VertexData.textureElementCount, GL11.GL_FLOAT,
				false, VertexData.stride, VertexData.normalByteOffset);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);
		
		// Create a new VBO for the indices and select it (bind) - INDICES
		vboiID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		System.out.println("COMMON: " + common);
		System.out.println("NEWC: " + newC);
		
		//Initialize model matrix
		modelMatrix = new Matrix4f(); //Initialized to the identity in the constructor
				
	}
	
	/**
	 * Get the index VBO.
	 * @return vboiID
	 */
	public int getIndexVBO(){
		return vboiID;
	}
	
	/**
	 * Get the vertex array object ID.
	 * @return vaoID
	 */
	public int getVAO(){
		return vaoID;
	}
	
	/**
	 * Gets the number of indices.
	 * @return indicesCount
	 */
	public int getIndicesCount(){
		return indicesCount;
	}
	
	/**
	 * Translate the model by a given vector.
	 * @param s The translation vector.
	 */
	public void translate(Vector3f s){
		Matrix4f.translate(s, modelMatrix, modelMatrix);
	}
	
	/**
	 * Rotate Y axis.
	 * @param angle The angle to rotate by.
	 */
	public void rotateY(float angle){
		Matrix4f.rotate(angle, new Vector3f(0f, 1f, 0f), modelMatrix, modelMatrix);
	}
	
	/**
	 * Rotate X axis.
	 * @param angle The angle to rotate by.
	 */	
	public void rotateX(float angle){
		Matrix4f.rotate(angle, new Vector3f(1f, 0f, 0f), modelMatrix, modelMatrix);
	}
	
	/**
	 * Rotate Z axis.
	 * @param angle The angle to rotate by.
	 */
	public void rotateZ(float angle){
		Matrix4f.rotate(angle, new Vector3f(0f, 0f, 1f), modelMatrix, modelMatrix);
	}
	
	/**
	 * Scale the model by a given vector.
	 * @param scale The scale vector to scale by.
	 */
	public void scale(Vector3f scale){
		Matrix4f.scale(scale, modelMatrix, modelMatrix);
	}
	
	/**
	 * Scale the model by a scalar.
	 * @param scale The scalar to scale by.
	 */
	public void scale(float scale){
		Matrix4f.scale(new Vector3f(scale, scale, scale), modelMatrix, modelMatrix);
	}
	
	/**
	 * Get the model matrix associated with this model.
	 * @return modelMatrix The model matrix.
	 */
	public Matrix4f getModelMatrix(){
		return modelMatrix;
	}
	
	
}
