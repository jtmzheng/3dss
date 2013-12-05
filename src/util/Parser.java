package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderer.Face;
import renderer.VertexData;
import texture.Material;


/**
 * This class parses obj/mtl files.
 * @author Adi
 *
 */
public class Parser {
	// Parsing token in OBJ/MTL files. 
	private final static String OBJ_COMMENT = "#";
    private final static String OBJ_VERTEX_TEXTURE = "vt";
    private final static String OBJ_VERTEX_NORMAL = "vn";
    private final static String OBJ_VERTEX = "v";
    private final static String OBJ_FACE = "f";
    private final static String OBJ_USEMTL = "usemtl";
    private final static String OBJ_MTLLIB = "mtllib";
    private final static String MTL_NEWMTL = "newmtl";
    private final static String MTL_KA = "Ka";
    private final static String MTL_KD = "Kd";
    private final static String MTL_KS = "Ks";
    private final static String MTL_NS = "Ns";
    
    // Default material.
    private Material currentMaterial = new Material(null);
    
    // List of obj properties that are used.
    private List<Vector3f> vertices;
    private List<Vector2f> textures;
    private List<Vector3f> normals;
    
    // List of faces.
    private List<Face> faces;
    
    // DataParser is used to slightly improve performance (does actual parsing).
    private ArrayBlockingQueue<String> parsedData;
    private volatile boolean isDone;
    
    private File objFile = null;
    
    /**
     * Initializes our parser.
     */
    public Parser() {
    	vertices = new ArrayList<Vector3f>();
    	textures = new ArrayList<Vector2f>();
    	normals = new ArrayList<Vector3f>();
    	faces = new ArrayList<Face>();
    	parsedData = new ArrayBlockingQueue<String>(10000);
    }
    
    /**
     * Parses an OBJ file.
     * @param file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void parseOBJFile (File file) throws IOException, InterruptedException {
    	objFile = file;
    	long curTime = System.currentTimeMillis();
    	String line = null;
    	Runnable parser = new DataParser(objFile);
    	Thread parserThread = new Thread(parser);
    	isDone = false;
    	parserThread.start();
    	
    	while (!isDone || !parsedData.isEmpty()) {
    		line = parsedData.poll();
    		if(line == null) continue;
    
			line = line.trim();
    		if (line.startsWith(OBJ_VERTEX_TEXTURE)) parseTexture(line);
    		else if (line.startsWith(OBJ_VERTEX_NORMAL)) parseNormal(line);
    		else if (line.startsWith(OBJ_VERTEX)) parseVertex(line);
    		else if (line.startsWith(OBJ_FACE)) parseFace(line);
    		else if (line.startsWith(OBJ_MTLLIB)) parseMTLLib(line);
    		else if (line.startsWith(OBJ_USEMTL)) parseUseMTL(line);
    	}    	
    	System.out.println("New OBJ Loading Time: " + (System.currentTimeMillis() - curTime));
    }
    
    private class DataParser implements Runnable {
    	
    	private File m_file; //file to read
    	
    	public DataParser(File file){
    		m_file = file;
    	}
    	
    	@Override
    	public void run(){
    		String line = null;

    		try {
    			FileReader fin = new FileReader(m_file);
        		BufferedReader bin = new BufferedReader (fin);
				while ((line = bin.readLine()) != null) {

					if (line.length() == 0) continue;
					if (line.startsWith(OBJ_COMMENT)) continue;
					
					parsedData.put(line);
				}
				bin.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e){ //Allows failure
				e.printStackTrace();
			} finally {
				isDone = true;
			}
    	}
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
    	String[] tokens = line.split("\\s+");
    	List<VertexData> faceData = new ArrayList<VertexData>();
    	if (!line.contains("/")) {
    		for (int x = 1; x < tokens.length; x++) {
    			VertexData v = new VertexData (vertices.get(Integer.parseInt(tokens[x])-1));
    			faceData.add(v);
    		}
    		faces.add(new Face(faceData, currentMaterial));
    		return;
    	}
    	
    	// We should have trimmed whitespace and stuff by this point.
    	if (tokens[1].matches("^[0-9]+/[0-9]+$")) {
    		for (int x = 1; x < tokens.length; x++) {
    			String[] indices = tokens[x].split("/");
    			VertexData v = new VertexData (vertices.get(Integer.parseInt(indices[0]) - 1),
    					textures.get(Integer.parseInt(indices[1]) - 1));
    			
    			faceData.add(v);
    		}
    		faces.add(new Face(faceData, currentMaterial));
    		return;
    	}
    	
    	if (tokens[1].matches("^[0-9]+/[0-9]+/[0-9]+$")) {
    		for (int x = 1; x < tokens.length; x++) {
    			String[] indices = tokens[x].split("/");
    			VertexData v = new VertexData (vertices.get(Integer.parseInt(indices[0]) - 1),
    					textures.get(Integer.parseInt(indices[1]) - 1),
    					normals.get(Integer.parseInt(indices[2]) - 1));
    			
    			faceData.add(v);
    		}
    		faces.add(new Face(faceData, currentMaterial));
    		return;
    	}
    	
    	if (tokens[1].matches("^[0-9]+//[0-9]+$")) {
    		for (int x = 1; x < tokens.length; x++) {
    			String[] indices = tokens[x].split("//");
    			VertexData v = new VertexData (vertices.get(Integer.parseInt(indices[0]) - 1),
    					new Vector2f(0f, 0f),
    					normals.get(Integer.parseInt(indices[1]) - 1));
    			
    			faceData.add(v);
    		}
    		faces.add(new Face(faceData, currentMaterial));
    		return;
    	}
    }
    
    private void parseUseMTL (String line) {}
    
    private void parseMTLLib (String line) {
    	String[] libNames = line.substring(OBJ_MTLLIB.length()).trim().split("\\w+");
    	
    	if (libNames != null) {
    		for (int i = 0; i < libNames.length; i++) {
    			try {
    				parseMTLFile(libNames[i]);
    			} catch (IOException e) {
    				System.err.println("Cannot find MTL filename " + libNames[i]);
    				e.printStackTrace();
    			}
    		}
    	}
    }
    
    /**
     * Parses a MTL file.
 	 * @param fileName
     */
    private void parseMTLFile (String fileName) throws IOException, FileNotFoundException {
        File mtlFile = new File(objFile.getParent(), fileName);
        FileReader fr = new FileReader(mtlFile);
        BufferedReader br = new BufferedReader(fr);
        
        String line = null;
		while ((line = br.readLine()) != null) {
			if (line.startsWith(OBJ_COMMENT)) continue;
			if (line.startsWith(MTL_NEWMTL)) parseNewMTL(line);
			if (line.startsWith(MTL_KA)) parseKa(line);
			if (line.startsWith(MTL_KD)) parseKd(line);
			if (line.startsWith(MTL_KS)) parseKs(line);
			if (line.startsWith(MTL_NS)) parseNs(line);
		}
		
		br.close();
    }
    
