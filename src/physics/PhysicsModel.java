package physics;

import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

public class PhysicsModel {

	@SuppressWarnings("unused")
	private final ConvexHullShape modelShape;
	private RigidBody modelRigidBody;
	
	public PhysicsModel(ConvexHullShape modelShape,
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
		Transform worldTransform = modelRigidBody.getWorldTransform(null);
		float [] glMatrix = new float [16]; // 4 x 4 matrix
		worldTransform.getOpenGLMatrix(glMatrix);
		
		System.out.println(glMatrix);
		return glMatrix;
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
	
	private void init() {
		
	}	
}
