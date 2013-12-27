package renderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import util.Parser;
import world.Ground;

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
		
		Model m = new Model(faces);
		return m;
	}
	
	public static Model loadObjModel(File file, Vector3f pos) throws InterruptedException, IOException {
		List<Face> faces;

		Parser parseFile = new Parser();
		parseFile.parseOBJFile(file);
		
		faces = parseFile.getFaces();
		
		Model m = new Model(faces, pos);
		return m;
	}

	// TODO: Use a better ground than just a cube.
	public static Ground loadGround(File file) throws InterruptedException, IOException {
		Parser parseFile = new Parser();
		parseFile.parseOBJFile(file);
		return new Ground(parseFile.getFaces(), new Vector3f(0, -10, 0));
	}
	
	// TODO: Use a better ground than just a cube.
	public static Ground loadGround(File file, Vector3f pos) throws InterruptedException, IOException {
		Parser parseFile = new Parser();
		parseFile.parseOBJFile(file);
		return new Ground(parseFile.getFaces(), pos);
	}
}