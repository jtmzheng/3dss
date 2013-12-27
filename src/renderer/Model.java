package renderer;

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
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import physics.PhysicsModel;
import physics.PhysicsModelProperties;
import texture.Material;
import texture.Texture;
import texture.TextureManager;

import com.bulletphysics.collision.dispatch.CollisionFlags;
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
 * Model class is an abstraction used by Renderer. Each model represents a physical object
 * in the environment. The OpenGL attributes will be passed as an interleaved VBO.
 * @author Max
 * @author Adi
 */
public class Model {
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

	public Model(List<Face> f, 
			Vector3f pos, 
			Vector3f ld, 
			Vector3f ls, 
			Vector3f la, 
			PhysicsModelProperties rigidBodyProp){
		
		this.faces = f;

		// Get instance of texture manager
		texManager = TextureManager.getInstance();

		// Setup the model 
		setup(pos, rigidBodyProp);

		// Transform
		this.translate(pos);

		// Setup the light associated with this model
		mLightHandle = new LightHandle(this, new Light(pos, ls, ld, la, null));
	}

	public Model(List<Face> f,
			Vector3f pos,
			PhysicsModelProperties rigidBodyProp){
		
		this.faces = f;
		
		// Get instance of texture manager
		texManager = TextureManager.getInstance();

		// Setup the model 
		setup(pos, rigidBodyProp);

		// Transform
		// this.translate(pos);
	}

	/**
	 * Creates a model with a list of faces.
	 * @param f The list of faces.
	 */
	public Model(List<Face> f,
			PhysicsModelProperties rigidBodyProp){
		
		this.faces = f;
		
		// Get instance of texture manager
		texManager = TextureManager.getInstance();

		setup(new Vector3f(0, 0, 0), rigidBodyProp);
	}


	/**
	 * Common setup for constructor
	 * @param f
	 * @param initialPosition
	 * @param rigidBodyProp
	 */
	public void setup(Vector3f initialPosition, PhysicsModelProperties rigidBodyProp){
		long curTime = System.currentTimeMillis();

		// Strip any quads / polygons. 
		this.triangulate();

		// Setup the physics object (@TODO: Support for other collision shapes)
		ObjectArrayList<javax.vecmath.Vector3f> modelShapePoints = new ObjectArrayList<>();

		// Split face list into a list of face lists, each having their own material.
		mapMaterialToFaces = new HashMap<>();
		
		// Set up HashMaps
		mapVAOIds = new HashMap<>();
		mapVBOIndexIds = new HashMap<>();
		mapIndiceCount = new HashMap<>();
		
		Material currentMaterial = null;

		// Split the faces up by material
		for (Face face : this.faces) {
			currentMaterial = face.getMaterial();

			// If already in the map append to the list (else make new entry)
			if(mapMaterialToFaces.containsKey(currentMaterial)) {
				List<Face> faceList = mapMaterialToFaces.get(currentMaterial);
				faceList.add(face);
			}
			else {
				List<Face> faceList = new ArrayList<>();
				faceList.add(face);
				mapMaterialToFaces.put(currentMaterial, faceList);
			}
		}

		// System.out.println("Number of face lists by material: " +  mapMaterialToFace.size());

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

			/** For each face in the list, process the data and add to 
			 *  the byte buffer.
			 */
			for(Face face: materialFaces){			
				//Add first vertex of the face			
				tempVertexData = face.faceData.get(0);
				if(!vboIndexMap.containsKey(tempVertexData)){
					vboIndexMap.put(tempVertexData, index);
					verticesFloatBuffer.put(tempVertexData.getElements());
					vboIndex.add(index++);
				}
				else{
					vboIndex.add(vboIndexMap.get(tempVertexData));
					modelShapePoints.add(new javax.vecmath.Vector3f(tempVertexData.getXYZ()));
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
					modelShapePoints.add(new javax.vecmath.Vector3f(tempVertexData.getXYZ()));
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
					modelShapePoints.add(new javax.vecmath.Vector3f(tempVertexData.getXYZ()));
				}

			}

			//Create VBO Index buffer
			verticesFloatBuffer.flip();
			int [] indices = new int[vboIndex.size()];
			int indicesCount = vboIndex.size();
			mapIndiceCount.put(material, indicesCount);

			for(int i = 0; i < vboIndex.size(); i++){
				indices[i] = vboIndex.get(i); 
			}

			IntBuffer indicesBuffer = BufferUtils.createIntBuffer(vboIndex.size());
			indicesBuffer.put( indices );
			indicesBuffer.flip();

			// Create a new Vertex Array Object in memory and select it (bind)
			int vaoID = GL30.glGenVertexArrays();
			mapVAOIds.put(material, vaoID);
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
		
		//Initialize model matrix (Initialized to the identity in the constructor)
		modelMatrix = new Matrix4f(); 
		
		// Create and initialize the physics model
		ConvexHullShape modelShape = new ConvexHullShape(modelShapePoints);
		physicsModel = setupPhysicsModel(modelShape, initialPosition, rigidBodyProp);
	
		renderFlag = true;
		
		System.out.println("Model loading to GPU: " + (System.currentTimeMillis() - curTime));
	}
	
