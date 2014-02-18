package renderer.model;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Quat4f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import physics.PhysicsModel;
import physics.PhysicsModelProperties;
import renderer.light.Light;
import renderer.light.LightHandle;
import renderer.shader.ShaderController;
import system.Settings;
import texture.Material;
import texture.Texture;
import texture.TextureManager;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;

/**
 * Model class is an abstraction used by both the renderer and the physics engine. Each model represents a physical object
 * in the environment. The OpenGL attributes will be passed as an interleaved VBO. Changes are applied to the physics model.
 *
 * @TODO: This class needs cleanup.
 * @TODO: Dynamically assigning attributes for each model
 * @author Max
 * @author Adi
 */
public class Model {
	// Defaults
	private static final Vector3f DEFAULT_INITIAL_POSITION = new Vector3f(0, 0, 0);

	// Unique ID for the model (used for picking)
	private final int uniqueId;
	private final Vector3f uniqueIdColour;
	
	// Map of VBOs and indices for each material in the model
	private Map<Material, Integer> mapVBOIndexIds;
	private Map<Material, Integer> mapIndiceCount;

	// Vertex Array Objects
	private Map<Material, Integer> mapVAOIds;

	// The model matrix assosciated with this model.
	private Matrix4f modelMatrix;

	// Faces that make up this model.
	private List<Face> faces;
	private Map<Material, List<Face>> mapMaterialToFaces;

	// LightHandle of the model
	private LightHandle mLightHandle = null;

	// TextureManager instance
	private TextureManager texManager;

	// Physics model
	private PhysicsModel physicsModel;

	// Flag for whether this model should be rendered
	private boolean renderFlag;	

	// Physics properties of the model.
	private PhysicsModelProperties physicsProps;

	// Initial position of the model.
	private Vector3f initialPos;
	
	// Bounding box for the model
	private BoundingBox boundBox;

	// If the model is set up yet.
	private boolean isGLsetup = false;

	/**
	 * Merges the meshes of two models and returns the merged model.
	 * Ignores the physics model properties of the two and uses the defaults. If custom
	 * physics properties are required, please use the other merge method.
	 * @param a
	 * @param b
	 * @return the merged model
	 */
	public static Model merge (Model a, Model b) {
		return Model.merge(a, b, new PhysicsModelProperties());
	}

	/**
	 * Merges the meshes of two models and returns the merged model, with custom
	 * physics properties.
	 * @param a The first model.
	 * @param b The second model.
	 * @param props Custom physics model properties.
	 * @return the merged model
	 */
	public static Model merge (Model a, Model b, PhysicsModelProperties props) {
		Matrix4f mMatrixA = a.getPhysicsModel().getTransformMatrix();
		Matrix4f mMatrixB = b.getPhysicsModel().getTransformMatrix();

		List<Face> mergedList = new ArrayList<Face>();
		for (Face face : a.getFaceList()) {
			List<VertexData> transformedVertices = new ArrayList<>();
			for (VertexData v : face.getVertices()) {
				float[] pos = v.getXYZW();
				Vector4f position = new Vector4f(pos[0], pos[1], pos[2], pos[3]);
				Matrix4f.transform(mMatrixA, position, position);
				transformedVertices.add(new VertexData(v, position));
			}
			mergedList.add(new Face(transformedVertices, face.getMaterial()));
		}
		for (Face face : b.getFaceList()) {
			List<VertexData> transformedVertices = new ArrayList<>();
			for (VertexData v : face.getVertices()) {
				float[] pos = v.getXYZW();
				Vector4f position = new Vector4f(pos[0], pos[1], pos[2], pos[3]);
				Matrix4f.transform(mMatrixB, position, position);
				transformedVertices.add(new VertexData(v, position));
			}
			mergedList.add(new Face(transformedVertices, face.getMaterial()));
		}
		return new Model(mergedList, props);
	}

