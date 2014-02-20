package renderer.model;

import com.bulletphysics.collision.dispatch.CollisionFlags;

import physics.PhysicsModelProperties;
import util.Primitives;

/**
 * Represents a static ground.
 * 
 * @author Adi
 */
public class Ground extends Model{
	private static PhysicsModelProperties groundProps;
	static {
		groundProps = new PhysicsModelProperties();
		groundProps.setProperty("mass", 0f);
		groundProps.setProperty("collisionFlags", CollisionFlags.STATIC_OBJECT);
	}

	/**
	 * Creates the ground on the x-z plane.
	 * @param x Width of the ground.
	 * @param y Length of the ground.
	 */
	public Ground(float x, float y) {
		super(Primitives.getPlane(x,  y).getFaceList(), groundProps);
	}
	
	/**
	 * Creates the ground using the specified model. Rewrites its physics properties such that
	 * it remains static.
	 * @param gm
	 */
	public Ground(Model gm) {
		super(gm.getFaceList(), groundProps);
	}
}