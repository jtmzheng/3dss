package terrain;

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
import texture.Texture;
import texture.TextureLoader;
import util.Primitives;
import world.World;
import characters.Player;

import com.bulletphysics.collision.dispatch.CollisionFlags;

public class BlockTerrain {
	
	public BlockTerrain(int [][][] data, int cubeSize) {
		this.data = data;
		this.cSize = cubeSize;
		setup();
	}
	
	public void render() {
		blockModel.render(false);
	}
	
	private void setup() {
		List<Model> modelsToMerge = new ArrayList<>();
		Model base = Primitives.getCube(cSize);
		
		for (int x = 0; x < data.length; x++) {
			for (int y = 0; y < data[0].length; y++) {
				for (int z = 0; z < data[0][0].length; z++) {
					if(data[x][y][z] != 0) { 
						Model copy = new Model(base, new Vector3f(x * cSize, y * cSize, z * cSize));
						modelsToMerge.add(copy);
					}
				}
			}
		}
	
		PhysicsModelProperties groundProps = new PhysicsModelProperties();
		groundProps.setProperty("mass", 0f);
		groundProps.setProperty("restitution", 0.9f);
		groundProps.setProperty("damping", 0.9f);
		groundProps.setProperty("collisionFlags", CollisionFlags.STATIC_OBJECT);
		
		long start = System.currentTimeMillis();
		this.blockModel = Model.merge(modelsToMerge, groundProps);
		long end = System.currentTimeMillis();
		System.out.println("Time: " + (end - start));

	}
	
	public static void main(String [] args) {
		BlockTerrainGenerator btg = new BlockTerrainGenerator(20, 1, 1.1);
		BlockTerrain bt = btg.generateTerrain();
		
		Camera gameCam = new Camera(new Vector3f(0.0f, 0.0f, 5.0f));
		Fog fog = new Fog(true);
		Renderer gameRenderer = new Renderer(512, 512, gameCam, 60, fog, "Skybox Test");
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
	
	private int data [][][];
	private int cSize;
	public Model blockModel;
	
}