	/**
	 * Merges the meshes of a list of models and returns the merged model.
	 * Ignores the physics model properties of the models in the list and uses the defaults.
	 * If custom physics properties are required, please use the other merge method.
	 * @param modelList the list of models to merge
	 * @return the merged model
	 */
	public static Model merge (List<Model> modelList) {
		return Model.merge(modelList, new PhysicsModelProperties());
	}

	/**
	 * Merges the meshes of a list of models and returns the merged model, with custom
	 * physics properties.
	 * @param modelList the list of models to merge
	 * @return the merged model
	 */
	public static Model merge (List<Model> modelList, PhysicsModelProperties props) {
		if (modelList.size() <= 1) {
			throw new IllegalArgumentException("Requires a list of size greater than one.");
		}

		Model mergedModel = modelList.get(0);
		for (int i = 1; i < modelList.size(); i++) {
			mergedModel = Model.merge(mergedModel, modelList.get(i), props);
		}

		return mergedModel;
	}

	/**
	 * Constructs a model (a representation of a 3D object).
	 * @param f The list of faces that make up the model.
	 * @param pos The initial position of the model.
	 * @param ld The diffuse light intensity.
	 * @param ls The specular light intensity.
	 * @param la The ambient light intensity.
	 * @param rigidBodyProp Custom physics properties this model should have.
	 */
	public Model(List<Face> f, 
			Vector3f pos, 
			Vector3f ld, 
			Vector3f ls, 
			Vector3f la, 
			PhysicsModelProperties rigidBodyProp){

		this.faces = f;
		this.physicsProps = rigidBodyProp;
		this.boundBox = new BoundingBox();

		// Get instance of texture manager
		texManager = TextureManager.getInstance();

		initialPos = pos;

		// Setup the light associated with this model
		mLightHandle = new LightHandle(this, new Light(pos, ls, ld, la, null));

		// Set the ID to the hash code
		uniqueIdColour = encodeColour(hashCode());
		uniqueId = decodeColour(uniqueIdColour.x, uniqueIdColour.y, uniqueIdColour.z);
		
		// Set up the physics model.
		setupPhysicsModel();
	}

	/**
	 * Constructs a model (a representation of a 3D object).
	 * @param f The list of faces that make up the model.
	 * @param pos The initial position of the model.
	 * @param rigidBodyProp Custom physics properties this model should have.
	 */
	public Model(List<Face> f,
			Vector3f pos,
			PhysicsModelProperties rigidBodyProp){

		this.faces = f;
		this.physicsProps = rigidBodyProp;
		this.boundBox = new BoundingBox();

		// Get instance of texture manager
		texManager = TextureManager.getInstance();

		// Setup the model 
		initialPos = pos;

		// Set the ID to the hash code
		uniqueIdColour = encodeColour(hashCode());
		uniqueId = decodeColour(uniqueIdColour.x, uniqueIdColour.y, uniqueIdColour.z);

		setupPhysicsModel();
	}

	/**
	 * Constructs a model (a representation of a 3D object).
	 * @param f The list of faces that make up the model.
	 * @param rigidBodyProp Custom physics properties this model should have.
	 */
	public Model(List<Face> f,
			PhysicsModelProperties rigidBodyProp){

		this.faces = f;
		this.physicsProps = rigidBodyProp;
		this.boundBox = new BoundingBox();

		// Get instance of texture manager
		texManager = TextureManager.getInstance();

		initialPos = DEFAULT_INITIAL_POSITION;

		// Set the ID to the hash code
		uniqueIdColour = encodeColour(hashCode());
		uniqueId = decodeColour(uniqueIdColour.x, uniqueIdColour.y, uniqueIdColour.z);
		
		setupPhysicsModel();
	}

