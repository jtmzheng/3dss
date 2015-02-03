package spatial;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import renderer.Renderable;
import renderer.model.Cullable;
import util.MathUtils;
import util.Plane;

/***
 * TODO: Needs an update function
 * @author Max
 *
 */
public class OctNode implements Renderable {
	private static final int CAPACITY = 8;	
	private static final int DIM = 3;
	private OctNode[] children;
	private int count;
	private List<Cullable> objects;
	private boolean isBound;
	
	// Max Depth of Subtrees
	private final int maxDepth;
	
	// Coordinates and half width of node
	private float x;
	private float y;
	private float z;
	private float[] pos;
	
	// Half the width of the current volume (Note: seems that canonically, this is half the width of the child)
	private float halfWidth;
	
	public OctNode(float x, float y, float z, float halfWidth, int maxDepth) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.pos = new float[] {x, y, z};
		this.halfWidth = halfWidth;
		this.children = new OctNode[CAPACITY];
		this.maxDepth = maxDepth;
		this.count = 0;
		this.isBound = false;
		this.objects = new ArrayList<>();
		
		// Initialize the children to null
		for(int i = 0; i < CAPACITY; ++i)
			this.children[i] = null;
	}

	private static OctNode createChild(OctNode parent, int index) {
		float halfWidth = parent.halfWidth / 2;
		float x = ((index & 1) > 0 ? halfWidth : -halfWidth);
		float y = ((index & 2) > 0 ? halfWidth : -halfWidth);
		float z = ((index & 4) > 0 ? halfWidth : -halfWidth);
		return new OctNode(parent.x + x, parent.y + y, parent.z + z, halfWidth, parent.maxDepth - 1);
	}
	
	public void insert(Cullable obj) {
		boolean straddle = false;
		int index = 0;
		count++;
		
		for(int i = 0; i < DIM; ++i) {
			float delta = obj.getCentre(i) - pos[i];
			straddle |= Math.abs(delta) < (halfWidth / 2 + obj.getWidth(i) / 2);
			
			// Map the position to an octant
			if(delta > 0)
				index |= (1 << i);
		}
		
		System.out.println("Max depth: " + maxDepth);
		// If the object straddles a geometric boundary for the node, or if the max depth is reached add to this level
		if(straddle || maxDepth == 0) {
			objects.add(obj);
		} else {
			if(children[index] == null) {
				children[index] = OctNode.createChild(this, index);
			} 
			
			children[index].insert(obj);
		}
	}
	
	@Override
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix) {
		// Render everything at this level
		for(Cullable obj : objects) {
			obj.render(parentMatrix, viewMatrix);
		}
	}
	
	@Override
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix, Plane[] frustumPlanes) {
		if(isCullable(viewMatrix, parentMatrix, frustumPlanes))
			return;		
		
		// Render each child
		for(OctNode node : children) {
			if(node != null)
				node.render(parentMatrix, viewMatrix, frustumPlanes);
		}
		
		// Render everything at this level
		for(Cullable obj : objects) {
			obj.render(parentMatrix, viewMatrix, frustumPlanes);
		}
	}

	@Override
	public boolean bind() {
		if(isBound)
			return true;
		
		isBound = true;
		for(Cullable obj : objects) {
			isBound &= obj.bind();
		}
		
		// Now call on children
		for(int i = 0; i < CAPACITY; ++i) {
			if(children[i] != null) {
				isBound &= children[i].bind();
			}
		}
		
		return isBound;
	}

	@Override
	public boolean hasChildren() {
		return count > 0;
	}

	@Override
	public List<Renderable> getChildren() {
		return null;
	}

	@Override
	public boolean isBound() {
		return isBound;
	}

	@Override
	public boolean isCullable(Matrix4f viewMatrix, Matrix4f parentMatrix, Plane[] frustumPlanes) {
		Vector4f[] corners = new Vector4f[CAPACITY];
		for(int j = 0; j < CAPACITY; j++) {
			float dx = ((j & 1) > 0 ? halfWidth : -halfWidth);
			float dy = ((j & 2) > 0 ? halfWidth : -halfWidth);
			float dz = ((j & 4) > 0 ? halfWidth : -halfWidth);
			corners[j] = new Vector4f(x + dx, y + dy, z + dz, 1);
			Matrix4f.transform(parentMatrix, corners[j], corners[j]);
			Matrix4f.transform(viewMatrix, corners[j], corners[j]);
		}
		
		boolean outsidefrustum = true;
		for (int i = 0; i < frustumPlanes.length; i++) {
			for (int j = 0; j < CAPACITY; j++) {
				float dP = MathUtils.dotPlaneWithVector(frustumPlanes[i], corners[j]);
				outsidefrustum &= dP < 0f;
				if(outsidefrustum == false)
					break;
			}
			
			// If any outside any plane can deem object to be cullable
			if (outsidefrustum == true) 
				return true;
	
			outsidefrustum = true;
		}
		
		return false;
	}
	
}
