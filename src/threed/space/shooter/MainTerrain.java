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
import renderer.util.Skybox;
import terrain.BlockTerrain;
import terrain.BlockTerrainGenerator;
import texture.Texture;
import texture.TextureLoader;
import world.World;
import characters.Player;

public class MainTerrain {


	/**
	 * Test client
	 * @param args
	 */
	public static void main(String [] args) {
		BlockTerrainGenerator btg = new BlockTerrainGenerator(25, 1, 1.1);
		BlockTerrain bt = btg.generateTerrain();

		Camera gameCam = new Camera(new Vector3f(0.0f, 0.0f, 5.0f));
		Fog fog = new Fog(false);
		Renderer gameRenderer = new Renderer(512, 512, gameCam, 60, fog, "Terrain Test");
		World gameWorld = new World(gameRenderer);
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

		gameWorld.addModel(bt.blockModel);

		Player player;
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

		ArrayList<Input> rawInputs = new ArrayList<Input>();
		rawInputs.add(new MouseInput());
		rawInputs.add(new KeyInput());

		for (Input i : rawInputs) {
			i.initialize();
			i.setListener(player);
		}

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

}
