package physics;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

public class PhysicsModel {

	@SuppressWarnings("unused")
	private final CollisionShape modelShape;
	private RigidBody modelRigidBody;
	
	/**
	 * Constructor for PhysicsModel takes a CollisionShape and a RigidBody
	 * @param modelShape
	 * @param modelRigidBody
	 */
	public PhysicsModel(CollisionShape modelShape,
			RigidBody modelRigidBody) {
		this.modelShape = modelShape;
		this.modelRigidBody = modelRigidBody;
		init();
	}
	
	/**
	 * Returns a float array representing a 4 x 4 OpenGL model matrix
	 * @return A float array of size 16
	 */
	public float[] getOpenGLTransformMatrix() {
		Transform worldTransform = modelRigidBody.getWorldTransform(new Transform());
		float [] glMatrix = new float [16]; // 4 x 4 matrix
		worldTransform.getOpenGLMatrix(glMatrix);
		
		return glMatrix;
	}
	
	/**
	 * Returns a LWJGL matrix of the transformation of the rigid body
	 * @return
	 */
	public Matrix4f getTransformMatrix() {
		Transform worldTransform = modelRigidBody.getWorldTransform(new Transform());
		javax.vecmath.Matrix4f tMat = worldTransform.getMatrix(new javax.vecmath.Matrix4f());
		Matrix4f tMatConv = new Matrix4f();
		
		// Copy the values over (transposed)
		tMatConv.m00 = tMat.m00;
		tMatConv.m01 = tMat.m10;
		tMatConv.m02 = tMat.m20;
		tMatConv.m03 = tMat.m30;
		tMatConv.m10 = tMat.m01;
		tMatConv.m11 = tMat.m11;
		tMatConv.m12 = tMat.m21;
		tMatConv.m13 = tMat.m31;		
		tMatConv.m20 = tMat.m02;
		tMatConv.m21 = tMat.m12;
		tMatConv.m22 = tMat.m22;
		tMatConv.m23 = tMat.m32;
		tMatConv.m30 = tMat.m03;
		tMatConv.m31 = tMat.m13;
		tMatConv.m32 = tMat.m23;
		tMatConv.m33 = tMat.m33;

		return tMatConv;
	}
	
	public RigidBody getRigidBody() {
		return modelRigidBody;
	}
	
	/**
	 * Apply a force on the model (gets applied on next world simulate)
	 * @param force The force vector that is to be applied on the model
	 */
	public void applyForce(Vector3f force) {
		javax.vecmath.Vector3f convertedVec = new javax.vecmath.Vector3f(force.x,
				force.y,
				force.z);
		applyForce(convertedVec);
	}
	
	/**
	 * Apply a force on the model (gets applied on next world simulate)
	 * @param force The force vector that is to be applied on the model
	 */
	public void applyForce(javax.vecmath.Vector3f force) {
		modelRigidBody.activate(true);
		modelRigidBody.applyCentralForce(force);
	}
		
	/**
	 * Translate the model
	 * @param vec The vector to translate the model by
	 */
	public void translate(javax.vecmath.Vector3f vec) {
		Transform worldTransform = modelRigidBody.getWorldTransform(new Transform());
		javax.vecmath.Matrix4f mat = worldTransform.getMatrix(new javax.vecmath.Matrix4f());
		mat.setTranslation(vec);
		worldTransform.set(mat);
		modelRigidBody.setWorldTransform(worldTransform);
	}
	
	/**
	 * Rotate about the y-axis.
	 * @param angle The angle to rotate by.
	 */
	public void rotateY(float angle) {
		Transform worldTransform = modelRigidBody.getWorldTransform(new Transform());
		javax.vecmath.Matrix4f mat = worldTransform.getMatrix(new javax.vecmath.Matrix4f());
		mat.rotY(angle);
		worldTransform.set(mat);
		modelRigidBody.setWorldTransform(worldTransform);
	}

	/**
	 * Rotate about the x-axis.
	 * @param angle The angle to rotate by
	 */	
	public void rotateX(float angle) {
		Transform worldTransform = modelRigidBody.getWorldTransform(new Transform());
		javax.vecmath.Matrix4f mat = worldTransform.getMatrix(new javax.vecmath.Matrix4f());
		mat.rotX(angle);
		worldTransform.set(mat);
		modelRigidBody.setWorldTransform(worldTransform);
	}

	/**
	 * Rotate about the z-axis.
	 * @param angle The angle to rotate by.
	 */
	public void rotateZ(float angle) {
		Transform worldTransform = modelRigidBody.getWorldTransform(new Transform());
		javax.vecmath.Matrix4f mat = worldTransform.getMatrix(new javax.vecmath.Matrix4f());
		mat.rotZ(angle);
		worldTransform.set(mat);
		modelRigidBody.setWorldTransform(worldTransform);
	}

	
	/**
	 * Stub for future initialization 
	 */
	private void init() {
		modelRigidBody.setFriction(100);
	}	
}
