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
import renderer.model.ModelInt;
import renderer.model.ModelScene;
import renderer.model.ModelType;
import renderer.util.Skybox;
import renderer.util.TextBox;
import renderer.util.TextManager;
import spatial.OctTree;
import texture.Texture;
import texture.TextureLoader;
import util.Primitives;
import world.World;
import characters.Player;

public class MainSpatial {

	public static List<Model> generateCube(Vector3f origin, int xSize, int ySize, int zSize, int cSize, Model base) {
		Vector3f start = new Vector3f(
				origin.x - ((float) xSize * cSize) / 2,
				origin.y - ((float) ySize * cSize) / 2, 
				origin.z - ((float) zSize * cSize) / 2);
		ArrayList<Model> modelList = new ArrayList<>();
		
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				for (int z = 0; z < zSize; z++) {
					ModelScene copy = new ModelScene(base, new Vector3f(
							start.x + (float) cSize / 2 + x * cSize, 
							start.y + (float) cSize / 2 + y * cSize, 
							start.z	+ (float) cSize / 2 + z * cSize));
					modelList.add(copy);
				}
			}
		}

		return modelList;
	}

	/**
	 * Test client
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ModelScene base = (ModelScene)Primitives.getCube(1, ModelType.SCENE);
		List<Model> blockModel = generateCube(new Vector3f(0, 0, 0), 35, 35, 35, 5, base);
		OctTree octree = new OctTree(0, 0, 0, 50, 5);
		
		TextManager textManager = TextManager.getInstance();
		TextBox playerPosition = new TextBox("", 10, 10, 18);
		textManager.addTextBox(playerPosition);

		// Define the context for the renderer
		Context context = new Context("Terrain Test", 600, 600, 3, 3, false, 60);

		Camera gameCam = new Camera(new Vector3f(0.0f, 0.0f, 5.0f));
		Fog fog = new Fog(false);
		Renderer gameRenderer = new Renderer(context, gameCam, fog);
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

		if (sb != null) {
			gameRenderer.addSkybox(sb);
		}

		for(Model model : blockModel) {
			model.bind();
			octree.insert(model);
			//gameWorld.addModel(model);
		}

		gameWorld.addModel(octree);
		Player player;
		try {
			PhysicsModelProperties playerProperties = new PhysicsModelProperties();
			playerProperties.setProperty("mass", 10f);
			playerProperties.setProperty("restitution", 0.75f);

			ModelInt a = ModelFactory.loadObjModel(new File("res/obj/sphere.obj"), playerProperties);
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
		while (!Display.isCloseRequested()) {
			// Poll the inputs.
			for (Input i : rawInputs) {
				i.poll();
			}

			player.move();
			long start = System.currentTimeMillis();
			gameWorld.simulate();
			long end = System.currentTimeMillis();
			System.out.println("Time 1: " + (end - start));

			Vector3f position = player.getPosition();
			double x = Math.round(position.x * 100.0) / 100.0;
			double y = Math.round(position.y * 100.0) / 100.0;
			double z = Math.round(position.z * 100.0) / 100.0;
			textManager.setText(playerPosition, "pos: (" + x + "," + y + "," + z + ")");
		}

		gameWorld.cleanupDynamicWorldObjects();
	}
}
