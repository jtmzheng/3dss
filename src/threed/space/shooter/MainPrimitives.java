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
import renderer.Context;
import renderer.Fog;
import renderer.Renderer;
import renderer.model.Model;
import renderer.model.ModelInt;
import renderer.model.ModelType;
import renderer.util.Ground;
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

	private static World gameWorld;
	private static Renderer gameRenderer;
	private static Player player;
	private static Camera gameCam;
	private static ArrayList<Input> rawInputs = new ArrayList<Input>();
	
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
		
		ModelInt a = (ModelInt) Primitives.getRectangularPrism(5, 5, 5, playerProperties);
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
				
		// Define the OpenGL context
		Context context = new Context("Primitives Test", 600, 600, 3, 3, false, 60);
		
		gameCam = new Camera(new Vector3f(10.0f, 0.0f, -20.0f));
		gameRenderer = new Renderer(context, gameCam, worldFog);
		gameWorld = new World(gameRenderer);

		Ground ground = new Ground(1000, 1000);
		ground.translate(new Vector3f(-500, 0, -500));

		List<ModelInt> modelsToMerge = new ArrayList<ModelInt>();
		ModelInt current = (ModelInt) Primitives.getCube(2, ModelType.INTERACTIVE);
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 5; y++) {
				ModelInt copy = new ModelInt(current, new Vector3f(x, y, 0));
				modelsToMerge.add(copy);
			}
		}

		ModelInt merged = ModelInt.merge(modelsToMerge);
		ModelInt cloned = new ModelInt(merged, new Vector3f(5, 10, -10));
		gameWorld.addModel(merged);
		gameWorld.addModel(cloned);
		gameWorld.addModel(ground);
	}
}