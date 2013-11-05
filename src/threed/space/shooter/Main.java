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
import renderer.ModelFactory;
import renderer.Renderer;
import util.Logger;
import characters.Player;

/**
 * Main class for our game.
 * Contains the game loop.
 * @author Adi
 */
public class Main {

	/**
	 * The renderer the game uses.
	 */
	static Renderer gameRenderer;
	
	/**
	 * The player object.
	 */
	static Player gamePlayer;
	
	/**
	 * The camera object.
	 */
	static Camera gameCam;
	
	/**
	 * The inputs used in this game.
	 */
	static ArrayList<Input> rawInputs = new ArrayList<Input>();
	
	public static void main(String [] args){
		setupRenderer();
		setupPlayer();
		setupInputs();
		
		// Game loop.
		while(!Display.isCloseRequested()){
			// Poll the inputs.
			for (Input i : rawInputs) {
				i.poll();
			}
			
			// Move the player.
			gamePlayer.move();
			
			// Render a new frame.
			gameRenderer.renderScene();
		}
		
		cleanUp();
	}
	
	/**
	 * Sets up the game renderer.
	 */
	public static void setupRenderer() {
		gameCam = new Camera(new Vector3f(0.0f, 0.0f, 5.0f));
		gameRenderer = new Renderer(600, 600, gameCam);
		try{
			gameRenderer.bindNewModel(ModelFactory.loadModel(new File("res/obj/bunny.obj")));	
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets up the game player.
	 */
	public static void setupPlayer() {
		gamePlayer = new Player(gameCam);
	}
	
	/**
	 * Sets up the raw inputs and binds them to player.
	 */
	public static void setupInputs() {
		rawInputs.add(new MouseInput());
		rawInputs.add(new KeyInput());
		
		for (Input i : rawInputs) {
			i.initialize();
			i.setListener(gamePlayer);
		}
	}
	
	/**
	 * Cleans up, frees memory, flushes streams.
	 */
	public static void cleanUp() {
		Logger.flush();
	}
}