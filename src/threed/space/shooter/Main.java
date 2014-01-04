package threed.space.shooter;

import input.Input;
import input.KeyInput;
import input.MouseInput;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import physics.PhysicsModelProperties;
import renderer.Camera;
import renderer.Fog;
import renderer.Model;
import renderer.ModelFactory;
import renderer.Renderer;
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
		try {
			PhysicsModelProperties playerProperties = new PhysicsModelProperties();
			playerProperties.setProperty("mass", 10f);
			playerProperties.setProperty("restitution", 0.75f);
			
			Model a = ModelFactory.loadObjModel(new File("res/obj/sphere.obj"), playerProperties);
			player = new Player(gameCam, a, gameRenderer);
			gameWorld.addModel(a);
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
			return;
		}
		
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
		Fog worldFog = new Fog(true);
		
		gameCam = new Camera(new Vector3f(0.0f, 0.0f, 5.0f));
		gameRenderer = new Renderer(512, 512, gameCam, 60, worldFog);
		gameWorld = new World(gameRenderer);
		
		try{
			PhysicsModelProperties bProperties = new PhysicsModelProperties();
			bProperties.setProperty("mass", 100f);
			bProperties.setProperty("restitution", 0.75f);
			
			Model a = ModelFactory.loadObjModel(new File("res/obj/ATAT.obj"), new Vector3f(5, 0, 5));
			Model b = ModelFactory.loadObjModel(new File("res/obj/sphere.obj"), new Vector3f(-5, 0, -5), bProperties);
			
			PhysicsModelProperties groundProps = new PhysicsModelProperties();
			groundProps.setProperty("mass", 0f);
			groundProps.setProperty("restitution", 0.9f);
			groundProps.setProperty("damping", 0.9f);
			groundProps.setProperty("collisionFlags", CollisionFlags.STATIC_OBJECT);
			Model ground = ModelFactory.loadObjModel(new File("res/obj/cube.obj"), new Vector3f(-25, -55, -25), groundProps);

			gameWorld.addModel(a);
			gameWorld.addModel(b);
			gameWorld.addModel(ground);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
}