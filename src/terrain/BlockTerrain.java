package terrain;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import physics.PhysicsModelProperties;
import renderer.model.Model;
import util.Primitives;

import com.bulletphysics.collision.dispatch.CollisionFlags;

public class BlockTerrain {
	
	public BlockTerrain(int [][][] data, int cubeSize) {
		this.data = data;
		this.cSize = cubeSize;
		setup();
	}
	
	public void render(Matrix4f viewMatrix) {
		blockModel.render(false, viewMatrix);
	}
	
	private void setup() {
		List<Model> modelsToMerge = new ArrayList<>();
		Model base = Primitives.getCube(cSize);
		
		long start = System.currentTimeMillis();
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
		long end = System.currentTimeMillis();
		System.out.println("Time 1: " + (end - start));
		
		PhysicsModelProperties groundProps = new PhysicsModelProperties();
		groundProps.setProperty("mass", 0f);
		groundProps.setProperty("restitution", 0.9f);
		groundProps.setProperty("damping", 0.9f);
		groundProps.setProperty("collisionFlags", CollisionFlags.STATIC_OBJECT);
		
		start = System.currentTimeMillis();
		this.blockModel = Model.merge(modelsToMerge, groundProps);
		end = System.currentTimeMillis();
		System.out.println("Time 2: " + (end - start));

	}
	
	private int data [][][];
	private int cSize;
	public Model blockModel;
	
}