	/**
	 * Render a model that has already been set up
	 * @TODO: Make a class for the HashMaps (a struct) - will keep it cleaner
	 */
	public void render() {
		if(renderFlag) {
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
				GL20.glEnableVertexAttribArray(0); //position
				GL20.glEnableVertexAttribArray(1); //color
				GL20.glEnableVertexAttribArray(2); //texture
				GL20.glEnableVertexAttribArray(3); //normal
				GL20.glEnableVertexAttribArray(4);
				GL20.glEnableVertexAttribArray(5);
				GL20.glEnableVertexAttribArray(6);

				// Bind to the index VBO that has all the information about the order of the vertices
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mapVBOIndexIds.get(material));

				// Draw the vertices
				GL11.glDrawElements(GL11.GL_TRIANGLES, mapIndiceCount.get(material), GL11.GL_UNSIGNED_INT, 0);
			}
		}
	}

	/**
	 * Translate the model by a given vector.
	 * @param s The translation vector.
	 * @deprecated
	 */
	public void translate(Vector3f s) {
		Matrix4f.translate(s, modelMatrix, modelMatrix);
	}
	
	public void applyForce(Vector3f force) {
		physicsModel.applyForce(force);
	}

	/**
	 * Rotate Y axis.
	 * @param angle The angle to rotate by.
	 * @deprecated
	 */
	public void rotateY(float angle){
		Matrix4f.rotate(angle, new Vector3f(0f, 1f, 0f), modelMatrix, modelMatrix);
	}

	/**
	 * Rotate X axis.
	 * @param angle The angle to rotate by.
	 * @deprecated
	 */	
	public void rotateX(float angle){
		Matrix4f.rotate(angle, new Vector3f(1f, 0f, 0f), modelMatrix, modelMatrix);
	}

	/**
	 * Rotate Z axis.
	 * @param angle The angle to rotate by.
	 * @deprecated
	 */
	public void rotateZ(float angle){
		Matrix4f.rotate(angle, new Vector3f(0f, 0f, 1f), modelMatrix, modelMatrix);
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
		return physicsModel.getTransformMatrix();
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
	
	/**
	 * Helper method to set up the PhysicsModel associated with this Model
	 * @param modelShape
	 * @param position
	 * @param rigidBodyProp
	 * @return
	 */
	protected PhysicsModel setupPhysicsModel(CollisionShape modelShape,
			Vector3f position,
			PhysicsModelProperties rigidBodyProp) {
		
		// Set up the model in the initial position
        MotionState modelMotionState = new DefaultMotionState(new Transform(new javax.vecmath.Matrix4f(new Quat4f(0, 0, 0, 1), 
        		new javax.vecmath.Vector3f(position.x, position.y, position.z), 
        		1)));
        
        javax.vecmath.Vector3f modelInertia = new javax.vecmath.Vector3f();
        
        modelShape.calculateLocalInertia(1.0f, modelInertia);
        RigidBodyConstructionInfo modelConstructionInfo = new RigidBodyConstructionInfo(1.0f, modelMotionState, modelShape, modelInertia);
        
        // Retrieve the properties from the PhysicsModelProperties
        modelConstructionInfo.restitution = rigidBodyProp.getProperty("restitution") == null ? 0.5f : (Float)rigidBodyProp.getProperty("restitution");
        modelConstructionInfo.angularDamping = rigidBodyProp.getProperty("angularDamping") == null ? 0.95f : (Float)rigidBodyProp.getProperty("angularDamping");
        modelConstructionInfo.mass = rigidBodyProp.getProperty("mass") == null ? 100 : (Float)rigidBodyProp.getProperty("mass");
        
        RigidBody modelRigidBody = new RigidBody(modelConstructionInfo);
        modelRigidBody.setCollisionFlags((Integer) (rigidBodyProp.getProperty("collisionFlags") == null ? modelRigidBody.getCollisionFlags() :
        	rigidBodyProp.getProperty("collisionFlags")));
        modelRigidBody.setDamping((Float) (rigidBodyProp.getProperty("damping") == null ? 0f : rigidBodyProp.getProperty("damping")),
        						  (Float) (rigidBodyProp.getProperty("damping") == null ? 0f : rigidBodyProp.getProperty("damping")));
        modelRigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        PhysicsModel model = new PhysicsModel(modelShape, 
        		modelRigidBody);
        
        return model;
	}
}