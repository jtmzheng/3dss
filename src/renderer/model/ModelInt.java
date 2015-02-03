package renderer.model;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.vecmath.Quat4f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
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
import texture.Material;
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
	 * ModelInt ignores any parents orientation, transform matrix is derived from Bullet
	 * @param parentMatrix
	 * @return
	 */
	@Override
	public Matrix4f getModelMatrix(Matrix4f parentMatrix) {
		return physicsModel.getTransformMatrix();
	}
	
	/**
	 * Render a ModelInt that has already been set up
	 * @TODO: Make a class for the HashMaps (a struct) - will keep it cleaner
	 */
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix) {
		super.render(parentMatrix, viewMatrix);
		if(!renderFlag)
			return;

		// @TODO: Get picking working or remove code
		if(isPicked) {
			GL20.glUniform1i(ShaderController.getSelectedModelLocation(), 1);
		} else {
			GL20.glUniform1i(ShaderController.getSelectedModelLocation(), 0);
		}
	}
	
	/**
	 * Render a ModelInt that has already been set up
	 * @TODO: Make a class for the HashMaps (a struct) - will keep it cleaner
	 */
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix, Plane[] frustumPlanes) {
		super.render(parentMatrix, viewMatrix, frustumPlanes);
		if(!renderFlag)
			return;

		// @TODO: Get picking working or remove code
		if(isPicked) {
			GL20.glUniform1i(ShaderController.getSelectedModelLocation(), 1);
		} else {
			GL20.glUniform1i(ShaderController.getSelectedModelLocation(), 0);
		}
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
	@Override
	public void translate(Vector3f s) {
		physicsModel.translate(new javax.vecmath.Vector3f(s.x,
				s.y,
				s.z));
	}

	/**
	 * Rotate about the y-axis
	 * @param angle The angle to rotate by.
	 */
	@Override
	public void rotateY(float angle){
		physicsModel.rotateY(angle);
	}

	/**
	 * Rotate about the x-axis
	 * @param angle The angle to rotate by.
	 */
	@Override
	public void rotateX(float angle){
		physicsModel.rotateX(angle);
	}

	/**
	 * Rotate about the z-axis
	 * @param angle The angle to rotate by.
	 */
	@Override
	public void rotateZ(float angle){
		physicsModel.rotateZ(angle);
	}

	/**
	 * Scale the ModelInt by a given vector.
	 * @param scale The scale vector to scale by.
	 * @deprecated
	 */
	@Override
	public void scale(Vector3f scale){
		Matrix4f.scale(scale, modelMatrix, modelMatrix);
	}

	public float[] getModelMatrixBuffer() {
		return physicsModel.getOpenGLTransformMatrix();
	}

	/**
	 * Get the physics ModelInt associated with this ModelInt.
	 * @return
	 */
	public PhysicsModel getPhysicsModel() {
		return physicsModel;
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
