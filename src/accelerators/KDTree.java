package accelerators;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

import renderer.Renderable;
import renderer.model.BoundingBox;
import renderer.model.Cullable;
import util.Plane;

/**
 * KD Trees are a binary space partitioning data structure (at each node in the
 * tree, the space is split into two portions). This implementation currently cycles
 * through which dimension to split on depending on what level the node is in.
 *
 * TODO(adi): Add a heuristic to help decide which axis to split on.
 * TODO(adi): Add nearest neighbor search.
 * TODO(adi): Perhaps have something that only splits a KDNode when the number of objects
 * 			  it contains is greater than a certain amount.
 * @author Adi
 */
public class KDTree implements Renderable {
	private KDNode root;
	private List<Cullable> children;
	private static int DEFAULT_DEPTH = 15;

	public KDTree (BoundingBox bBox) {
		root = new KDNode(bBox, DEFAULT_DEPTH, 0);
		this.children = new ArrayList<>();
	}

	public KDTree (BoundingBox bBox, int maxDepth) {
		root = new KDNode(bBox, maxDepth, 0);
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
	
	public boolean insertAll(List<Cullable> objs) {
		if (objs.size() == 0) 
			return true;
	
		boolean ret = true;
		for (Cullable obj : objs) {
			if (obj != null) {
				ret &= insert(obj);
			}
		}
		return ret;
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
		return this.children.size() > 0;
	}

	@Override
	public List<Renderable> getChildren() {
		return new ArrayList<Renderable>(this.children);
	}

	@Override
	public boolean isBound() {
		return root.isBound();
	}

	@Override
	public boolean isCullable(Matrix4f viewMatrix, Matrix4f parentMatrix,
			Plane[] frustumPlanes) {
		return root.isCullable(viewMatrix, parentMatrix, frustumPlanes);
	}

}
