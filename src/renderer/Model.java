package renderer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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

import texture.Material;

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
	private Matrix4f modelMatrix;

	// Faces that make up this model.
	private List<Face> faces;
	
	// LightHandle of the model
	private LightHandle m_LightHandle = null;

	public Model(List<Face> f, Vector3f pos, Vector3f ld, Vector3f ls, Vector3f la){
		this.faces = f;
		
		// Setup the model 
		setup();

		// Transform
		this.translate(pos);

		// Setup the light associated with this model
		m_LightHandle = new LightHandle(this, new Light(pos, ls, ld, la, null)); 
	}
	
	public Model(List<Face> f, Vector3f pos){
		this.faces = f;

		// Setup the model 
		setup();
		
		// Transform
		this.translate(pos);

	}
	
	/**
	 * Creates a model with a list of faces.
	 * @param f The list of faces.
	 */
	public Model(List<Face> f){
		this.faces = f;

		setup();
	}

	
	/**
	 * Common setup for constructor
	 * @param f
	 */
	public void setup(){
		long curTime = System.currentTimeMillis();

		// Strip any quads / polygons. 
		this.triangulate();

		// Split face list into a list of face lists, each having their own material.
		List<ArrayList<Face>> facesByMaterial = new ArrayList<ArrayList<Face>>();
		Material currentMaterial = null;
		ArrayList<Face> currentFaceList = new ArrayList<Face>();
		for (Face face : this.faces) {
			if (face.getMaterial() != currentMaterial) {
				if (!currentFaceList.isEmpty()) {
					facesByMaterial.add(currentFaceList);
				}
				currentMaterial = face.getMaterial();
				currentFaceList = new ArrayList<Face>();
			}
			currentFaceList.add(face);
		}
		
		System.out.println("Number of face lists by material: " + facesByMaterial.size());
		
		// Put each 'Vertex' in one FloatBuffer
		ByteBuffer verticesByteBuffer = BufferUtils.createByteBuffer(this.faces.size() *  3 * VertexData.stride); //TODO : Allocating proper amount
		FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
		HashMap<VertexData, Integer> vboIndexMap = new HashMap<VertexData, Integer>();
		List<Integer> vboIndex = new ArrayList<Integer>();
		VertexData tempVertexData;

		
		// VBO index
		int index = 0;

		/** For each face in the list, process the data and add to 
		 *  the byte buffer.
		 */
		for(Face face: this.faces){			
			//Add first vertex of the face			
			tempVertexData = face.faceData.get(0);
			if(!vboIndexMap.containsKey(tempVertexData)){
				vboIndexMap.put(tempVertexData, index);
				verticesFloatBuffer.put(tempVertexData.getElements());
				vboIndex.add(index++);
			}
			else{
				vboIndex.add(vboIndexMap.get(tempVertexData));
			}
			
			//Add second vertex of the face
			tempVertexData = face.faceData.get(1);
			if(!vboIndexMap.containsKey(tempVertexData)){
				vboIndexMap.put(tempVertexData, index);
				verticesFloatBuffer.put(tempVertexData.getElements());
				vboIndex.add(index++);
			}
			else{
				vboIndex.add(vboIndexMap.get(tempVertexData));
			}

			//Add third vertex of the face
			tempVertexData = face.faceData.get(2);
			if(!vboIndexMap.containsKey(tempVertexData)){
				vboIndexMap.put(tempVertexData, index);
				verticesFloatBuffer.put(tempVertexData.getElements());
				vboIndex.add(index++);
			}
			else{
				vboIndex.add(vboIndexMap.get(tempVertexData));
			}

		}

		//Create VBO Index buffer
		verticesFloatBuffer.flip();
		int [] indices = new int[vboIndex.size()];
		indicesCount = vboIndex.size();

		for(int i = 0; i < vboIndex.size(); i++){
			indices[i] = vboIndex.get(i); 
		}

		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(vboIndex.size());
		indicesBuffer.put( indices );
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
		GL20.glVertexAttribPointer(3, VertexData.normalElementCount, GL11.GL_FLOAT,
				false, VertexData.stride, VertexData.normalByteOffset);

		// Put the normal coordinates in attribute list 3
		GL20.glVertexAttribPointer(4, VertexData.specularElementCount, GL11.GL_FLOAT,
				false, VertexData.stride, VertexData.specularElementByteOffset);
		
		// Put the normal coordinates in attribute list 3
		GL20.glVertexAttribPointer(5, VertexData.ambientElementCount, GL11.GL_FLOAT,
				false, VertexData.stride, VertexData.ambientElementByteOffset);
		
		// Put the normal coordinates in attribute list 3
		GL20.glVertexAttribPointer(6, VertexData.specularPowerElementCount, GL11.GL_FLOAT,
				false, VertexData.stride, VertexData.specularPowerElementByteOffset);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		// Create a new VBO for the indices and select it (bind) - INDICES
		vboiID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		//Initialize model matrix
		modelMatrix = new Matrix4f(); //Initialized to the identity in the constructor

		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);
		System.out.println("Model loading to GPU: " + (System.currentTimeMillis() - curTime));
	}
	
	/**
	 * Get the index VBO.
	 * @return the VBO ID
	 */
	public int getIndexVBO(){
		return vboiID;
	}

	/**
	 * Get the vertex array object ID.
	 * @return the VAO ID
	 */
	public int getVAO(){
		return vaoID;
	}

	/**
	 * Gets the number of indices.
	 * @return the number of indices
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
	 * @return the model matrix
	 */
	public Matrix4f getModelMatrix(){
		return modelMatrix;
	}
	
	/**
	 * Add a light to this model 
	 * @param light
	 */
	public void addLight(Light light) {
		if(m_LightHandle != null) {
			m_LightHandle.invalidate();
		}
		
		m_LightHandle = new LightHandle(this, light);
	}
	
	/**
	 * Remove the light associated with this model
	 */
	public void removeLight() {
		if(m_LightHandle != null) {
			m_LightHandle.invalidate();
		}
	}

	/**
	 * Remove the non-triangle faces from the model
	 * @param List to remove non-triangles from
	 */
	private void triangulate (){
		List<Face> removeFaces = new ArrayList<Face>();
		List<Face> addFaces = new ArrayList<Face>();
		for (Face face : this.faces) {
			if (face.faceData.size() == 4) {
				removeFaces.add(face);
				addFaces.add(new Face( face.getVertex(0) , face.getVertex(1) , face.getVertex(2), face.getMaterial() ));
				addFaces.add(new Face( face.getVertex(0) , face.getVertex(2) , face.getVertex(3), face.getMaterial() ));
			}
			else if (face.faceData.size() > 4){
				removeFaces.add(face);
			}
		}

		this.faces.removeAll(removeFaces);
		this.faces.addAll(addFaces); 
	}
}