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
import renderer.Renderer;
import renderer.SceneGraph;
import renderer.SceneNode;
import renderer.model.Model;
import renderer.model.ModelFactory;
import renderer.model.ModelInt;
import renderer.model.ModelScene;
import renderer.model.ModelType;
import renderer.util.Skybox;
import texture.Texture;
import texture.TextureLoader;
import util.Primitives;
import world.World;
import characters.Player;

public class MainScene {
	/**
	 * Test client
	 * @param args
	 */
	public static void main(String [] args) {
		// Define the context for the renderer
		Context context = new Context("Terrain Test", 800, 800, 3, 3, false, 60);
		Camera gameCam = new Camera(new Vector3f(0.0f, 0.0f, 5.0f));
		Renderer gameRenderer = new Renderer(context, gameCam);
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

		// Setup scene graph (a pyramid), technically a linked list
		SceneNode[] rgNodes = new SceneNode[100];
		SceneGraph sceneGraph = null;
		float startSize = 10;
		float thickness = 1;
		for(int i = 0; i < rgNodes.length; i++) {
			ModelScene model = (ModelScene) Primitives.getRectangularPrism(startSize - 0.1f * i, startSize - 0.1f * i, thickness, ModelType.SCENE);
			model.translate(new Vector3f(0, 0, -thickness));
			rgNodes[i] = new SceneNode(model);
			
			// Scene graph should have one child
			if(i == 0) {
				model.rotateX(90);
				sceneGraph = new SceneGraph(rgNodes[i]);
			} else {
				rgNodes[i - 1].addChild(rgNodes[i]);
			}
		}
		
		gameWorld.addModel(sceneGraph);
		
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
