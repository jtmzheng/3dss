package physics;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

public class PhysicsModel {

	@SuppressWarnings("unused")
	private final CollisionShape modelShape;
	private RigidBody modelRigidBody;
	
	public PhysicsModel(CollisionShape modelShape,
			RigidBody modelRigidBody) {
		this.modelShape = modelShape;
		this.modelRigidBody = modelRigidBody;
		init();
	}
	
	/**
	 * Returns a float array representing a 4 x 4 OpenGL model matrix
	 * @return
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
		
		// Copy the values over
		tMatConv.m00 = tMat.m00;
		tMatConv.m01 = tMat.m01;
		tMatConv.m02 = tMat.m02;
		tMatConv.m03 = tMat.m03;
		tMatConv.m10 = tMat.m10;
		tMatConv.m11 = tMat.m11;
		tMatConv.m12 = tMat.m12;
		tMatConv.m13 = tMat.m13;		
		tMatConv.m20 = tMat.m20;
		tMatConv.m21 = tMat.m21;
		tMatConv.m22 = tMat.m22;
		tMatConv.m23 = tMat.m23;
		tMatConv.m30 = tMat.m30;
		tMatConv.m31 = tMat.m31;
		tMatConv.m32 = tMat.m32;
		tMatConv.m33 = tMat.m33;

		return tMatConv;
	}
	
	public RigidBody getRigidBody() {
		return modelRigidBody;
	}
	
	/**
	 * Apply a force on the model (gets applied on next world simulate)
	 * @param force
	 */
	public void applyForce(Vector3f force) {
		javax.vecmath.Vector3f convertedVec = new javax.vecmath.Vector3f(force.x,
				force.y,
				force.z);
		applyForce(convertedVec);
	}
	
	/**
	 * Apply a force on the model (gets applied on next world simulate)
	 * @param force 
	 */
	public void applyForce(javax.vecmath.Vector3f force) {
		modelRigidBody.activate(true);
		modelRigidBody.applyCentralForce(force);
	}
	
	/**
	 * Stub for future initialization 
	 */
	private void init() {
		modelRigidBody.setFriction(100);
	}	
}