	/**
	 * Constructs a model (a representation of a 3D object). This constructor
	 * uses default physicsmodel properties.
	 * @param f
	 */
	public Model(List<Face> f) {
		this.faces = f;
		this.physicsProps = new PhysicsModelProperties();
		this.boundBox = new BoundingBox();

		// Get instance of texture manager.
		texManager = TextureManager.getInstance();

		initialPos = DEFAULT_INITIAL_POSITION;

		// Set the UID to the hash code
		uniqueIdColour = encodeColour(hashCode());
		uniqueId = decodeColour(uniqueIdColour.x, uniqueIdColour.y, uniqueIdColour.z);
		
		setupPhysicsModel();
	}
	/**
	 * Copy constructor
	 * @param model Model to copy
	 * @param position Initial position of copy
	 */
	public Model(Model model, 
			Vector3f position) {

		// Copy the model faces
		List<Face> faceList = new ArrayList<>();
		for (Face face : model.getFaceList()) {
			faceList.add(new Face(face));
		}

		// Set member variables
		this.faces = faceList;
		this.physicsProps = new PhysicsModelProperties(model.getPhysicsProperties());
		this.boundBox = new BoundingBox();

		// Get instance of texture manager
		texManager = TextureManager.getInstance();

		initialPos = position;

		// Set the UID to the hash code
		uniqueIdColour = encodeColour(hashCode());
		uniqueId = decodeColour(uniqueIdColour.x, uniqueIdColour.y, uniqueIdColour.z);
		
		setupPhysicsModel();
	}

