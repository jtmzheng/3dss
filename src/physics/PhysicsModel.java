package physics;

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
	public float[] getTransformMatrix() {
		Transform worldTransform = modelRigidBody.getWorldTransform(new Transform());
		float [] glMatrix = new float [16]; // 4 x 4 matrix
		worldTransform.getOpenGLMatrix(glMatrix);
		
		// System.out.println("Test" + Arrays.toString(glMatrix));
		return glMatrix;
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
