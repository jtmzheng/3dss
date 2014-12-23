package terrain;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import physics.PhysicsModelProperties;
import renderer.Renderable;
import renderer.model.Model;
import renderer.model.ModelInt;
import renderer.model.ModelType;
import util.Plane;
import util.Primitives;

import com.bulletphysics.collision.dispatch.CollisionFlags;

/**
 * A 3D block terrain class
 * @author Max
 *
 */
public class BlockTerrain implements Renderable {
	public Model blockModel;

	private int data [][][];
	private int cSize;
	
	public BlockTerrain(int [][][] data, int cubeSize) {
		this.data = data;
		this.cSize = cubeSize;
		setup();
	}
	
	private void setup() {
		List<ModelInt> modelsToMerge = new ArrayList<>();
		ModelInt base = (ModelInt) Primitives.getCube(cSize, ModelType.INTERACTIVE);
		
		long start = System.currentTimeMillis();
		for (int x = 0; x < data.length; x++) {
			for (int y = 0; y < data[0].length; y++) {
				for (int z = 0; z < data[0][0].length; z++) {
					if(data[x][y][z] != 0) { 
						ModelInt copy = new ModelInt(base, new Vector3f(x * cSize, y * cSize, z * cSize));
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
		this.blockModel = ModelInt.merge(modelsToMerge, groundProps);
		end = System.currentTimeMillis();
		System.out.println("Time 2: " + (end - start));

	}

	@Override
	public void render(Matrix4f parentMatrix, Matrix4f viewMatrix) {
		blockModel.render(parentMatrix, viewMatrix);
	}

	@Override
	public boolean bind() {
		return false;
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Renderable> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBound() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCullable(Matrix4f viewMatrix, Plane[] frustumPlanes) {
		// TODO Auto-generated method stub
		return false;
	}
}
