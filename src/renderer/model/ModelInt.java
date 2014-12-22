package renderer.model;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
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
import org.lwjgl.util.vector.Vector4f;

import physics.PhysicsModel;
import physics.PhysicsModelProperties;
import renderer.Renderable;
import renderer.light.Light;
import renderer.light.LightHandle;
import renderer.shader.ShaderController;
import system.Settings;
import texture.Material;
import texture.Texture;
import texture.TextureManager;
import util.ColourUtils;
import util.MathUtils;
import util.Plane;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;

/**
 * ModelInt class is an abstraction used by both the renderer and the physics engine. Each ModelInt represents a physical object
 * in the environment. The OpenGL attributes will be passed as an interleaved VBO. Changes are applied to the physics ModelInt.
 *
 * @TODO: This class needs cleanup.
 * @TODO: Dynamically assigning attributes for each ModelInt
 * @TODO: Each ModelInt should basically be given a shader program that it will use to render
 * @author Max
 * @author Adi
 */
public class ModelInt extends Model {
	// Unique ID for the ModelInt (used for picking)
	private final int uniqueId;
	private final Vector3f uniqueIdColour;

	// Physics ModelInt
	private PhysicsModel physicsModel;
	private PhysicsModelProperties physicsProps;

	// If the ModelInt is currently being picked.
	private boolean isPicked = false;

	/**
	 * Merges the meshes of two models and returns the merged ModelInt.
	 * Ignores the physics ModelInt properties of the two and uses the defaults. If custom
	 * physics properties are required, please use the other merge method.
	 * @param a
	 * @param b
	 * @param transform
	 * @return the merged ModelInt
	 */
	public static ModelInt merge (ModelInt a, ModelInt b, boolean transform) {
		return ModelInt.merge(a, b, new PhysicsModelProperties(), transform);
	}

	public static ModelInt merge (ModelInt a, ModelInt b) {
		return ModelInt.merge(a, b, new PhysicsModelProperties(), true);
	}

	/**
	 * Merges the meshes of two models and returns the merged ModelInt, with custom
	 * physics properties.
	 * @param a The first ModelInt.
	 * @param b The second ModelInt.
	 * @param props Custom physics ModelInt properties.
	 * @param transform Whether the models should be transformed in world space first
	 * @return the merged ModelInt
	 */
	public static ModelInt merge (ModelInt a, ModelInt b, PhysicsModelProperties props, boolean transform) {
		List<Face> mergedList = new ArrayList<Face>();

		if(transform) {
			Matrix4f mMatrixA = a.getPhysicsModel().getTransformMatrix();
			Matrix4f mMatrixB = b.getPhysicsModel().getTransformMatrix();

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
		} else {
			mergedList.addAll(a.getFaceList());
			mergedList.addAll(b.getFaceList());
		}

		return new ModelInt(mergedList, props);
	}

	/**
	 * Merges the meshes of a list of models and returns the merged ModelInt.
	 * Ignores the physics ModelInt properties of the models in the list and uses the defaults.
	 * If custom physics properties are required, please use the other merge method.
	 * @param modelList the list of models to merge
	 * @return the merged ModelInt
	 */
	public static ModelInt merge (List<ModelInt> modelList) {
		return ModelInt.merge(modelList, new PhysicsModelProperties());
	}

	/**
	 * Merges the meshes of a list of models and returns the merged ModelInt, with custom
	 * physics properties.
	 * @param modelList the list of models to merge
	 * @return the merged ModelInt
	 */
	public static ModelInt merge (List<ModelInt> modelList, PhysicsModelProperties props) {
		if (modelList.size() <= 1) {
			throw new IllegalArgumentException("Requires a list of size greater than one.");
		}

		ModelInt mergedModel = merge(modelList, 0, modelList.size() - 1, props);

		return mergedModel;
	}

	/**
	 * Divide and conquer the task of merging
	 * @param modelList
	 * @param i
	 * @param j
	 * @param props
	 * @return
	 */
	private static ModelInt merge(List<ModelInt> modelList, int i, int j, PhysicsModelProperties props) {
		if(i >= j) {
			return modelList.get(i); 
		} else {
			ModelInt a = merge(modelList, i, i + (j - i) / 2, props);
			ModelInt b = merge(modelList, i + (j - i) / 2 + 1, j, props);
			return i - j > 1 ? merge(a, b, props, false) : merge(a, b, props, true);
		}
	}

