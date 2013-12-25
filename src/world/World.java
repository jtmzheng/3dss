package world;

import renderer.Renderer;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;

/**
 * World class that encapsulates a JBullet DynamicsWorld and a Renderer
 * @author Max
 */
public class World {

	private DynamicsWorld dynamicsWorld;
	private Renderer renderer;
	
	/**
	 * Constructor for World class
	 * @param renderer
	 */
	public World(Renderer renderer) {
		this.renderer = renderer;
		setupPhysics(/*@TODO: Options*/);
	}
	
	/**
	 * Set up the physics (JBullets) of the World 
	 */
	private void setupPhysics() {
		BroadphaseInterface broadphase = new DbvtBroadphase();
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        ConstraintSolver solver = new SequentialImpulseConstraintSolver();
        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
	}
	
	/**
	 * Test client
	 * @param args
	 */
	public static void main(int args[]) {
		
	}
 	
}
