package renderer.model;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderer.Renderable;
import renderer.light.Light;
import renderer.light.LightHandle;
import renderer.shader.ShaderController;
import system.Settings;
import texture.Material;
import texture.Texture;
import texture.TextureManager;

/**
 * Super class for all kinds of models
 * Currently: ModelScene, Model
 * @author Max
 *
 */
public abstract class Model implements Renderable {
	// Defaults
	protected static final Vector3f DEFAULT_INITIAL_POSITION = new Vector3f(0, 0, 0);

	// Enable culling for this Model or not.
	protected boolean enableCulling = true;
	
	// Map of VBOs and indices for each material in the Model
	protected Map<Material, Integer> mapVBOIndexIds;
	protected Map<Material, Integer> mapIndiceCount;

	// Vertex Array Objects
	protected Map<Material, Integer> mapVAOIds;

	// The Model matrix assosciated with this Model.
	protected Matrix4f modelMatrix;

	// Faces that make up this Model.
	protected List<Face> faces;
	protected Map<Material, List<Face>> mapMaterialToFaces;
	
	// LightHandle of the Model
	protected LightHandle mLightHandle = null;
	
	// Flag for whether this Model should be rendered
	protected boolean renderFlag;	
	
	// Initial position of the Model.
	protected Vector3f initialPos;

	// Bounding box for the Model
	protected BoundingBox boundBox;

	// Instance of the shared settings object.
	protected Settings settings = Settings.getInstance();
	
	// If the Model has been bound yet.
	protected boolean isBound = false;
	
	public Model(List<Face> f, Vector3f pos, Vector3f ld, Vector3f ls, Vector3f la) {
		this.faces = f;
		this.initialPos = pos;
		
		// Setup the light associated with this Model
		this.mLightHandle = new LightHandle(this, new Light(pos, ls, ld, la, null));	
		setup();
	}
	
	public Model(List<Face> f, Vector3f pos) {
		this.faces = f;
		this.initialPos = pos;
		setup();
	}
	
	public Model(List<Face> f) {
		this.faces = f;
		this.initialPos = DEFAULT_INITIAL_POSITION;
		setup();
	}
	
	public Model(Model model, Vector3f pos) {
		// Copy the ModelInt faces
		List<Face> faceList = new ArrayList<>();
		for (Face face : model.faces) {
			faceList.add(new Face(face));
		}

		// Set member variables
		this.faces = faceList;
		this.initialPos = pos;
		
		setup();
	}
	

