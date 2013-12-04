package renderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

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
	public static Model loadModel(File file) throws InterruptedException, IOException {
		List<Face> faces;

		Parser parseFile = new Parser();
		parseFile.parseOBJFile(file);
		
		faces = parseFile.getFaces();
		
		Model m = new Model(faces);
		return m;
	}
}

