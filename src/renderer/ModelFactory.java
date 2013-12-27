package renderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionFlags;

import physics.PhysicsModelProperties;
import util.Parser;

/**
 * Static utility class to load in .obj files (and eventually other stuff).
 * Remember, vertex index starts at 1 for obj files, not 0.
 * @author Max
 * @author Adi
 */
public class ModelFactory {
	
	// Private constructor to prevent instantiation.
	private ModelFactory() {}
	
	/**
	 * Loads a 3D model.
	 * @param file The OBJ file.
	 * @return m The Model object.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Model loadObjModel(File file) throws InterruptedException, IOException {
		List<Face> faces;

		Parser parseFile = new Parser();
		parseFile.parseOBJFile(file);
		
		faces = parseFile.getFaces();
		
		Model m = new Model(faces, new PhysicsModelProperties());
		return m;
	}
	
	/**
	 * Loads a 3D model at an initial position
	 * @param file
	 * @param pos
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static Model loadObjModel(File file, Vector3f pos) throws InterruptedException, IOException {
		List<Face> faces;

		Parser parseFile = new Parser();
		parseFile.parseOBJFile(file);
		
		faces = parseFile.getFaces();
		
		Model m = new Model(faces, pos, new PhysicsModelProperties());
		return m;
	}
	
	/**
	 * Loads a 3D model with specified physics properties
	 * @param file
	 * @param rigidBodyProp
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static Model loadObjModel(File file, PhysicsModelProperties rigidBodyProp) throws InterruptedException, IOException {
		List<Face> faces;

		Parser parseFile = new Parser();
		parseFile.parseOBJFile(file);
		
		faces = parseFile.getFaces();
		
		Model m = new Model(faces, rigidBodyProp);
		return m;
	}

	/**
	 * Loads a 3D model at an initial position and with specified physics properties
	 * @param file
	 * @param pos
	 * @param rigidBodyProp
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static Model loadObjModel(File file, Vector3f pos, PhysicsModelProperties rigidBodyProp) throws InterruptedException, IOException {
		List<Face> faces;

		Parser parseFile = new Parser();
		parseFile.parseOBJFile(file);
		
		faces = parseFile.getFaces();
		
		Model m = new Model(faces, rigidBodyProp);
		return m;
	}

	// TODO: Use a better ground than just a cube.
	public static Model loadGround(File file) throws InterruptedException, IOException {
		Parser parseFile = new Parser();
		parseFile.parseOBJFile(file);
		
		PhysicsModelProperties groundProps = new PhysicsModelProperties();
		groundProps.setProperty("mass", 0f);
		groundProps.setProperty("restitution", 0.9f);
		groundProps.setProperty("damping", 0.9f);
		groundProps.setProperty("collisionFlags", CollisionFlags.STATIC_OBJECT);
		return new Model(parseFile.getFaces(), new Vector3f(0, -10, 0), groundProps);
	}
	
	// TODO: Use a better ground than just a cube.
	public static Model loadGround(File file, Vector3f pos) throws InterruptedException, IOException {
		Parser parseFile = new Parser();
		parseFile.parseOBJFile(file);

		PhysicsModelProperties groundProps = new PhysicsModelProperties();
		groundProps.setProperty("mass", 0f);
		groundProps.setProperty("restitution", 0.9f);
		groundProps.setProperty("damping", 0.9f);
		groundProps.setProperty("collisionFlags", CollisionFlags.STATIC_OBJECT);
		return new Model(parseFile.getFaces(), pos, groundProps);
	}
}