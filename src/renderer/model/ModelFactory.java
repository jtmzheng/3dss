package renderer.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import physics.PhysicsModelProperties;
import util.Parser;

/**
 * Static utility class to load in .obj files.
 * 
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
	public static ModelInt loadObjModel(File file) throws InterruptedException, IOException {
		List<Face> faces;

		Parser parseFile = new Parser();
		parseFile.parseOBJFile(file);
		
		faces = parseFile.getFaces();
		
		ModelInt m = new ModelInt(faces, new PhysicsModelProperties());
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
	public static ModelInt loadObjModel(File file, Vector3f pos) throws InterruptedException, IOException {
		List<Face> faces;

		Parser parseFile = new Parser();
		parseFile.parseOBJFile(file);
		
		faces = parseFile.getFaces();
		
		ModelInt m = new ModelInt(faces, pos, new PhysicsModelProperties());
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
	public static ModelInt loadObjModel(File file, PhysicsModelProperties rigidBodyProp) throws InterruptedException, IOException {
		List<Face> faces;

		Parser parseFile = new Parser();
		parseFile.parseOBJFile(file);
		
		faces = parseFile.getFaces();
		
		ModelInt m = new ModelInt(faces, rigidBodyProp);
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
	public static ModelInt loadObjModel(File file, Vector3f pos, PhysicsModelProperties rigidBodyProp) throws InterruptedException, IOException {
		List<Face> faces;

		Parser parseFile = new Parser();
		parseFile.parseOBJFile(file);
		
		faces = parseFile.getFaces();
		
		ModelInt m = new ModelInt(faces, pos, rigidBodyProp);
		return m;
	}
}