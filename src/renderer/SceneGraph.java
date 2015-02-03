package renderer;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

import util.Plane;

/** 
 * Scene graph (tree?) for hierararchal ordering
 * @author maxz
 */
public class SceneGraph implements Renderable {
	private Renderable root;
	
	public SceneGraph(Renderable root) {
		this.root = root;
	}


	@Override
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix) {
		root.render(parentMatrix, viewMatrix);
	}
	
	@Override
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix, Plane[] frustumPlanes) {
		root.render(parentMatrix, viewMatrix, frustumPlanes);
	}

	@Override
	public boolean bind() {
		return root.bind();
	}

	@Override
	public boolean hasChildren() {
		return root.hasChildren();
	}

	@Override
	public List<Renderable> getChildren() {
		return root.getChildren();
	}

	@Override
	public boolean isBound() {
		return root.isBound();
	}

	@Override
	public boolean isCullable(Matrix4f viewMatrix, Matrix4f parentMatrix, Plane[] frustumPlanes) {
		return root.isCullable(viewMatrix, parentMatrix, frustumPlanes);
	}
}