	/**
	 * Setup GL for rendering.
	 */
	public void setupGL(){
		isGLsetup = true;

		// Strip any quads / polygons. 
		this.triangulate();

		// Split face list into a list of face lists, each having their own material.
		mapMaterialToFaces = new HashMap<>();

		// Set up HashMaps
		mapVAOIds = new HashMap<>();
		mapVBOIndexIds = new HashMap<>();
		mapIndiceCount = new HashMap<>();

		Material currentMaterial = null;

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

			// Put each 'Vertex' in one FloatBuffer
			ByteBuffer verticesByteBuffer = BufferUtils.createByteBuffer(materialFaces.size() *  3 * VertexData.stride); //TODO : Allocating proper amount
			FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
			
			Map<VertexData, Integer> vboIndexMap = new HashMap<VertexData, Integer>();
			List<Integer> vboIndex = new ArrayList<Integer>();
			VertexData tempVertexData;

			// VBO index
			int index = 0;

			/** 
			 *  For each face in the list, process the data and add to 
			 *  the byte buffer.
			 */
			for(Face face: materialFaces){			
				//Add first vertex of the face			
				tempVertexData = face.faceData.get(0);
				if(!vboIndexMap.containsKey(tempVertexData)){
					vboIndexMap.put(tempVertexData, index);
					verticesFloatBuffer.put(tempVertexData.getElements());
					boundBox.addVertex(tempVertexData.getXYZ());
					vboIndex.add(index++);
				} else {
					vboIndex.add(vboIndexMap.get(tempVertexData));
				}

				//Add second vertex of the face
				tempVertexData = face.faceData.get(1);
				if(!vboIndexMap.containsKey(tempVertexData)){
					vboIndexMap.put(tempVertexData, index);
					verticesFloatBuffer.put(tempVertexData.getElements());
					boundBox.addVertex(tempVertexData.getXYZ());
					vboIndex.add(index++);
				} else {
					vboIndex.add(vboIndexMap.get(tempVertexData));
				}

				//Add third vertex of the face
				tempVertexData = face.faceData.get(2);
				if(!vboIndexMap.containsKey(tempVertexData)){
					vboIndexMap.put(tempVertexData, index);
					verticesFloatBuffer.put(tempVertexData.getElements());
					boundBox.addVertex(tempVertexData.getXYZ());
					vboIndex.add(index++);
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
			indicesBuffer.put( indices );
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
			int vboiID = GL15.glGenBuffers();
			mapVBOIndexIds.put(material, vboiID);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiID);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);				
		}

		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);

		// Bind all the textures
		for(Material material : this.mapMaterialToFaces.keySet()) {
			Texture tex = material.mapKdTexture;
			int textureUnitId = texManager.getTextureSlot();
			tex.bind(textureUnitId);
			texManager.returnTextureSlot(textureUnitId);
		}
		
		// Bind the bounding box
		boundBox.bind();

		//Initialize model matrix (Initialized to the identity in the constructor)
		modelMatrix = new Matrix4f(); 

		renderFlag = true;
	}

	public void renderPicking() {
		if(renderFlag) {		
			FloatBuffer modelMatrixBuffer = BufferUtils.createFloatBuffer(16);
			FloatBuffer uniqueIdBuffer = BufferUtils.createFloatBuffer(3);
			
			modelMatrix = physicsModel.getTransformMatrix();
			modelMatrix.store(modelMatrixBuffer);
			modelMatrixBuffer.flip();
			uniqueIdColour.store(uniqueIdBuffer);
			uniqueIdBuffer.flip();

			GL20.glUniformMatrix4(ShaderController.getModelMatrixLocation(), false, modelMatrixBuffer);
			GL20.glUniform3(ShaderController.getUniqueIdLocation(), uniqueIdBuffer);

			GL11.glEnable(GL31.GL_PRIMITIVE_RESTART_INDEX); 
			GL31.glPrimitiveRestartIndex(BoundingBox.PRIMITIVE_RESTART_INDEX);
			GL30.glBindVertexArray(boundBox.getVAO());
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, boundBox.getVBOInd());
			GL11.glDrawElements(GL11.GL_TRIANGLE_STRIP, BoundingBox.INDICES.length, GL11.GL_UNSIGNED_INT, 0);
			GL11.glDisable(GL31.GL_PRIMITIVE_RESTART_INDEX); 

			/* 
			 * The code below is more accurate for picking, but slower 
			 */
			
			/*
			// Do bind and draw for each material's faces
			for(Material material : mapMaterialToFaces.keySet()) {
				GL30.glBindVertexArray(mapVAOIds.get(material));
				
				// Bind to the index VBO that has all the information about the order of the vertices
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mapVBOIndexIds.get(material));

				// Draw the vertices
				GL11.glDrawElements(GL11.GL_TRIANGLES, mapIndiceCount.get(material), GL11.GL_UNSIGNED_INT, 0);
			}
			*/
			
		}
	}

	/**
	 * Render a model that has already been set up
	 * @TODO: Make a class for the HashMaps (a struct) - will keep it cleaner
	 */
	public void render(boolean isPicked) {
		if(renderFlag) {		
			FloatBuffer modelMatrixBuffer = BufferUtils.createFloatBuffer(16);
			modelMatrix = physicsModel.getTransformMatrix();
			modelMatrix.store(modelMatrixBuffer);
			modelMatrixBuffer.flip();

			GL20.glUniformMatrix4(ShaderController.getModelMatrixLocation(), false, modelMatrixBuffer);
			
			// If model is picked change the colour
			if(isPicked) {
				GL20.glUniform1i(ShaderController.getSelectedModelLocation(), 1);
			} else {
				GL20.glUniform1i(ShaderController.getSelectedModelLocation(), 0);
			}

			// Do bind and draw for each material's faces
			for(Material material : mapMaterialToFaces.keySet()) {
				// Loop through all texture Ids for a given material
				for(Integer tex : material.getActiveTextureIds()) {
					Integer unitId = texManager.getTextureSlot();

					// If invalid continue
					if(unitId == null) {
						continue;
					}

					// Bind and activate sampler 
					GL20.glUniform1i(ShaderController.getTexSamplerLocation(), unitId - GL13.GL_TEXTURE0);
					GL13.glActiveTexture(unitId);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
					texManager.returnTextureSlot(unitId);
				}

				GL30.glBindVertexArray(mapVAOIds.get(material));

				// Bind to the index VBO that has all the information about the order of the vertices
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mapVBOIndexIds.get(material));

				// Draw the vertices
				GL11.glDrawElements(GL11.GL_TRIANGLES, mapIndiceCount.get(material), GL11.GL_UNSIGNED_INT, 0);
			}
		}
	}

	/**
	 * Apply a force on the model 
	 * @param force
	 */
	public void applyForce(Vector3f force) {
		physicsModel.applyForce(force);
	}

	/**
	 * Translate the model by a given vector
	 * @param s The displacement vector
	 */
	public void translate(Vector3f s) {
		physicsModel.translate(new javax.vecmath.Vector3f(s.x,
				s.y,
				s.z));
	}

	/**
	 * Rotate about the y-axis
	 * @param angle The angle to rotate by.
	 */
	public void rotateY(float angle){
		physicsModel.rotateY(angle);
	}

	/**
	 * Rotate about the x-axis
	 * @param angle The angle to rotate by.
	 */	
	public void rotateX(float angle){
		physicsModel.rotateX(angle);
	}

	/**
	 * Rotate about the z-axis
	 * @param angle The angle to rotate by.
	 */
	public void rotateZ(float angle){
		physicsModel.rotateZ(angle);
	}

	/**
	 * Scale the model by a given vector.
	 * @param scale The scale vector to scale by.
	 * @deprecated
	 */
	public void scale(Vector3f scale){
		Matrix4f.scale(scale, modelMatrix, modelMatrix);
	}

	/**
	 * Scale the model by a scalar.
	 * @param scale The scalar to scale by.
	 * @deprecated
	 */
	public void scale(float scale){
		Matrix4f.scale(new Vector3f(scale, scale, scale), modelMatrix, modelMatrix);
	}

	/**
	 * Get the model matrix associated with this model.
	 * @return the model matrix
	 */
	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}

	public float[] getModelMatrixBuffer() {
		return physicsModel.getOpenGLTransformMatrix();
	}

	/**
	 * Get the physics model associated with this model.
	 * @return
	 */
	public PhysicsModel getPhysicsModel() {
		return physicsModel;
	}

	/**
	 * Get whether the model is currently being rendered
	 * @return
	 */
	public boolean getRenderFlag() {
		return renderFlag;
	}

	/**
	 * Returns the list of faces that make up this model.
	 * @return the list of faces
	 */
	public List<Face> getFaceList () {
		return faces;
	}

	/**
	 * Returns the physics properties that this model has.
	 * @return the physics properties of the model
	 */
	public PhysicsModelProperties getPhysicsProperties () {
		return physicsProps;
	}

	/**
	 * Get the unique ID of the model
	 * @return uniqueId 
	 */
	public int getUID() {
		return uniqueId;
	}

	/**
	 * Returns if the model is set up for rendering
	 * @return isGLsetup
	 */
	public boolean isGLsetup() {
		return isGLsetup;
	}

	/**
	 * Set flag for whether this model should be rendered
	 * @param renderFlag
	 */
	public void setRenderFlag(boolean renderFlag) {
		this.renderFlag = renderFlag;
	}

	/**
	 * Get the origin of the model
	 * @return
	 */
	public javax.vecmath.Vector3f getModelOrigin() {
		return physicsModel.getRigidBody().getWorldTransform(new Transform()).origin;
	}

	/**
	 * Add a light to this model 
	 * @param light
	 */
	public void addLight(Light light) {
		if(mLightHandle != null) {
			mLightHandle.invalidate();
		}

		mLightHandle = new LightHandle(this, light);
	}

	/**
	 * Resets the model kinematics
	 */
	public void resetModelKinematics() {
		physicsModel.getRigidBody().setAngularVelocity(new javax.vecmath.Vector3f());
		physicsModel.getRigidBody().setLinearVelocity(new javax.vecmath.Vector3f());
	}

	/**
	 * Resets the model forces
	 */
	public void resetModelForces() {
		physicsModel.getRigidBody().clearForces();
	}

	/**
	 * Remove the light associated with this model
	 */
	public void removeLight() {
		if(mLightHandle != null) {
			mLightHandle.invalidate();
		}
	}

	/**
	 * Remove the non-triangle faces from the model
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
				removeFaces.add(face);
			}
		}

		this.faces.removeAll(removeFaces);
		this.faces.addAll(addFaces); 
	}

	/**
	 * Helper method to set up the PhysicsModel associated with this Model
	 * @param modelShape
	 * @param position
	 * @param rigidBodyProp
	 * @return
	 */
	private void setupPhysicsModel() {
		// Setup the physics object (@TODO: Support for other collision shapes)
		ObjectArrayList<javax.vecmath.Vector3f> modelShapePoints = new ObjectArrayList<>();
		
		for (Face face : faces) {
			for (VertexData vertex : face.getVertices()) {
				modelShapePoints.add(new javax.vecmath.Vector3f(vertex.getXYZ()));
			}
		}
		
		// Create and initialize the physics model
		CollisionShape modelShape = new ConvexHullShape(modelShapePoints);

		// Set up the model in the initial position
		MotionState modelMotionState = new DefaultMotionState(new Transform(new javax.vecmath.Matrix4f(new Quat4f(0, 0, 0, 1), 
				new javax.vecmath.Vector3f(initialPos.x, initialPos.y, initialPos.z), 
				1)));

		javax.vecmath.Vector3f modelInertia = new javax.vecmath.Vector3f();

		modelShape.calculateLocalInertia(1.0f, modelInertia);
		RigidBodyConstructionInfo modelConstructionInfo = new RigidBodyConstructionInfo(1.0f, modelMotionState, modelShape, modelInertia);

		// Retrieve the properties from the PhysicsModelProperties
		modelConstructionInfo.restitution = physicsProps.getProperty("restitution") == null ? Settings.getFloat("defaultRestitution") : (Float)physicsProps.getProperty("restitution");
		modelConstructionInfo.mass = physicsProps.getProperty("mass") == null ? Settings.getFloat("defaultMass") : (Float)physicsProps.getProperty("mass");
		modelConstructionInfo.angularDamping = physicsProps.getProperty("angularDamping") == null ? Settings.getFloat("defaultAngularDamping") : (Float)physicsProps.getProperty("angularDamping");
		modelConstructionInfo.linearDamping = physicsProps.getProperty("linearDamping") == null ? Settings.getFloat("defaultLinearDamping") : (Float)physicsProps.getProperty("linearDamping");
		modelConstructionInfo.friction = physicsProps.getProperty("friction") == null ? Settings.getFloat("defaultFriction") : (Float)physicsProps.getProperty("friction");

		RigidBody modelRigidBody = new RigidBody(modelConstructionInfo);
		modelRigidBody.setCollisionFlags((Integer) (physicsProps.getProperty("collisionFlags") == null ? modelRigidBody.getCollisionFlags() :
			physicsProps.getProperty("collisionFlags")));
		modelRigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

		physicsModel = new PhysicsModel(modelShape, modelRigidBody);
	}

	/**
	 * Encode a number into a colour
	 * @param num Number to encode (int)
	 * @return 
	 */
	private Vector3f encodeColour(int num) {
		int r = (num >> 16) & 0xFF;
		int g = (num >> 8) & 0xFF;
		int b = num & 0xFF;
		
		return new Vector3f((float)r / 255.0f, (float)g / 255.0f, (float)b / 255.0f);
	}
	
	/**
	 * Decode a colour into a number
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private int decodeColour(float x, float y, float z) {
		int red = (int)Math.ceil(x * 255);
		int green = (int)Math.ceil(y * 255);
		int blue = (int)Math.ceil(z * 255);
		
		int rgb = ((red & 0x0FF) << 16) | ((green & 0x0FF) << 8) | (blue & 0x0FF);
		return rgb;
	}

}
