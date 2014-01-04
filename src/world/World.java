package world;

import javax.vecmath.Vector3f;

import physics.PhysicsModel;
import renderer.Model;
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
	
	public void addModel(Model model) {
		renderer.bindNewModel(model);
		dynamicsWorld.addRigidBody(model.getPhysicsModel().getRigidBody());
	}
	
	public void removeModel(Model model) {
		// @TODO: Removal of model
	}
	
	public void simulate() {
		dynamicsWorld.stepSimulation(1.0f / renderer.getFrameRate());
		renderer.renderColourPicking();
		renderer.renderScene();
	}
	
	/**
	 * Set up the physics (JBullets) of the World
	 * @see PhysicsModel 
	 */
	private void setupPhysics() {
		BroadphaseInterface broadphase = new DbvtBroadphase();
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        ConstraintSolver solver = new SequentialImpulseConstraintSolver();
        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        dynamicsWorld.setGravity(new Vector3f(0.0f, -2f, 0.0f));
	}
	
	/**
	 * Test client
	 * @param args
	 */
	public static void main(int args[]) {
		
	}
}
