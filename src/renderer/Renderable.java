package renderer;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

import util.Plane;

// @TODO: Use this interface for anything that can be rendered, follows composite patter
public interface Renderable {
	
	// @TODO: Review bind(), isCullable
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix);
	public boolean bind(); 
	public boolean hasChildren();
	public List<Renderable> getChildren();
	public boolean isBound(); 
	public boolean isCullable(Matrix4f viewMatrix, Plane[] frustumPlanes);
	
	// @TODO: Possible extensions
	// public void setUniforms();	
}
