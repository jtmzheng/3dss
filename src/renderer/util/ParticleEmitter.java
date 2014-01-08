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

public class ParticleEmitter {
	private static final long DEFAULT_SPAWN_RATE = 100;
	private static final long DEFAULT_PARTICLE_VELOCITY_SCALE = 6;
	private static final long DEFAULT_PARTICLE_LIFETIME = 1000;

	private World gameWorld;
	private Vector3f initialPosition;

	private long speedModifier = DEFAULT_PARTICLE_VELOCITY_SCALE;
	private long particleLifetime = DEFAULT_PARTICLE_LIFETIME;
	private long spawnRate = DEFAULT_SPAWN_RATE;

	private final ScheduledExecutorService particleScheduler = Executors.newScheduledThreadPool(1);
	private Runnable spawnParticle;
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
		 spawnParticle = new Runnable() {
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
	 * Begins spawning particles.
	 */
	public void start () {
		particleScheduler.scheduleAtFixedRate(spawnParticle, 0, spawnRate, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Stops spawning particles.
	 */
	public void stop () {
		particleScheduler.shutdown();
	}

	private class Particle {

		private Model model;
		private long lifetime = DEFAULT_PARTICLE_LIFETIME;
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

		public Particle(Vector3f position, Vector3f velocity, long lifetime) {
			model = Primitives.getCube(0.1f);
			model.translate(position);
			
			RigidBody rb = model.getPhysicsModel().getRigidBody();
			rb.setLinearVelocity(new javax.vecmath.Vector3f(velocity.x, velocity.y, velocity.z));
			particleScheduler.schedule(removeParticle, lifetime, TimeUnit.MILLISECONDS);
		}
		
		public Model getModel() {
			return model;
		}
	}
}