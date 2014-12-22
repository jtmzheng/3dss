package renderer.util;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.lwjgl.util.vector.Vector3f;

import renderer.model.Model;
import renderer.model.ModelInt;
import util.Primitives;
import world.World;

import com.bulletphysics.dynamics.RigidBody;

/**
 * The particle emitter is a helper class used to generate particles at a fixed rate
 * and velocity. This can be used to create things like explosions, sparks, and snow.
 * 
 * @author Adi
 */
public class ParticleEmitter implements DynamicWorldObject {
	
	// Default values.
	private static final long DEFAULT_SPAWN_RATE = 50;
	private static final long DEFAULT_PARTICLE_LIFETIME = 1000;
	private static final long TERMINATE_EMITTER_TIMEOUT = Long.MAX_VALUE;
	private static final float DEFAULT_PARTICLE_VELOCITY_SCALE = 7;
	private static final float PARTICLE_SIZE_SCALE = 1f;

	// Reference to the world this emitter is in.
	private World gameWorld;
	
	// Initial position of the emitter.
	private Vector3f initialPosition;

	// Modifier to amplify the velocity of the particles.
	private float speedModifier = DEFAULT_PARTICLE_VELOCITY_SCALE;
	
	// Lifetime of a single particle, in milliseconds.
	private long particleLifetime = DEFAULT_PARTICLE_LIFETIME;

	// Scaling factor for the size of a particle.
	private float particleSizeScale = PARTICLE_SIZE_SCALE;

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
	 * Creates an instance of a ParticleEmitter.
	 * 
	 * @param world A reference to the world that the emitter is created in.
	 * @param initialPos The initial position to begin emitting particles.
	 * @param velocityScale The modifier to scale the velocity of the particles.
	 * @param particleLife The lifetime of a single particle in milliseconds.
	 * @param spawnRate The spawn rate of particles in milliseconds.
	 * @param particleSizeScale The scaling factor for the size of the particles (default is 1).
	 */
	public ParticleEmitter(World world, Vector3f initialPos, long velocityScale,
			long particleLife, long particleSpawnRate, long particleSizeScale) {
		gameWorld = world;
		initialPosition = initialPos;
		speedModifier = velocityScale;
		particleLifetime = particleLife;
		spawnRate = particleSpawnRate;
		this.particleSizeScale = particleSizeScale;

		setupRunnable();
	}
	

	@Override
	public boolean needsCleanup() {
		return true;
	}

	@Override
	public boolean runCleanup() {
		return stop();
	}

	/**
	 * Common setup for the particle runnable.
	 */
	private void setupRunnable() {
		spawnParticleRunnable = new Runnable() {
			@Override
			public void run() {
				Vector3f velocity = new Vector3f();
				velocity.x = randomGenerator.nextFloat() - 0.5f;
				velocity.y = randomGenerator.nextFloat() - 0.5f;
				velocity.z = randomGenerator.nextFloat() - 0.5f;
				velocity.scale(speedModifier);
				Particle p = new Particle(initialPosition, velocity, particleLifetime, particleSizeScale);

				try {
					gameWorld.addModel(p.getModel());
				} catch(IllegalStateException e) {
					e.printStackTrace();
				}
			}
		};
	}

	/**
	 * Sets the speed modifier to amplify the velocity of the particles (default is 7).
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
	 * Stops spawning particles (blocking). 
	 */
	public boolean stop () {
		particleScheduler.shutdown();
		try {
			particleScheduler.awaitTermination(TERMINATE_EMITTER_TIMEOUT, TimeUnit.MILLISECONDS);
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}

	/**
	 * Private inner class to wrap the particle model.
	 * 
	 * @author Adi
	 */
	private class Particle {
		
		// Default particle size (edge length of the cube).
		private static final float DEFAULT_PARTICLE_SIZE = 0.03f;

		// The model for this particle.
		private ModelInt model;
		
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
		 * @param particleSizeScale Scaling factor for the size of a particle (Default is 1).
		 */
		public Particle(Vector3f position, Vector3f velocity, long lifetime, float particleSizeScale) {
			model = Primitives.getCube(DEFAULT_PARTICLE_SIZE * particleSizeScale);
			model.translate(position);
			
			RigidBody rb = model.getPhysicsModel().getRigidBody();
			rb.setLinearVelocity(new javax.vecmath.Vector3f(velocity.x, velocity.y, velocity.z));
			particleScheduler.schedule(removeParticle, lifetime, TimeUnit.MILLISECONDS);
		}
		
		/**
		 * Returns the model of this particle.
		 * @return model
		 */
		public ModelInt getModel() {
			return model;
		}
	}

}