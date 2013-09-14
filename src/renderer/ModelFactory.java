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

/*
 * OBJ File convention (from wiki)
 * -------------------------------
 * 
 * # List of Vertices, with (x,y,z[,w]) coordinates, w is optional and defaults to 1.0.
 * v 0.123 0.234 0.345 1.0
 * v ...
 * ...
 * # Texture coordinates, in (u ,v [,w]) coordinates, these will vary between 0 and 1, w is optional and default to 0.
 * vt 0.500 1 [0]
 * vt ...
 * ...
 * # Normals in (x,y,z) form; normals might not be unit.
 *.
 * vn 0.707 0.000 0.707
 * vn ...
 * ...
 * # Parameter space vertices in ( u [,v] [,w] ) form; free form geometry statement ( see below )
 * vp 0.310000 3.210000 2.100000
 * vp ...
 * ...
 * # Face Definitions (see below)
 * f 1 2 3
 * f 3/1 4/2 5/3
 * f 6/4/1 3/5/3 7/6/5
 * f ...
 * ...
 *
 * Static class to load in .obj files (and eventually other stuff)
 * Remember, vertex index starts at 1 for obj files, not 0
 * @author Max
 * @author Adi
 */

public class ModelFactory {

	
	/*
	 * Apparently there is not .obj loader available... fun stuff!
	 * loadTexturedModel
	 * @author Max
	 */
	
	public static Model loadModel(File file) throws FileNotFoundException, IOException{
		List<Face> faces;

		Parser parseFile = new Parser();
		parseFile.parseOBJFile(file);
		
		faces = parseFile.getFaces();

		/**
		 * The faces contain all the information we need.
		 * @see Face.java
		 */
		Model m = new Model(faces);
		return m;
	}
	
}

