package threed.space.shooter;

import input.Input;
import input.KeyInput;
import input.MouseInput;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import physics.PhysicsModelProperties;
import renderer.Camera;
import renderer.Model;
import renderer.ModelFactory;
import renderer.Renderer;
import util.Primitives;
import world.World;
import characters.Player;

import com.bulletphysics.collision.dispatch.CollisionFlags;

/**
 * Main class for our game.
 * Contains the game loop.
 * @TODO: Start using JUnit
 * @author Adi
 * @author Max
 */
public class Main {
	
	/**
	 * The world object
	 */
	static World gameWorld;

	/**
	 * The renderer the game uses.
	 */
	static Renderer gameRenderer;
	
	/**
	 * The player.
	 */
	static Player player;
	
	/**
	 * The camera object.
	 */
	static Camera gameCam;
	
	/**
	 * The inputs used in this game.
	 */
	static ArrayList<Input> rawInputs = new ArrayList<Input>();
	
	public static void main(String [] args){
		setupWorld();
		setupPlayer();

		// Game loop.
		while(!Display.isCloseRequested()){
			// Poll the inputs.
			for (Input i : rawInputs) {
				i.poll();
			}
			
			player.move();
			gameWorld.simulate();
		}
	}

	/**
	 * Sets up the game player.
	 */
	public static void setupPlayer() {
		PhysicsModelProperties playerProperties = new PhysicsModelProperties();
		playerProperties.setProperty("mass", 10f);
		playerProperties.setProperty("restitution", 0.75f);
		
		Model a = Primitives.getRectangularPrism(5, 5, 10, playerProperties);
		player = new Player(gameCam, a);
		gameWorld.addModel(a);
		
		rawInputs.add(new MouseInput());
		rawInputs.add(new KeyInput());
		
		for (Input i : rawInputs) {
			i.initialize();
			i.setListener(player);
		}
	}
	
	/**
	 * Sets up the world
	 */
	public static void setupWorld() {
		gameCam = new Camera(new Vector3f(10.0f, 0f, -10.0f), new Vector3f(2, 0, 1));
		gameRenderer = new Renderer(600, 600, gameCam, 60);
		gameWorld = new World(gameRenderer);

		PhysicsModelProperties groundProps = new PhysicsModelProperties();
		groundProps.setProperty("mass", 0f);
		groundProps.setProperty("restitution", 0.9f);
		groundProps.setProperty("damping", 0.9f);
		groundProps.setProperty("collisionFlags", CollisionFlags.STATIC_OBJECT);
		
		// TODO: Use a plane when we figure out why some objects are falling through the plane.
		//Model ground = Primitives.getPlane(50, 50, groundProps);
		//ground.translate(new Vector3f(-25, -5, -25));
		Model ground = Primitives.getCube(50, groundProps);
		ground.translate(new Vector3f(-25, -55, -25));

		Random rand = new Random();
		// Create and add cubes of varying sizes.
		for (int i = 0; i < 10; i++) {
			float edgeLength = rand.nextFloat();
			Model cube = Primitives.getCube(edgeLength);
			cube.translate(new Vector3f(i + 1, 1, 10));
			gameWorld.addModel(cube);
		}
		
		// Create and add rectangular prisms of varying sizes.
		for (int i = 0; i < 5; i++) {
			float width = rand.nextFloat()*2.5f + 1;
			float length = rand.nextFloat()*2.5f + 1;
			float height = rand.nextFloat()*2.5f + 1;
			Model rectPrism = Primitives.getRectangularPrism(width, length, height);
			rectPrism.translate(new Vector3f(-10, 1, 3*i + 1));
			gameWorld.addModel(rectPrism);
		}
		gameWorld.addModel(ground);
	}
}