	/**
	 * Bind the ModelInt for rendering
	 * @return
	 */
	public boolean bind() {
		if(isBound)
			return false;

		// Split face list into a list of face lists, each having their own material.
		mapMaterialToFaces = new HashMap<>();
		mapVAOIds = new HashMap<>();
		mapVBOIndexIds = new HashMap<>();
		mapIndiceCount = new HashMap<>();

		Material currentMaterial = null;

		// Generate bounding box
		boundBox = new BoundingBox();

		// Split the faces up by material
		for(Face face : this.faces) {
			currentMaterial = face.getMaterial();

			// If already in the map append to the list (else make new entry)
			if(mapMaterialToFaces.containsKey(currentMaterial)) {
				List<Face> faceList = mapMaterialToFaces.get(currentMaterial);
				faceList.add(face);
			} else {
				List<Face> faceList = new ArrayList<>();
				faceList.add(face);
				mapMaterialToFaces.put(currentMaterial, faceList);
			}
		}

		for(Material material : mapMaterialToFaces.keySet()) {
			List<Face> materialFaces = mapMaterialToFaces.get(material);

			// Put each 'Vertex' in one FloatBuffer (guarenteed to be triangulated by this point
			ByteBuffer verticesByteBuffer = BufferUtils.createByteBuffer(materialFaces.size() *  3 * VertexData.stride);
			FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();

			Map<VertexData, Integer> vboIndexMap = new HashMap<VertexData, Integer>();
			List<Integer> vboIndex = new ArrayList<Integer>();
			VertexData tempVertexData;

			// VBO index (# of unique vertices)
			int iVertex = 0;
			// For each face in the list, process the data and add to the byte buffer.
			for(Face face: materialFaces){			
				//Add first vertex of the face			
				tempVertexData = face.faceData.get(0);
				if(!vboIndexMap.containsKey(tempVertexData)){
					vboIndexMap.put(tempVertexData, iVertex);
					verticesFloatBuffer.put(tempVertexData.getElements());
					boundBox.addVertex(tempVertexData.getXYZ());
					vboIndex.add(iVertex++);
				} else {
					vboIndex.add(vboIndexMap.get(tempVertexData));
				}

				//Add second vertex of the face
				tempVertexData = face.faceData.get(1);
				if(!vboIndexMap.containsKey(tempVertexData)){
					vboIndexMap.put(tempVertexData, iVertex);
					verticesFloatBuffer.put(tempVertexData.getElements());
					boundBox.addVertex(tempVertexData.getXYZ());
					vboIndex.add(iVertex++);
				} else {
					vboIndex.add(vboIndexMap.get(tempVertexData));
				}

				//Add third vertex of the face
				tempVertexData = face.faceData.get(2);
				if(!vboIndexMap.containsKey(tempVertexData)){
					vboIndexMap.put(tempVertexData, iVertex);
					verticesFloatBuffer.put(tempVertexData.getElements());
					boundBox.addVertex(tempVertexData.getXYZ());
					vboIndex.add(iVertex++);
				} else {
					vboIndex.add(vboIndexMap.get(tempVertexData));
				}			
			}

			// Create VBO Index buffer
			verticesFloatBuffer.flip();
			int [] indices = new int[vboIndex.size()];
			int indicesCount = vboIndex.size();
			mapIndiceCount.put(material, indicesCount);

			for(int i = 0; i < vboIndex.size(); i++) {
				indices[i] = vboIndex.get(i); 
			}

			IntBuffer indicesBuffer = BufferUtils.createIntBuffer(vboIndex.size());
			indicesBuffer.put(indices);
			indicesBuffer.flip();

			// Create a new Vertex Array Object in memory and select it (bind)
			int vaoID = GL30.glGenVertexArrays();
			mapVAOIds.put(material, vaoID);
			GL30.glBindVertexArray(vaoID);

			// Enable the attributes
			GL20.glEnableVertexAttribArray(0); //position
			GL20.glEnableVertexAttribArray(1); //color
			GL20.glEnableVertexAttribArray(2); //texture
			GL20.glEnableVertexAttribArray(3); //normal
			GL20.glEnableVertexAttribArray(4);
			GL20.glEnableVertexAttribArray(5);
			GL20.glEnableVertexAttribArray(6);

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

			// Put the normal coordinates in attribute list 4
			GL20.glVertexAttribPointer(4, VertexData.specularElementCount, GL11.GL_FLOAT,
					false, VertexData.stride, VertexData.specularElementByteOffset);

			// Put the normal coordinates in attribute list 5
			GL20.glVertexAttribPointer(5, VertexData.ambientElementCount, GL11.GL_FLOAT,
					false, VertexData.stride, VertexData.ambientElementByteOffset);

			// Put the normal coordinates in attribute list 6
			GL20.glVertexAttribPointer(6, VertexData.specularPowerElementCount, GL11.GL_FLOAT,
					false, VertexData.stride, VertexData.specularPowerElementByteOffset);

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);			

			// Create a new VBO for the indices and select it (bind) - INDICES
			int vboIndId = GL15.glGenBuffers();
			mapVBOIndexIds.put(material, vboIndId);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndId);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);				
		}

		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);

		// Bind all the textures
		for(Material material : this.mapMaterialToFaces.keySet()) {
			TextureManager tm = TextureManager.getInstance();
			Texture tex = material.mapKdTexture;
			int unitId = tm.getTextureSlot();
			tex.bind(unitId, ShaderController.getTexSamplerLocation());
			tm.returnTextureSlot(unitId);
		}

		// Bind the bounding box
		boundBox.bind();

		//Initialize ModelInt matrix (Initialized to the identity in the constructor)
		modelMatrix = new Matrix4f(); 
		renderFlag = true;
		isBound = true;

		return true;
	}
	
	/**
	 * Derived class defines how the model matrix is generated
	 * @param parentMatrix
	 * @param viewMatrix
	 * @return
	 */
	protected abstract Matrix4f getModelMatrix(Matrix4f parentMatrix);
	
	/**
	 * Render a ModelInt that has already been set up
	 * @TODO: Make a class for the HashMaps (a struct) - will keep it cleaner
	 */
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix) {
		if(!renderFlag)
			return;

		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		modelMatrix = getModelMatrix(parentMatrix);
		modelMatrix.store(buffer);
		buffer.flip();

		GL20.glUniformMatrix4(ShaderController.getModelMatrixLocation(), false, buffer);
		
		//TODO(MZ): If not orthogonal (ie, scale) need Matrix4f.transpose(Matrix4f.invert(Matrix4f.mul(viewMatrix, modelMatrix, null), null), null);
		Matrix4f normMatrix = Matrix4f.mul(viewMatrix, modelMatrix, null); 
		normMatrix.store(buffer);
		buffer.flip();

		GL20.glUniformMatrix4(ShaderController.getNormalMatrixLocation(), false, buffer);
		TextureManager tm = TextureManager.getInstance();

		// Do bind and draw for each material's faces
		for(Material material : mapMaterialToFaces.keySet()) {
			List<Integer> rgiUsedSlots = new ArrayList<>();
			// Loop through all texture IDs for a given material
			for(Integer tex : material.getActiveTextureIds()) {
				Integer unitId = tm.getTextureSlot();

				if(unitId == null) {
					continue;
				}

				// Bind and activate sampler 
				GL20.glUniform1i(ShaderController.getTexSamplerLocation(), unitId - GL13.GL_TEXTURE0); //TODO(MZ): This should be mapped to a uniform location specified in the material
				GL13.glActiveTexture(unitId);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
				rgiUsedSlots.add(unitId);
			}

			GL30.glBindVertexArray(mapVAOIds.get(material));
			// Bind to the index VBO that has all the information about the order of the vertices
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mapVBOIndexIds.get(material));
			// Draw the vertices
			GL11.glDrawElements(GL11.GL_TRIANGLES, mapIndiceCount.get(material), GL11.GL_UNSIGNED_INT, 0);
			
			for(Integer iUsed : rgiUsedSlots) {
				tm.returnTextureSlot(iUsed);
			}
		}
	}
	
	/**
	 * Returns the list of faces that make up this ModelInt.
	 * @return the list of faces
	 */
	public List<Face> getFaceList () {
		return faces;
	}
	
	/**
	 * Gets the bounding box for this ModelInt.
	 * @return boundBox
	 */
	public BoundingBox getBoundingBox() {
		return boundBox;
	}

	/**
	 * Get whether the ModelInt is currently being rendered
	 * @return
	 */
	public boolean getRenderFlag() {
		return renderFlag;
	}
	
	
	/**
	 * Abstract model manipulation functions implemented by each derived class
	 */
	public abstract void translate(Vector3f s);
	public abstract void rotateY(float angle);
	public abstract void rotateX(float angle);
	public abstract void rotateZ(float angle);
	public abstract void scale(Vector3f scale);
	
	/**
	 * Setup the Model
	 */
	private void setup() {
		isBound = false;
		
		// Strip any quads / polygons. 
		triangulate();
	}
	
	/**
	 * Remove the non-triangle faces from the Model (triangulates quads)
	 * @param List to remove non-triangles from
	 */
	private void triangulate() {
		List<Face> removeFaces = new ArrayList<Face>();
		List<Face> addFaces = new ArrayList<Face>();
		for (Face face : this.faces) {
			if (face.faceData.size() == 4) {
				removeFaces.add(face);
				addFaces.add(new Face(face.getVertex(0) , face.getVertex(1) , face.getVertex(2), face.getMaterial()));
				addFaces.add(new Face(face.getVertex(0) , face.getVertex(2) , face.getVertex(3), face.getMaterial()));
			} else if (face.faceData.size() > 4){
				removeFaces.add(face); //TODO(MZ): Currently just culls any face > 4 vertices
			}
		}

		this.faces.removeAll(removeFaces);
		this.faces.addAll(addFaces); 
	}
}
