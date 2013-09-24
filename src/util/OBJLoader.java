package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderer.Face;
import renderer.Model;

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
 */

@Deprecated
public class OBJLoader {

	/*
	 * Apparently there is not .obj loader available... fun stuff!
	 * loadTexturedModel
	 * @author Max
	 */
	
	public static Model loadModel(File file) throws FileNotFoundException, IOException{
		List<Vector3f> vertices = new ArrayList<Vector3f>();    //vertex 
		List<Vector3f> normals = new ArrayList<Vector3f>();     //vertex normal
		List<Vector2f> textures = new ArrayList<Vector2f>();    //vertex texture
		List<Face> faces = new ArrayList<Face>();
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		
		while((line = br.readLine()) != null){
			if(line.startsWith("v ")){
				vertices.add(new Vector3f(
						Float.valueOf(line.split(" ")[1]), 
						Float.valueOf(line.split(" ")[2]),
						Float.valueOf(line.split(" ")[3])));
			}/*
			else if(line.startsWith("vt ")){
				textures.add(new Vector2f(
						Float.valueOf(line.split(" ")[1]), 
						Float.valueOf(line.split(" ")[2])));
			}*/
			else if(line.startsWith("vn ")){
				normals.add(new Vector3f(
						Float.valueOf(line.split(" ")[1]), 
						Float.valueOf(line.split(" ")[2]),
						Float.valueOf(line.split(" ")[3])));
			}
			else if(line.startsWith("f ")){
				//				f 5/1/1 1/2/1 4/3/1
				//				f 5 1 3 <-- Only vertex data (TODO)
				String [] fields = line.split(" ");
				System.out.println(line);
				Vector3f v = new Vector3f(
						Float.valueOf(fields[1].split("/")[0]),
						Float.valueOf(fields[2].split("/")[0]),
						Float.valueOf(fields[3].split("/")[0]));
				
				/*
				Vector3f vt = new Vector3f(
						Float.valueOf(fields[1].split("/")[1]),
						Float.valueOf(fields[2].split("/")[1]),
						Float.valueOf(fields[3].split("/")[1]));
				*/
				
				Vector3f vn = new Vector3f(
						Float.valueOf(fields[1].split("/")[2]),
						Float.valueOf(fields[2].split("/")[2]),
						Float.valueOf(fields[3].split("/")[2]));
				//faces.add(new Face(v, vn /*, vt */));	
			}
		}
		
		br.close();
		System.out.println(vertices.size());
		System.out.println(faces.size());
		System.out.println(normals.size());
		
		//If all of the lists aren't the same size then the 
		//if(vertices.size() != normals.size()  || vertices.size() != faces.size())
		//	throw new IOException();
		
		//Model m = new Model(vertices, normals, textures, faces);
		//return m;
		return null;
	}
	
}

