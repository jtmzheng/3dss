package threed.space.shooter;

import input.Input;
import input.KeyInput;
import input.MouseInput;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderer.Camera;
import renderer.Model;
import renderer.ModelFactory;
import renderer.Renderer;
import world.Ground;
import world.World;
import characters.Player;

/**
 * Main class for our game.
 * Contains the game loop.
 * @author Adi
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
		// setupRenderer();
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
	 * Sets up the game renderer.
	 */
	public static void setupRenderer() {
		gameCam = new Camera(new Vector3f(0.0f, 0.0f, 5.0f));
		gameRenderer = new Renderer(600, 600, gameCam, 60);
		try{
			Model a = ModelFactory.loadObjModel(new File("res/obj/sphere.obj"));
			a.translate(new Vector3f(5, 0, 5));
			Model b = ModelFactory.loadObjModel(new File("res/obj/sphere.obj"));
			b.translate(new Vector3f(-5, 0, -5));
			gameRenderer.bindNewModel(a);
			gameRenderer.bindNewModel(b);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets up the game player.
	 */
	public static void setupPlayer() {
		try {
			Model a = ModelFactory.loadObjModel(new File("res/obj/sphere.obj"));
			player = new Player(gameCam, a);
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
		gameCam = new Camera(new Vector3f(0.0f, 0.0f, 5.0f));
		gameRenderer = new Renderer(600, 600, gameCam, 60);
		gameWorld = new World(gameRenderer);
		
		try{
			Model a = ModelFactory.loadObjModel(new File("res/obj/ATAT.obj"), new Vector3f(5, 0, 5));
			a.applyForce(new Vector3f(5, 0, 5));
			Model b = ModelFactory.loadObjModel(new File("res/obj/sphere.obj"), new Vector3f(-5, 0, -5));
			b.applyForce(new Vector3f(-5, 0, -5));
			
			Ground ground = ModelFactory.loadGround(new File("res/obj/cube.obj"), new Vector3f(-25, -55, -25));
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
