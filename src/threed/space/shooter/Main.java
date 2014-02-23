package threed.space.shooter;

import input.Input;
import input.KeyInput;
import input.MouseInput;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import physics.PhysicsModelProperties;
import renderer.Camera;
import renderer.Fog;
import renderer.Renderer;
import renderer.model.Model;
import renderer.model.ModelFactory;
import renderer.util.Ground;
import renderer.util.ParticleEmitter;
import renderer.util.Skybox;
import system.Settings;
import texture.Texture;
import texture.TextureLoader;
import world.World;
import characters.Player;

/**
 * Main class for our game.
 * Contains the game loop.
 * @TODO: Start using JUnit
 * @author Adi
 * @author Max
 */
public class Main {
	
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
		
		gameWorld.cleanupDynamicWorldObjects();
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
		gameRenderer = new Renderer(512, 512, gameCam, 60, worldFog, "Skybox Test");
		gameWorld = new World(gameRenderer);
		
		List<String> files = new ArrayList<>();
		files.add("miramar_ft.png");
		files.add("miramar_bk.png");
		files.add("miramar_up.png");
		files.add("miramar_dn.png");
		files.add("miramar_rt.png");
		files.add("miramar_lf.png");
		
		Skybox sb = null;
		try {
			Texture sbTex = TextureLoader.loadCubeMapTexture(files, "miramar");
			sb = new Skybox(sbTex);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e2) {
			e2.printStackTrace();
		}
		
		if(sb != null) {
			gameRenderer.addSkybox(sb);
		}
		
		try {
			PhysicsModelProperties bProperties = new PhysicsModelProperties();
			bProperties.setProperty("mass", 10f);
			bProperties.setProperty("restitution", 0.2f);
			
			Model a = ModelFactory.loadObjModel(new File("res/obj/ATAT.obj"), new Vector3f(5, 0, 5));
			Model b = ModelFactory.loadObjModel(new File("res/obj/sphere.obj"), new Vector3f(-5, 0, -5), bProperties);
			
			Ground ground = new Ground(1000, 1000);
			ground.translate(new Vector3f(-500, 0, -500));

			gameWorld.addModel(a);
			gameWorld.addModel(b);
			gameWorld.addModel(ground);

			ParticleEmitter p = new ParticleEmitter(gameWorld, new Vector3f(0, 2, 0));
			gameWorld.addDynamicWorldObject(p);
			p.start();
		} catch(IOException e){
			e.printStackTrace();
		} catch(InterruptedException e){
			e.printStackTrace();
		}
	}
}