	/**
	 * Constructs a ModelInt (a representation of a 3D object).
	 * @param f The list of faces that make up the ModelInt.
	 * @param pos The initial position of the ModelInt.
	 * @param ld The diffuse light intensity.
	 * @param ls The specular light intensity.
	 * @param la The ambient light intensity.
	 * @param rigidBodyProp Custom physics properties this ModelInt should have.
	 */
	public ModelInt(List<Face> f, 
			Vector3f pos, 
			Vector3f ld, 
			Vector3f ls, 
			Vector3f la, 
			PhysicsModelProperties rigidBodyProp) {
		super(f, pos, ld, ls, la);
		
		this.physicsProps = rigidBodyProp;

		// Set the ID to the hash code
		uniqueIdColour = ColourUtils.encodeColour(hashCode());
		uniqueId = ColourUtils.decodeColour(uniqueIdColour.x, uniqueIdColour.y, uniqueIdColour.z);
		setupPhysicsModel();
	}

	/**
	 * Constructs a ModelInt (a representation of a 3D object).
	 * @param f The list of faces that make up the ModelInt.
	 * @param pos The initial position of the ModelInt.
	 * @param rigidBodyProp Custom physics properties this ModelInt should have.
	 */
	public ModelInt(List<Face> f,
			Vector3f pos,
			PhysicsModelProperties rigidBodyProp){
		super(f, pos);
		this.physicsProps = rigidBodyProp;
		
		// Set the ID to the hash code
		uniqueIdColour = ColourUtils.encodeColour(hashCode());
		uniqueId = ColourUtils.decodeColour(uniqueIdColour.x, uniqueIdColour.y, uniqueIdColour.z);
		setupPhysicsModel();
	}

	/**
	 * Constructs a ModelInt (a representation of a 3D object).
	 * @param f The list of faces that make up the ModelInt.
	 * @param rigidBodyProp Custom physics properties this ModelInt should have.
	 */
	public ModelInt(List<Face> f, PhysicsModelProperties rigidBodyProp){
		super(f);
		this.physicsProps = rigidBodyProp;

		// Set the ID to the hash code
		uniqueIdColour = ColourUtils.encodeColour(hashCode());
		uniqueId = ColourUtils.decodeColour(uniqueIdColour.x, uniqueIdColour.y, uniqueIdColour.z);
		setupPhysicsModel();
	}

	/**
	 * Constructs a ModelInt (a representation of a 3D object). This constructor
	 * uses default physicsmodel properties.
	 * @param f
	 */
	public ModelInt(List<Face> f) {
		super(f);
		this.physicsProps = new PhysicsModelProperties();

		// Set the UID to the hash code
		uniqueIdColour = ColourUtils.encodeColour(hashCode());
		uniqueId = ColourUtils.decodeColour(uniqueIdColour.x, uniqueIdColour.y, uniqueIdColour.z);
		setupPhysicsModel();
	}
	