    private void parseNewMTL (String line) {
    	String[] tokens = line.split("\\w+");
    	String fileName = tokens[1];
    	
    	// Set current material to new material, with the texture as the filename.
    	this.currentMaterial = new Material(fileName);
    }
    
    /**
     * Parses ambiance.
     * @param line
     */
    private void parseKa (String line) {
    	String[] tokens = line.split("\\w+");
    	this.currentMaterial.Ka = new float[] {Float.parseFloat(tokens[1]),
					   Float.parseFloat(tokens[2]),
					   Float.parseFloat(tokens[3])};
    }
    
    /**
     * Parses diffuse.
     * @param line
     */
    private void parseKd (String line) {
    	String[] tokens = line.split("\\w+");
    	if (tokens.length == 5) {
        	this.currentMaterial.Kd = new float[] {Float.parseFloat(tokens[1]),
					   Float.parseFloat(tokens[2]),
					   Float.parseFloat(tokens[3]),
					   Float.parseFloat(tokens[4])};
    	} else {
        	this.currentMaterial.Kd = new float[] {Float.parseFloat(tokens[1]),
					   Float.parseFloat(tokens[2]),
					   Float.parseFloat(tokens[3]),
					   1.0f};
        }
    }
    
    /**
     * Parses Ks.
     * @param line
     */
    private void parseKs (String line) {
    	String[] tokens = line.split("\\w+");
    	this.currentMaterial.Ks = new float[] {Float.parseFloat(tokens[1]),
					   Float.parseFloat(tokens[2]),
					   Float.parseFloat(tokens[3])};
    }
    
    /**
     * Parses Ns.
     * @param line
     */
    private void parseNs (String line) {
    	String[] tokens = line.split("\\w+");
    	this.currentMaterial.Ns = Float.parseFloat(tokens[1]);
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