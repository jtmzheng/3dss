package renderer.model;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

import renderer.Renderable;
import util.Plane;

/**
 * Non-interactive model for the scene (can be a part of the Scene Graph)
 * @author maxz
 */

public class ModelScene extends Model {

	@Override
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean bind() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Renderable> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBound() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCullable(Matrix4f viewMatrix, Plane[] frustumPlanes) {
		// TODO Auto-generated method stub
		return false;
	}

}
