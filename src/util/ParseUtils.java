package util;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderer.model.Face;
import renderer.model.VertexData;
import texture.Material;

/**
 * Utility functions used by the file parser.
 *
 * @author Adi
 */
public class ParseUtils {
	
	// Prevent instantiation.
	private ParseUtils(){}
	
	/**
	 * Parses a face line from a .obj file and returns the face object.
	 * @param line
	 * @param vertices
	 * @param normals
	 * @param textures
	 * @param currentMaterial
	 * @return
	 */
	public static Face parseFace(String line, List<Vector3f> vertices, List<Vector3f> normals, List<Vector2f> textures, Material currentMaterial) {
    	String[] tokens = line.split("\\s+");
	 	List<VertexData> faceData = new ArrayList<VertexData>();
    	
    	if (!line.contains("/")) {
    		for (int x = 1; x < tokens.length; x++) {
    			VertexData v = new VertexData (vertices.get(Integer.parseInt(tokens[x])-1));
    			faceData.add(v);
    		}
    		return new Face(faceData, currentMaterial);
    	}
    	
    	// We should have trimmed whitespace and stuff by this point.
    	if (tokens[1].matches("^[0-9]+/[0-9]+$")) {
    		for (int x = 1; x < tokens.length; x++) {
    			String[] indices = tokens[x].split("/");
    			VertexData v = new VertexData (vertices.get(Integer.parseInt(indices[0]) - 1),
    					textures.get(Integer.parseInt(indices[1]) - 1));
    			
    			faceData.add(v);
    		}
    		return new Face(faceData, currentMaterial);
    	}
    	
    	if (tokens[1].matches("^[0-9]+/[0-9]+/[0-9]+$")) {
    		for (int x = 1; x < tokens.length; x++) {
    			String[] indices = tokens[x].split("/");
    			VertexData v = new VertexData (vertices.get(Integer.parseInt(indices[0]) - 1),
    					textures.get(Integer.parseInt(indices[1]) - 1),
    					normals.get(Integer.parseInt(indices[2]) - 1));
    			
    			faceData.add(v);
    		}
    		return new Face(faceData, currentMaterial);
    	}
    	
    	if (tokens[1].matches("^[0-9]+//[0-9]+$")) {
    		for (int x = 1; x < tokens.length; x++) {
    			String[] indices = tokens[x].split("//");
    			VertexData v = new VertexData (vertices.get(Integer.parseInt(indices[0]) - 1),
    					new Vector2f(0f, 0f),
    					normals.get(Integer.parseInt(indices[1]) - 1));
    			
    			faceData.add(v);
    		}
    		return new Face(faceData, currentMaterial); 
    	}
    	
    	return null;
	}
	
	/**
	 * Parses a list of two floats.
	 * @param str
	 * @return a Vector2f with our results
	 */
	public static Vector2f parse2FloatList (String str) {
		Vector2f ret = new Vector2f();
		String[] vals = str.split("\\s+");
		ret.x = Float.parseFloat(vals[1]);
		ret.y = Float.parseFloat(vals[2]);
		
		return ret;
	}
	
	/**
	 * Parses a list of three floats.
	 * @param str
	 * @return a Vector3f with our results
	 */
	public static Vector3f parse3FloatList (String str) {
		Vector3f ret = new Vector3f();
		String[] vals = str.split("\\s+");
		ret.x = Float.parseFloat(vals[1]);
		ret.y = Float.parseFloat(vals[2]);
		ret.z = Float.parseFloat(vals[3]);
		
		return ret;
	}
}