	/**
	 * Copy constructor
	 * @param ModelInt ModelInt to copy
	 * @param position Initial position of copy
	 */
	public ModelInt(ModelInt model, Vector3f position) {
		super(model, position);
		this.physicsProps = new PhysicsModelProperties(model.getPhysicsProperties());
		
		// Set the UID to the hash code
		uniqueIdColour = ColourUtils.encodeColour(hashCode());
		uniqueId = ColourUtils.decodeColour(uniqueIdColour.x, uniqueIdColour.y, uniqueIdColour.z);
		setupPhysicsModel();
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

		return isBound;
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

			/**
			 * The code below is more efficient, but worse results (using BB for color based picking)
			 */
			
			/*
			GL11.glEnable(GL31.GL_PRIMITIVE_RESTART_INDEX); 
			GL31.glPrimitiveRestartIndex(BoundingBox.PRIMITIVE_RESTART_INDEX);
			GL30.glBindVertexArray(boundBox.getVAO());
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, boundBox.getVBOInd());
			GL11.glDrawElements(GL11.GL_TRIANGLE_STRIP, BoundingBox.INDICES.length, GL11.GL_UNSIGNED_INT, 0);
			GL11.glDisable(GL31.GL_PRIMITIVE_RESTART_INDEX);
			*/ 

			/* 
			 * The code below is more accurate for picking, but slower 
			 */


			// Do bind and draw for each material's faces
			for(Material material : mapMaterialToFaces.keySet()) {
				GL30.glBindVertexArray(mapVAOIds.get(material));

				// Bind to the index VBO that has all the information about the order of the vertices
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mapVBOIndexIds.get(material));

				// Draw the vertices
				GL11.glDrawElements(GL11.GL_TRIANGLES, mapIndiceCount.get(material), GL11.GL_UNSIGNED_INT, 0);
			}
		}
	}

	/**
	 * Render a ModelInt that has already been set up
	 * @TODO: Make a class for the HashMaps (a struct) - will keep it cleaner
	 */
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix) {
		if(!renderFlag)
			return;

		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		modelMatrix = physicsModel.getTransformMatrix();
		modelMatrix.store(buffer);
		buffer.flip();

		GL20.glUniformMatrix4(ShaderController.getModelMatrixLocation(), false, buffer);
		
		//TODO(MZ): If not orthogonal (ie, scale) need Matrix4f.transpose(Matrix4f.invert(Matrix4f.mul(viewMatrix, modelMatrix, null), null), null);
		Matrix4f normMatrix = Matrix4f.mul(viewMatrix, modelMatrix, null); 
		normMatrix.store(buffer);
		buffer.flip();

		GL20.glUniformMatrix4(ShaderController.getNormalMatrixLocation(), false, buffer);

		// If ModelInt is picked change the colour
		if(isPicked) {
			GL20.glUniform1i(ShaderController.getSelectedModelLocation(), 1);
		} else {
			GL20.glUniform1i(ShaderController.getSelectedModelLocation(), 0);
		}

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
	 * Checks if the model should be culled or not
	 * @param viewMatrix
	 * @param frustumPlanes
	 * @return
	 */
	public boolean isCullable(Matrix4f viewMatrix, Plane[] frustumPlanes) {
		if(!this.enableCulling)
			return false;
		
		float[] pts = this.getBoundingBox().getVertexList();
		Vector4f[] transformedPts = new Vector4f[8];
		Matrix4f tMat = this.getPhysicsModel().getTransformMatrix();

		for (int i = 0; i < pts.length; i+=4) {
			Vector4f mPt = new Vector4f(pts[i], pts[i+1], pts[i+2], pts[i+3]);
			Matrix4f.transform(tMat, mPt, mPt);
			Matrix4f.transform(viewMatrix, mPt, mPt);
			transformedPts[i/4] = mPt;
		}
		
		boolean outsidefrustum = true;
		for (int i = 0; i < frustumPlanes.length; i++) {
			for (int j = 0; j < transformedPts.length; j++) {
				float dP = MathUtils.dotPlaneWithVector(frustumPlanes[i], transformedPts[j]);
				outsidefrustum &= dP < 0f;
			}
			if (outsidefrustum == true) return true;
			outsidefrustum = false;
		}
		
		return false;
	}

	/**
	 * Apply a force on the ModelInt 
	 * @param force
	 */
	public void applyForce(Vector3f force) {
		physicsModel.applyForce(force);
	}

	/**
	 * Translate the ModelInt by a given vector
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
	 * Scale the ModelInt by a given vector.
	 * @param scale The scale vector to scale by.
	 * @deprecated
	 */
	public void scale(Vector3f scale){
		Matrix4f.scale(scale, modelMatrix, modelMatrix);
	}

	public float[] getModelMatrixBuffer() {
		return physicsModel.getOpenGLTransformMatrix();
	}
	
	public Matrix4f getModelMatrix() {
		return physicsModel.getTransformMatrix();
	}

	/**
	 * Get the physics ModelInt associated with this ModelInt.
	 * @return
	 */
	public PhysicsModel getPhysicsModel() {
		return physicsModel;
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
	 * Returns the physics properties that this ModelInt has.
	 * @return the physics properties of the ModelInt
	 */
	public PhysicsModelProperties getPhysicsProperties () {
		return physicsProps;
	}

	/**
	 * Get the unique ID of the ModelInt
	 * @return uniqueId 
	 */
	public int getUID() {
		return uniqueId;
	}

	/**
	 * Returns if the ModelInt is set up for rendering
	 * @return isBound
	 */
	public boolean isBound() {
		return isBound;
	}

	/**
	 * Set flag for whether this ModelInt should be rendered
	 * @param renderFlag
	 */
	public void setRenderFlag(boolean renderFlag) {
		this.renderFlag = renderFlag;
	}

	/**
	 * Get the origin of the ModelInt
	 * @return
	 */
	public javax.vecmath.Vector3f getModelOrigin() {
		return physicsModel.getRigidBody().getWorldTransform(new Transform()).origin;
	}

	/**
	 * Add a light to this ModelInt 
	 * @param light
	 */
	public void addLight(Light light) {
		if(mLightHandle != null) {
			mLightHandle.invalidate();
		}

		mLightHandle = new LightHandle(this, light);
	}

	/**
	 * Resets the ModelInt kinematics
	 */
	public void resetModelKinematics() {
		physicsModel.getRigidBody().setAngularVelocity(new javax.vecmath.Vector3f());
		physicsModel.getRigidBody().setLinearVelocity(new javax.vecmath.Vector3f());
	}

	/**
	 * Resets the ModelInt forces
	 */
	public void resetModelForces() {
		physicsModel.getRigidBody().clearForces();
	}

	/**
	 * Remove the light associated with this ModelInt
	 */
	public void removeLight() {
		if(mLightHandle != null) {
			mLightHandle.invalidate();
		}
	}

	/**
	 * Sets the flag for if the ModelInt is currently picked or not.
	 */
	public void setPickedFlag(boolean picked) {
		isPicked = picked;
	}

	/**
	 * Sets the flag for frustrum culling for this ModelInt.
	 */
	public void setFrustrumCulling(boolean cull) {
		enableCulling = cull;
	}

	/**
	 * Helper method to set up the PhysicsModel associated with this ModelInt
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

		// Create and initialize the physics ModelInt.
		ConvexShape modelShape = new ConvexHullShape(modelShapePoints);

		// TODO: Optimize convex hull shape by removing unnecessary vertices.
		// See http://www.bulletphysics.org/mediawiki-1.5.8/index.php/BtShapeHull_vertex_reduction_utility.
		// The issue is that this simplification takes quite a while.

		// Set up the ModelInt in the initial position
		MotionState modelMotionState = new DefaultMotionState(new Transform(new javax.vecmath.Matrix4f(new Quat4f(0, 0, 0, 1), 
				new javax.vecmath.Vector3f(initialPos.x, initialPos.y, initialPos.z), 
				1)));

		javax.vecmath.Vector3f modelInertia = new javax.vecmath.Vector3f();

		modelShape.calculateLocalInertia(1.0f, modelInertia);
		RigidBodyConstructionInfo modelConstructionInfo = new RigidBodyConstructionInfo(1.0f, modelMotionState, modelShape, modelInertia);

		// Retrieve the properties from the PhysicsModelProperties
		modelConstructionInfo.restitution = physicsProps.getProperty("restitution") == null ? settings.get("physics", "defaultRestitution", float.class) : (Float)physicsProps.getProperty("restitution");
		modelConstructionInfo.mass = physicsProps.getProperty("mass") == null ? settings.get("physics", "defaultMass", float.class) : (Float)physicsProps.getProperty("mass");
		modelConstructionInfo.angularDamping = physicsProps.getProperty("angularDamping") == null ? settings.get("physics", "defaultAngularDamping", float.class) : (Float)physicsProps.getProperty("angularDamping");
		modelConstructionInfo.linearDamping = physicsProps.getProperty("linearDamping") == null ? settings.get("physics", "defaultLinearDamping", float.class) : (Float)physicsProps.getProperty("linearDamping");
		modelConstructionInfo.friction = physicsProps.getProperty("friction") == null ? settings.get("physics", "defaultFriction", float.class) : (Float)physicsProps.getProperty("friction");

		RigidBody modelRigidBody = new RigidBody(modelConstructionInfo);
		modelRigidBody.setCollisionFlags((Integer) (physicsProps.getProperty("collisionFlags") == null ? modelRigidBody.getCollisionFlags() :
			physicsProps.getProperty("collisionFlags")));
		modelRigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

		physicsModel = new PhysicsModel(modelShape, modelRigidBody);
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public List<Renderable> getChildren() {
		return Collections.emptyList();
	}
}
