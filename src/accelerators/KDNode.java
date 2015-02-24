package accelerators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import renderer.Renderable;
import renderer.model.BoundingBox;
import renderer.model.Cullable;
import util.MathUtils;
import util.Plane;

public class KDNode implements Renderable {
	private int id;
	private KDNode parent;
	private boolean isBound;

	// KD trees are binary-space partitioned, each node has at most two children.
	private KDNode leftChild;
	private KDNode rightChild;
	
	// Objects that are a part of this level.
	private List<Cullable> objects;

	private int dimension;
	private int remainingLevels;
	private BoundingBox enclosure;

	public KDNode (BoundingBox enclosure, int remainingLevels, int dimension) {
		this.dimension = dimension;
		this.remainingLevels = remainingLevels;
		this.enclosure = enclosure;
		this.objects = new ArrayList<>();
	}

	@Override
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix) {
		// Render everything at this level
		for(Cullable obj : objects) {
			obj.render(parentMatrix, viewMatrix);
		}		
	}

	@Override
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix,
			Plane[] frustumPlanes) {
		if(isCullable(viewMatrix, parentMatrix, frustumPlanes)) {
			return;
		}

		// Render each child
		if (this.leftChild != null) 
			this.leftChild.render(parentMatrix, viewMatrix, frustumPlanes);
		if (this.rightChild != null)
			this.rightChild.render(parentMatrix, viewMatrix, frustumPlanes);

		// Render everything at this level
		for(Cullable obj : objects) {
			obj.render(parentMatrix, viewMatrix, frustumPlanes);
		}		
	}

	public void insert(Cullable obj) {
		if (this.remainingLevels == 0) {
			this.objects.add(obj);
			return;
		}
		int compare = compareToCullable(obj);
		
		// If the cullable overlaps the left and right halves, add it to the current list.
		if (compare == 0) {
			this.objects.add(obj);
			return;
		}
		
		if (compare == -1) {
			if (this.leftChild == null) {
				BoundingBox leftBBox = this.enclosure.bisectLeft(dimension);
				this.leftChild = new KDNode(leftBBox, this.remainingLevels - 1, (this.dimension + 1) % 3);
			}
			this.leftChild.insert(obj);
		} else {
			if (this.rightChild == null) {
				BoundingBox rightBBox = this.enclosure.bisectRight(dimension);
				this.rightChild = new KDNode(rightBBox, this.remainingLevels - 1, (this.dimension + 1) % 3);
			}
			this.rightChild.insert(obj);
		}
	}

	/**
	 * Determines if the Cullable belongs in the left half of this enclosure or the right half.
	 * This function returns zero if it overlaps both halves.
	 * @param obj
	 * @return
	 */
	private int compareToCullable(Cullable obj) {
		float minObj = obj.getCentre(dimension) - obj.getWidth(dimension) / 2;
		float maxObj = obj.getCentre(dimension) + obj.getWidth(dimension) / 2;
		float midPoint = this.enclosure.getCentre(dimension);
		
		if (minObj < midPoint && maxObj < midPoint) return -1;
		if (minObj > midPoint && maxObj > midPoint) return 1;
		return 0;
	}

	@Override
	public boolean bind() {
		if(isBound)
			return true;
		
		isBound = true;
		for(Cullable obj : objects) {
			isBound &= obj.bind();
		}
		
		if (leftChild != null) isBound &= leftChild.bind();
		if (rightChild != null) isBound &= rightChild.bind();

		return isBound;
	}

	@Override
	public boolean hasChildren() {
		return leftChild != null || rightChild != null;
	}

	@Override
	public List<Renderable> getChildren() {
		return Arrays.asList(new Renderable[] { leftChild, rightChild });
	}

	@Override
	public boolean isBound() {
		return isBound;
	}

	@Override
	public boolean isCullable(Matrix4f viewMatrix, Matrix4f parentMatrix,
			Plane[] frustumPlanes) {
		float[] bBox = this.enclosure.currentBounds();
		Vector4f[] corners = new Vector4f[bBox.length / 4];

		Matrix4f pvTransform = new Matrix4f();
		Matrix4f.mul(parentMatrix, viewMatrix, pvTransform);

		for(int j = 0; j < bBox.length; j += 4) {
			corners[j/4] = new Vector4f(bBox[j+0], bBox[j+1], bBox[j+2], bBox[j+3]);
			Matrix4f.transform(pvTransform, corners[j/4], corners[j/4]);
		}
		boolean outsidefrustum = true;
		for (int i = 0; i < frustumPlanes.length; i++) {
			for (int j = 0; j < bBox.length / 4; j++) {
				float dP = MathUtils.dotPlaneWithVector(frustumPlanes[i], corners[j]);
				outsidefrustum &= dP < 0f;
				if(outsidefrustum == false)
					break;
			}
			
			// If all the BBox points are on the other side of a frustum plane, cull it.
			if (outsidefrustum == true) 
				return true;
	
			outsidefrustum = true;
		}

		return false;
	}
}
