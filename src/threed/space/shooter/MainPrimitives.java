package threed.space.shooter;

import input.Input;
import input.KeyInput;
import input.MouseInput;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import physics.PhysicsModelProperties;
import renderer.Camera;
import renderer.Fog;
import renderer.Model;
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
public class MainPrimitives {
	
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
		player = new Player(gameCam, a, gameRenderer);
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
		// Create fog
		Fog worldFog = new Fog(new Vector3f(0.2f, 0.2f, 0.2f), 2.0f, 10.0f, true);
				
		gameCam = new Camera(new Vector3f(10.0f, 0.0f, -20.0f));
		gameRenderer = new Renderer(600, 600, gameCam, 60, worldFog);
		gameWorld = new World(gameRenderer);

		PhysicsModelProperties groundProps = new PhysicsModelProperties();
		groundProps.setProperty("mass", 0f);
		groundProps.setProperty("restitution", 0.9f);
		groundProps.setProperty("damping", 0.9f);
		groundProps.setProperty("collisionFlags", CollisionFlags.STATIC_OBJECT);
		
		//@TODO: Use a plane when we figure out why some objects are falling through the plane.
		//Model ground = Primitives.getPlane(50, 50, groundProps);
		Model ground = Primitives.getCube(50, groundProps);
		ground.translate(new Vector3f(-25, -55, -25));

		List<Model> modelsToMerge = new ArrayList<Model>();
		Model current = Primitives.getCube(2);
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 5; y++) {
				Model copy = new Model(current, new Vector3f(x, y, 0));
				modelsToMerge.add(copy);
			}
		}

		Model merged = Model.merge(modelsToMerge);
		Model cloned = new Model(merged, new Vector3f(5, 10, -10));
		gameWorld.addModel(merged);
		gameWorld.addModel(cloned);
		gameWorld.addModel(ground);
	}
}