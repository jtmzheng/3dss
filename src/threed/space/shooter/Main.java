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
import renderer.Context;
import renderer.Fog;
import renderer.Renderer;
import renderer.model.Model;
import renderer.model.ModelFactory;
import renderer.util.Ground;
import renderer.util.ParticleEmitter;
import renderer.util.Skybox;
import renderer.util.TextBox;
import renderer.util.TextManager;
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
	private static TextManager textManager = TextManager.getInstance();

	private static TextBox playerPosition;

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
			updateTextOnScreen();
		}
		
		gameWorld.cleanupDynamicWorldObjects();
	}

	public static void updateTextOnScreen () {
		Vector3f position = player.getPosition();
		double x = Math.round(position.x*100.0)/100.0;
		double y = Math.round(position.y*100.0)/100.0;
		double z = Math.round(position.z*100.0)/100.0;
		textManager.setText(playerPosition, "pos: (" + x + "," + y + "," + z + ")");
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

		playerPosition = new TextBox("", 10, 10, 18);
		textManager.addTextBox(playerPosition);
	}
	
	/**
	 * Sets up the world
	 */
	public static void setupWorld() {
		// Create fog
		Fog worldFog = new Fog(true);
		
		// Create the context for the renderer
		Context context = new Context("Skybox Test", 512, 512, 3, 3, false, 60);
		
		gameCam = new Camera(new Vector3f(0.0f, 0.0f, 5.0f));
		gameRenderer = new Renderer(context, gameCam, worldFog);
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