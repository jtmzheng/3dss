package renderer.model;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderer.Renderable;
import util.MathUtils;
import util.Plane;

/**
 * Non-interactive model for the scene (can be a part of the Scene Graph)
 * @author maxz
 */

public class ModelScene extends Model {

	public ModelScene(List<Face> f) {
		super(f);
	}

	@Override
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix) {
		super.render(parentMatrix, viewMatrix);
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public List<Renderable> getChildren() {
		return null;
	}

	@Override
	public boolean isCullable(Matrix4f viewMatrix, Plane[] frustumPlanes) {
		return false;
	}

	@Override
	protected Matrix4f getModelMatrix(Matrix4f parentMatrix) {
		return Matrix4f.mul(modelMatrix, parentMatrix, null);
	}

	@Override
	public boolean isBound() {
		return isBound;
	}
	
	/**
	 * Translate the modelMatrix by a given vector
	 * @param s The displacement vector
	 */
	public void translate(Vector3f s) {
		Matrix4f.translate(s, modelMatrix, modelMatrix);
	}

	/**
	 * Rotate about the y-axis
	 * @param angle The angle to rotate by.
	 */
	public void rotateY(float angle){
		Matrix4f.rotate(angle, MathUtils.Y_AXIS, modelMatrix, modelMatrix);
	}

	/**
	 * Rotate about the x-axis
	 * @param angle The angle to rotate by.
	 */	
	public void rotateX(float angle){
		Matrix4f.rotate(angle, MathUtils.X_AXIS, modelMatrix, modelMatrix);
	}

	/**
	 * Rotate about the z-axis
	 * @param angle The angle to rotate by.
	 */
	@Override
	public void rotateZ(float angle){
		Matrix4f.rotate(angle, MathUtils.Z_AXIS, modelMatrix, modelMatrix);
	}

	/**
	 * Scale the ModelScene by a given vector.
	 * @param scale The scale vector to scale by.
	 */
	@Override
	public void scale(Vector3f scale){
		Matrix4f.scale(scale, modelMatrix, modelMatrix);
	}

}
