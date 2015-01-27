package world;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import javax.vecmath.Vector3f;

import physics.PhysicsModel;
import renderer.Renderable;
import renderer.Renderer;
import renderer.model.ModelInt;
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
	private ArrayBlockingQueue<Renderable> removeQueue;
	private ArrayBlockingQueue<Renderable> addQueue;

	private List<DynamicWorldObject> worldObjects;
	
	/**
	 * Constructor for World class
	 * @param renderer
	 */
	public World(Renderer renderer) {
		this.renderer = renderer;
		this.worldObjects = new ArrayList<>();

		removeQueue = new ArrayBlockingQueue<Renderable>(100);
		addQueue = new ArrayBlockingQueue<Renderable>(100);
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
	
	public void addModel(Renderable model) {
		addQueue.add(model);
	}

	public void removeModel(Renderable model) {
		removeQueue.add(model);
	}

	private void _addModel(Renderable model) throws IllegalStateException {
		renderer.addModel(model);

		// @TODO: Fix this hack
		if(ModelInt.class.isAssignableFrom(model.getClass())) {
			dynamicsWorld.addRigidBody(((ModelInt)model).getPhysicsModel().getRigidBody());
		}
	}

	private void _removeModel(Renderable model) {
		renderer.removeModel(model);

		// @TODO: Fix this hack
		if(ModelInt.class.isAssignableFrom(model.getClass())) {
			dynamicsWorld.removeCollisionObject(((ModelInt)model).getPhysicsModel().getRigidBody());
			dynamicsWorld.removeRigidBody(((ModelInt)model).getPhysicsModel().getRigidBody());
		}
	}
	
	public void simulate() {
		dynamicsWorld.stepSimulation(1.0f / renderer.getFrameRate());
		renderer.updateModels();

		//TODO: this is throwing a GL error (invalid operation). find out why
		// renderer.renderColourPicking();
		renderer.renderScene();

		// Flush the add and remove model queues synchronously, so we don't add
		// or remove while simulating.
		flushQueues();
	}

	private void flushQueues() {
		while (!removeQueue.isEmpty()) {
			_removeModel(removeQueue.poll());
		}

		while (!addQueue.isEmpty()) {
			_addModel(addQueue.poll());
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
}
