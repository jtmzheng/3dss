package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderer.model.Face;
import texture.Material;

/**
 * This class parses .obj and .mtl files.
 * .obj files reference .mtl files by the tag "mtllib". In .mtl files, there are "newmtl"
 * tags which declare new materials (which we represent by Material.java). These "newmtl"s 
 * have properties which include texture maps and lighting values.
 * 
 * @TODO: Support smoothing groups.
 * @TODO: Add error checking 
 * @TODO: Add documentation
 * @author Adi
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
    private final static String MTL_ILLUM = "illum";
    private final static String MTL_NI = "Ni";
    private final static String MTL_MAP_KA = "map_Ka";
    private final static String MTL_MAP_KD = "map_Kd";
    private final static String MTL_MAP_KS = "map_Ks";
    
    // Materials in this object.
    private Map<String, Material> materialMapping = new HashMap<String, Material>();
    
    // Current material being parsed. Initialize with default material.
    private Material currentMaterial = new Material();
    
    // List of obj properties that are used.
    private List<Vector3f> vertices;
    private List<Vector2f> textures;
    private List<Vector3f> normals;
    
    // List of faces.
    private static List<Face> faces;
    
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
    	
    	parserThread.join();
    	System.out.println("New OBJ Loading Time: " + (System.currentTimeMillis() - curTime));
    }
    
    private void parseTexture (String line) {
    	textures.add(ParseUtils.parse2FloatList(line));
    }
    
    private void parseNormal (String line) {
    	normals.add(ParseUtils.parse3FloatList(line));
    }

    private void parseVertex (String line) {
    	vertices.add(ParseUtils.parse3FloatList(line));
    }

    private void parseFace (String line) {
    	faces.add(ParseUtils.parseFace(line, vertices, normals, textures, currentMaterial));
    }
    
    private void parseUseMTL (String line) {
    	line = line.split(" ")[1];
    	currentMaterial = materialMapping.get(line);
    }
    
    private void parseMTLLib (String line) {
    	String[] libNames = line.substring(OBJ_MTLLIB.length()).trim().split(" ");
    	
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
     * Parses .mtl files referenced by .obj files.
     * @param fileName
     * @throws IOException
     * @throws FileNotFoundException
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
			if (line.startsWith(MTL_ILLUM)) parseIllum(line);
			if (line.startsWith(MTL_NS)) parseNs(line);
			if (line.startsWith(MTL_NI)) parseNi(line);
			if (line.startsWith(MTL_MAP_KA)) parseMapKa(line);
			if (line.startsWith(MTL_MAP_KD)) parseMapKd(line);
			if (line.startsWith(MTL_MAP_KS)) parseMapKs(line);
		}
		
		br.close();
    }
    
    private void parseNewMTL (String line) {
    	line = line.substring(MTL_NEWMTL.length()).trim();
    	
    	// Create a new material and set it to the current material being parsed.
    	currentMaterial = new Material(line);
    	materialMapping.put(line, this.currentMaterial);
    }

    private void parseKa (String line) {
    	String[] tokens = line.split(" ");
    	currentMaterial.Ka = new float[] {Float.parseFloat(tokens[1]),
					   Float.parseFloat(tokens[2]),
					   Float.parseFloat(tokens[3])};
    }

    private void parseKd (String line) {
    	String[] tokens = line.split(" ");
    	if (tokens.length == 5) {
        	currentMaterial.Kd = new float[] {Float.parseFloat(tokens[1]),
					   Float.parseFloat(tokens[2]),
					   Float.parseFloat(tokens[3]),
					   Float.parseFloat(tokens[4])};
    	} else {
        	currentMaterial.Kd = new float[] {Float.parseFloat(tokens[1]),
					   Float.parseFloat(tokens[2]),
					   Float.parseFloat(tokens[3]),
					   1.0f};
        }
    }

    private void parseIllum (String line) {
    	line = line.substring(MTL_ILLUM.length()).trim();
    	currentMaterial.illumModel = Integer.parseInt(line);
    }
    
    private void parseNs (String line) {
    	line = line.substring(MTL_NS.length()).trim();
    	currentMaterial.Ns = Float.parseFloat(line);
    }

    private void parseNi (String line) {
    	line = line.substring(MTL_NI.length()).trim();
    	currentMaterial.niOpticalDensity = Float.parseFloat(line);
    }
    
    private void parseKs (String line) {
    	String[] tokens = line.split("\\s+");
    	currentMaterial.Ks = new float[] {Float.parseFloat(tokens[1]),
					   Float.parseFloat(tokens[2]),
					   Float.parseFloat(tokens[3])};
    }

    private void parseMapKa (String line) {
    	currentMaterial.setMapKaFile(line.split(" ")[1]);
    }
    
    private void parseMapKd (String line) {
    	currentMaterial.setMapKdFile(line.split(" ")[1]);
    }
    
    private void parseMapKs (String line) {
    	currentMaterial.setMapKsFile(line.split(" ")[1]);
    }
   
    // Getters.
    public List<Vector3f> getVertices () { return vertices; }
    public List<Vector2f> getTextures () { return textures; }
    public List<Vector3f> getNormals () { return normals; }
    public List<Face> getFaces () { return faces; }
    
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
}