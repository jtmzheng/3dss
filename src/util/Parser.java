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
import renderer.VertexData;


/**
 * This class parses obj/mtl files.
 * @author Adi
 *
 */
public class Parser {

	// Testing our parser with cube.obj
	public static void main (String[] args) {
		Parser cubeParser = new Parser();
		try {
			cubeParser.parseOBJFile(new File("res/obj/cow.obj"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// Parsing token in OBJ/MTL files. 
	private final static String OBJ_COMMENT = "#";
    private final static String OBJ_VERTEX_TEXTURE = "vt";
    private final static String OBJ_VERTEX_NORMAL = "vn";
    private final static String OBJ_VERTEX = "v";
    private final static String OBJ_FACE = "f";
    private final static String OBJ_GROUP_NAME = "g";
    private final static String OBJ_OBJECT_NAME = "o";
    private final static String OBJ_POINT = "p";
    private final static String OBJ_LINE = "l";
    private final static String OBJ_USEMTL = "usemtl";
    private final static String MTL_NEWMTL = "newmtl";
    private final static String MTL_KA = "Ka";
    private final static String MTL_KD = "Kd";
    private final static String MTL_KS = "Ks";
    private final static String MTL_TF = "Tf";
    
    private List<Vector3f> vertices;
    private List<Vector2f> textures;
    private List<Vector3f> normals;
    
    private List<Face> faces;
    
    /**
     * Initializes our parser.
     */
    public Parser() {
    	vertices = new ArrayList<Vector3f>();
    	textures = new ArrayList<Vector2f>();
    	normals = new ArrayList<Vector3f>();
    	faces = new ArrayList<Face>();
    }
    
    /**
     * Parses an OBJ file.
     * @param file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void parseOBJFile (File file) throws FileNotFoundException, IOException {
    	String line = null;
    	
    	FileReader fin = new FileReader(file);
    	BufferedReader bin = new BufferedReader (fin);
    	
    	while ((line = bin.readLine()) != null) {
    		line = line.trim();
    		if (line.length() == 0) continue;
    		if (line.startsWith(OBJ_COMMENT)) continue;
    
    		if (line.startsWith(OBJ_VERTEX_TEXTURE)) parseTexture(line);
    		else if (line.startsWith(OBJ_VERTEX_NORMAL)) parseNormal(line);
    		else if (line.startsWith(OBJ_VERTEX)) parseVertex(line);
    		else if (line.startsWith(OBJ_FACE)) parseFace(line);
    		else if (line.startsWith(OBJ_GROUP_NAME)) parseGroup(line);
    		else if (line.startsWith(OBJ_POINT)) parsePoint(line);
    		else if (line.startsWith(OBJ_LINE)) parseLine(line);
    		else if (line.startsWith(OBJ_USEMTL)) parseMTL (line);
    	}
    	
    	Logger.flush();
    	bin.close();
    }
    
    /**
     * Parses a texture.
     * @param line
     */
    private void parseTexture (String line) {
    	textures.add(StringUtilities.parse2FloatList(line));
    }
    
    /**
     * Parses a normal.
     * @param line
     */
    private void parseNormal (String line) {
    	normals.add(StringUtilities.parse3FloatList(line));
    }
    
    /**
     * Parses a vertex.
     * @param line
     */
    private void parseVertex (String line) {
    	vertices.add(StringUtilities.parse3FloatList(line));
    }
    
    /**
     * Parses a face.
     * From the OBJ spec, faces can be specified as:
     * f int int int ...
     * OR
     * f int/int int/int int/int . . .
     * OR
     * f int/int/int int/int/int int/int/int
     * The numbers are (1 based) indexes into the vertices, texture, and normals lists (respectively).
     * 
     * For no texture vertices, we have the following:
     * f int//int int//int int//int ...
     * 
     * Consistency in the triplets is required. EG this is illegal:
     * f 1/1/1 2/2/2 3//3 4//4
     * 
     * @param line
     */
    private void parseFace (String line) {
    	Logger.write(line);
    	String[] tokens = line.split("\\s+");
    	List<VertexData> faceData = new ArrayList<VertexData>();
    	
    	if (!line.contains("/")) {
    		for (int x = 1; x < tokens.length; x++) {
    			VertexData v = new VertexData (vertices.get(Integer.parseInt(tokens[x])-1));
    			faceData.add(v);
    		}
    		
    		faces.add(new Face(faceData));
    		return;
    	}
    	
    	// We should have trimmed whitespace and stuff by this point.
    	if (tokens[1].matches("^[1-9]/[0-9]$")) {
    		for (int x = 1; x < tokens.length; x++) {
    			String[] indices = tokens[x].split("/");
    			VertexData v = new VertexData (vertices.get(Integer.parseInt(indices[0]) - 1),
    										   textures.get(Integer.parseInt(indices[1]) - 1));
    			
    			faceData.add(v);
    		}
    		
    		faces.add(new Face(faceData));
    		return;
    	}
    	
    	if (tokens[1].matches("^[1-9]/[1-9]/[1-9]$")) {
    		for (int x = 1; x < tokens.length; x++) {
    			String[] indices = tokens[x].split("/");
    			VertexData v = new VertexData (vertices.get(Integer.parseInt(indices[0]) - 1),
    										   textures.get(Integer.parseInt(indices[1]) - 1),
    					 					   normals.get(Integer.parseInt(indices[2]) - 1));
    			
    			faceData.add(v);
    		}
    		
    		faces.add(new Face(faceData));
    		return;
    	}
    	
    	if (tokens[1].matches("^[1-9]//[0-9]$")) {
    		for (int x = 1; x < tokens.length; x++) {
    			String[] indices = tokens[x].split("//");
    			VertexData v = new VertexData (vertices.get(Integer.parseInt(indices[0]) - 1),
    					 					   new Vector2f(0f, 0f),
    					 					   normals.get(Integer.parseInt(indices[1]) - 1));
    			
    			faceData.add(v);
    		}
    		
    		faces.add(new Face(faceData));
    		return;
    	}
    }
    
    
    /**
     * Parses a line.
     * @param line
     */
    private void parseLine (String line) {

    }
    
    /**
     * Parses a group.
     * @param line
     */
    private void parseGroup (String line) {
    	
    }
    
    /**
     * Parses a point.
     * @param line
     */
    private void parsePoint (String line) {
    	
    }
    
    /**
     * Parses an mtl tag.
     * @param line
     */
    private void parseMTL (String line) {
    	
    }
    
    /**
     * Gets the list of vertices that this parser parsed.
     * @return the list of vertices
     */
    public List<Vector3f> getVertices () {
    	return vertices;
    }
    
    /**
     * Gets the list of texture that this parser parsed.
     * @return the list of textures
     */
    public List<Vector2f> getTextures () {
    	return textures;
    }
    
    /**
     * Gets the list of normals that this parser parsed.
     * @return the list of normals
     */
    public List<Vector3f> getNormals () {
    	return normals;
    }
    
    /**
     * Gets the list of faces that this parser parsed.
     * @return the list of faces
     */
    public List<Face> getFaces () {
    	return faces;
    }
}
