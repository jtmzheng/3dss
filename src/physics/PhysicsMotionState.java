package physics;

import javax.vecmath.Quat4f;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

/**
 * This class is used to interface between the physics engine and OpenGL
 * @author Max
 */
public class PhysicsMotionState extends MotionState{

	private final Vector3f X_AXIS = new Vector3f(1, 0, 0);
	private final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
	private final Vector3f Z_AXIS = new Vector3f(0, 0, 1);

	private Matrix4f modelMatrix;
	private Transform transform;
	
	public PhysicsMotionState(Transform transform,
			Matrix4f modelMatrix) {
		this.transform = transform;
		this.modelMatrix = modelMatrix;
	}
	
	@Override
	public Transform getWorldTransform(Transform worldTrans) {
		return transform;
	}

	@Override
	public void setWorldTransform(Transform worldTrans) {
		if(worldTrans == null)
			return;
		
		modelMatrix.setIdentity();
		Quat4f rot = worldTrans.getRotation(new Quat4f());
		modelMatrix.rotate(rot.x, X_AXIS);
		modelMatrix.rotate(rot.y, Y_AXIS);
		modelMatrix.rotate(rot.z, Z_AXIS);	
		javax.vecmath.Vector3f pos = worldTrans.origin;
		Matrix4f.translate(new Vector3f(pos.x,
				pos.y,
				pos.z),
				modelMatrix,
				modelMatrix);
	}

}
