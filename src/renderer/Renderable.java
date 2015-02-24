package renderer;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

import util.Plane;

// @TODO: Use this interface for anything that can be rendered, follows composite patter
public interface Renderable {
	
	// @TODO: Review bind(), isCullable
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix);
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix, Plane[] frustumPlanes);
	public boolean bind(); 
	public boolean hasChildren();
	public List<Renderable> getChildren();
	public boolean isBound(); 
	
	// @TODO: Arguments need to be flipped in order
	public boolean isCullable(Matrix4f viewMatrix, Matrix4f parentMatrix, Plane[] frustumPlanes);
	
	// @TODO: Possible extensions
	// public void setUniforms();	
}
