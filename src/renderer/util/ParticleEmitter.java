package renderer.util;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.lwjgl.util.vector.Vector3f;

import renderer.model.Model;
import util.Primitives;
import world.World;

import com.bulletphysics.dynamics.RigidBody;

/**
 * The particle emitter is a helper class used to generate particles at a fixed rate
 * and velocity. This can be used to create things like explosions, sparks, and snow.
 * 
 * @author Adi
 */
public class ParticleEmitter {
	// Default values.
	private static final long DEFAULT_SPAWN_RATE = 100;
	private static final long DEFAULT_PARTICLE_VELOCITY_SCALE = 6;
	private static final long DEFAULT_PARTICLE_LIFETIME = 1000;

	// Reference to the world this emitter is in.
	private World gameWorld;
	
	// Initial position of the emitter.
	private Vector3f initialPosition;

	// Modifier to amplify the velocity of the particles.
	private long speedModifier = DEFAULT_PARTICLE_VELOCITY_SCALE;
	
	// Lifetime of a single particle, in milliseconds.
	private long particleLifetime = DEFAULT_PARTICLE_LIFETIME;
	
	// Rate at which particles spawn, in milliseconds.
	private long spawnRate = DEFAULT_SPAWN_RATE;

	// The service that schedules tasks for adding and removing particles.
	private final ScheduledExecutorService particleScheduler = Executors.newScheduledThreadPool(1);
	
	// The runnable that binds a particle to the world.
	private Runnable spawnParticleRunnable;
	
	// A helper object to create random numbers.
	private Random randomGenerator = new Random();

	/**
	 * Creates an instance of a ParticleEmitter.
	 * 
	 * @param world A reference to the world that the emitter is created in.
	 * @param initialPos The initial position to begin emitting particles.
	 */
	public ParticleEmitter(World world, Vector3f initialPos) {
		gameWorld = world;
		initialPosition = initialPos;
		
		setupRunnable();
	}

	/**
	 * Creates an instance of a ParticleEmitter.
	 * 
	 * @param world A reference to the world that the emitter is created in.
	 * @param initialPos The initial position to begin emitting particles.
	 * @param velocityScale The modifier to scale the velocity of the particles.
	 */
	public ParticleEmitter(World world, Vector3f initialPos, long velocityScale) {
		gameWorld = world;
		initialPosition = initialPos;
		speedModifier = velocityScale;

		setupRunnable();
	}

	/**
	 * Creates an instance of a ParticleEmitter.
	 * 
	 * @param world A reference to the world that the emitter is created in.
	 * @param initialPos The initial position to begin emitting particles.
	 * @param velocityScale The modifier to scale the velocity of the particles.
	 * @param particleLife The lifetime of a single particle in milliseconds.
	 */
	public ParticleEmitter(World world, Vector3f initialPos, long velocityScale, 
			long particleLife) {
		gameWorld = world;
		initialPosition = initialPos;
		speedModifier = velocityScale;
		particleLifetime = particleLife;

		setupRunnable();
	}

	/**
	 * Creates an instance of a ParticleEmitter.
	 * 
	 * @param world A reference to the world that the emitter is created in.
	 * @param initialPos The initial position to begin emitting particles.
	 * @param velocityScale The modifier to scale the velocity of the particles.
	 * @param particleLife The lifetime of a single particle in milliseconds.
	 * @param spawnRate The spawn rate of particles in milliseconds.
	 */
	public ParticleEmitter(World world, Vector3f initialPos, long velocityScale,
			long particleLife, long particleSpawnRate) {
		gameWorld = world;
		initialPosition = initialPos;
		speedModifier = velocityScale;
		particleLifetime = particleLife;
		spawnRate = particleSpawnRate;

		setupRunnable();
	}

	/**
	 * Common setup for the particle runnable.
	 */
	private void setupRunnable() {
		spawnParticleRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					Vector3f velocity = new Vector3f();
			        velocity.x = randomGenerator.nextFloat() - 0.5f;
			        velocity.y = randomGenerator.nextFloat() - 0.5f;
			        velocity.z = randomGenerator.nextFloat() - 0.5f;
			        velocity.scale(speedModifier);
					Particle p = new Particle(initialPosition, velocity, particleLifetime);

					gameWorld.addModel(p.getModel());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	/**
	 * Sets the spawn rate of the particle emitter.
	 * @param newSpawnRate
	 */
	public void setSpawnRate (long newSpawnRate) {
		stop();
		spawnRate = newSpawnRate;
		start();
	}

	/**
	 * Sets the speed modifier to amplify the velocity of the particles (default is 6).
	 * @param modifier
	 */
	public void setSpeedModifier (long modifier) {
		speedModifier = modifier;
	}

	/**
	 * Begins spawning particles.
	 */
	public void start () {
		particleScheduler.scheduleAtFixedRate(spawnParticleRunnable, 0, spawnRate, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Stops spawning particles.
	 */
	public void stop () {
		particleScheduler.shutdown();
	}

	/**
	 * Private inner class to wrap the particle model.
	 * 
	 * @author Adi
	 */
	private class Particle {

		// The model for this particle.
		private Model model;
		
		// The runnable to remove a particle from the world.
		private final Runnable removeParticle = new Runnable() {
			@Override
			public void run() {
				try {
					gameWorld.removeModel(model);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		/**
		 * Creates an instance of a particle.
		 * @param position Initial position of the particle.
		 * @param velocity Initial velocity of the particle.
		 * @param lifetime Lifetime of the particle in milliseconds.
		 */
		public Particle(Vector3f position, Vector3f velocity, long lifetime) {
			model = Primitives.getCube(0.05f);
			model.translate(position);
			
			RigidBody rb = model.getPhysicsModel().getRigidBody();
			rb.setLinearVelocity(new javax.vecmath.Vector3f(velocity.x, velocity.y, velocity.z));
			particleScheduler.schedule(removeParticle, lifetime, TimeUnit.MILLISECONDS);
		}
		
		/**
		 * Returns the model of this particle.
		 * @return model
		 */
		public Model getModel() {
			return model;
		}
	}
}