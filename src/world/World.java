package world;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import physics.PhysicsModel;
import renderer.Renderer;
import renderer.model.Model;
import renderer.util.DynamicWorldObject;

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
	private final Object PHYSICS_WORLD_LOCK = new Object(); 
	private float WORLD_GRAVITY = -9.81f;

	private DynamicsWorld dynamicsWorld;
	private Renderer renderer;
	
	private List<DynamicWorldObject> worldObjects;
	
	/**
	 * Constructor for World class
	 * @param renderer
	 */
	public World(Renderer renderer) {
		this.renderer = renderer;
		this.worldObjects = new ArrayList<>();
		setupPhysics(/*@TODO: Options*/);
	}
	
	public void addDynamicWorldObject(DynamicWorldObject dwo) {
		worldObjects.add(dwo);
	}
	
	public boolean cleanupDynamicWorldObjects() {
		boolean success = true;
		for(DynamicWorldObject dwo : worldObjects) {
			if(dwo.needsCleanup()) {
				success &= dwo.runCleanup();
			}
		}
		worldObjects.clear();
		return success;
	}
	
	public void addModel(Model model) throws IllegalStateException {
		synchronized(PHYSICS_WORLD_LOCK) {
			renderer.addModel(model);
			dynamicsWorld.addRigidBody(model.getPhysicsModel().getRigidBody());
		}
	}

	public void removeModel(Model model) {
		synchronized(PHYSICS_WORLD_LOCK) {
			renderer.removeModel(model);
			dynamicsWorld.removeCollisionObject(model.getPhysicsModel().getRigidBody());
			dynamicsWorld.removeRigidBody(model.getPhysicsModel().getRigidBody());
		}
	}
	
	public void simulate() {
		synchronized(PHYSICS_WORLD_LOCK) {
			dynamicsWorld.stepSimulation(1.0f / renderer.getFrameRate());
			renderer.updateModels();
			renderer.renderColourPicking();
			renderer.renderScene();
		}
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
        dynamicsWorld.setGravity(new Vector3f(0.0f, WORLD_GRAVITY, 0.0f));
	}
	
	/**
	 * Test client
	 * @param args
	 */
	public static void main(int args[]) {
		
	}
}
