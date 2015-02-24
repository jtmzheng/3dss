package accelerators;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

import renderer.Renderable;
import renderer.model.Cullable;
import util.Plane;

// TODO:MZ Add constructor for OctNode that takes in a depth (recursive)
// TODO:MZ Add in support for dynamic resizing for OctTree;
// TODO:MZ Add in debugging flag for flat render
public class OctTree implements Renderable {
	private OctNode root;
	private long size;
	private List<Cullable> children;
	private int maxDepth;

	public OctTree(float x, float y, float z, float halfWidth, int maxDepth) {
		this.maxDepth = maxDepth;
		this.root = new OctNode(x, y, z, halfWidth, maxDepth);
		this.size = 1;
		this.children = new ArrayList<>();
	}
	
	@Override
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix) {
		root.render(parentMatrix, viewMatrix);
	}
	
	@Override
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix, Plane[] frustumPlanes) {
		root.render(parentMatrix, viewMatrix, frustumPlanes);
	}
	
	public boolean insert(Cullable obj) {
		if (obj == null)
			return false;

		root.insert(obj);
		children.add(obj);
		return true;
	}
	
	@Override
	public boolean bind() {
		root.bind();
		return isBound();
	}
	
	@Override
	public boolean hasChildren() {
		return size > 0;
	}
	
	@Override
	public List<Renderable> getChildren() {
		return new ArrayList<Renderable>(children);
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
