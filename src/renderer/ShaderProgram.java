package renderer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;

public class ShaderProgram {
	protected Map<String, Integer> shaderAttributes;
	
	private Map<String, Integer> shaderNameToId;
	private Map<Integer, Integer> shaderIdToType;	
	private int programId;
	
	/**
	 * Constructor for the shader program
	 * @param shaders
	 */
	public ShaderProgram(Map<String, Integer> shaders) {		
		programId = GL20.glCreateProgram();
		shaderNameToId = new HashMap<>();
		shaderIdToType = new HashMap<>();
		
		for(String file : shaders.keySet()){
			int shaderId = loadShader(file, shaders.get(file));
			GL20.glAttachShader(programId, shaderId);
			shaderNameToId.put(file, shaderId);
			shaderIdToType.put(shaderId, shaders.get(file));
		}
		
		// Set up the attributes for this program
		setupAttributes();
		
		// Bind attributes
		for(String attribute : getAttributes()) {
			GL20.glBindAttribLocation(programId, getAttributeValue(attribute), attribute);
		}

		// Link and validate the program
		GL20.glLinkProgram(programId);
		GL20.glValidateProgram(programId);
	}
	
	/**
	 * Get the program ID
	 * @return programId
	 */
	public int getProgram() {
		return programId;
	}
	
	/**
	 * Get the attributes associated
	 * @return
	 */
	public Collection<String> getAttributes() {
		return shaderAttributes.keySet();
	}
	
	public Integer getAttributeValue(String name) {
		return shaderAttributes.get(name);
	}
	
	protected void setupAttributes() {
		this.shaderAttributes = new HashMap<>();
	}
	
	/**
	 * Loads a shader from a file.
	 * @param filename Name of shader file.
	 * @param type The shader type.
	 * @return the shader UID
	 */
	private int loadShader(String filename, int type) {
		StringBuilder shaderSource = new StringBuilder();
		int shaderID = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read file.");
			e.printStackTrace();
			System.exit(-1);
		}

		shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		return shaderID;
	}	
}
