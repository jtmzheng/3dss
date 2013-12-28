package physics;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

/**
 * This class is used to interface between the physics engine and OpenGL.
 *
 * @author Max
 */
public class PhysicsMotionState extends MotionState{

	@SuppressWarnings("unused")
	private final Vector3f X_AXIS = new Vector3f(1, 0, 0);
	@SuppressWarnings("unused")
	private final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
	@SuppressWarnings("unused")
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
				
		transform = worldTrans;
		modelMatrix = convertMatToLWJGL(transform.getMatrix(new javax.vecmath.Matrix4f()));
	}
	
	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}
	
	private Matrix4f convertMatToLWJGL(javax.vecmath.Matrix4f tMat) {
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
}