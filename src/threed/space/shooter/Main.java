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
import characters.Character;
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
	 * The characters in the game.
	 */
	static ArrayList<Character> gameCharacters = new ArrayList<Character>();
	
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
		
		// Game loop.
		while(!Display.isCloseRequested()){
			// Poll the inputs.
			for (Input i : rawInputs) {
				i.poll();
			}
			
			for (Character c : gameCharacters) {
				c.move();
			}
			
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
			gameRenderer.bindNewModel(ModelFactory.loadModel(new File("res/obj/ATAT.obj")));	
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets up the game player.
	 */
	public static void setupPlayer() {
		Player gamePlayer = new Player(gameCam);
		
		rawInputs.add(new MouseInput());
		rawInputs.add(new KeyInput());
		
		for (Input i : rawInputs) {
			i.initialize();
			i.setListener(gamePlayer);
		}
		
		gameCharacters.add(gamePlayer);
	}
	
	/**
	 * Cleans up, frees memory, flushes streams.
	 */
	public static void cleanUp() {
		Logger.flush();
	}
}
