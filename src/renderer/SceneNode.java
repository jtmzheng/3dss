package renderer;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

import renderer.model.ModelScene;
import util.Plane;

/**
 * Composite node for scene graph (can have scene nodes or models as children)
 * @author Max
 */
public class SceneNode implements Renderable {
	private List<Renderable> children;
	private ModelScene model; 
		
	public SceneNode() {
		this.children = new ArrayList<Renderable>();
		this.model = null;
	}
	
	public SceneNode(ModelScene model) {
		this.children = new ArrayList<Renderable>();
		this.model = model;
	}
	
	/**
	 * Recursively render each child
	 */
	@Override
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix) {
		if(this.model != null)
			model.render(parentMatrix, viewMatrix);
		
		parentMatrix = model.getModelMatrix(parentMatrix);
		for(Renderable child : children) {
			child.render(parentMatrix, viewMatrix);
		}
	}
	
	/**
	 * Recursively render each child
	 */
	@Override
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix, Plane[] frustumPlanes) {
		if(this.model != null)
			model.render(parentMatrix, viewMatrix);
		
		parentMatrix = model.getModelMatrix(parentMatrix);
		for(Renderable child : children) {
			child.render(parentMatrix, viewMatrix);
		}
	}

	/**
	 * Recursively bind each child
	 */
	@Override
	public boolean bind() {
		boolean fBound = true;
		if(this.model != null)
			fBound =  model.bind();
		
		for(Renderable child : children) {
			fBound &= child.bind();
		}
		
		return fBound;
	}

	public void addChild(Renderable rdbl) {
		children.add(rdbl);
	}
	
	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	@Override
	public List<Renderable> getChildren() {
		return children;
	}

	@Override
	public boolean isBound() {
		return model == null ? true : model.isBound();
	}

	@Override
	public boolean isCullable(Matrix4f viewMatrix, Matrix4f parentMatrix, Plane[] frustumPlanes) {
		// TODO:MZ Implement culling
		return false;
	}
	
}
