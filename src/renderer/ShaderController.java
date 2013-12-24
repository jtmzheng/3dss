package renderer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;

import system.Settings;

/**
 * ShaderController will manage our shaders.
 * @author Max 
 */
public class ShaderController {

	private Map<String, Integer> shaderNameToID = null;
	private Map<Integer, Integer> shaderIDToType = null;
	
	private static int projectionMatrixLocation = 0;
	private static int viewMatrixLocation = 0;
	private static int modelMatrixLocation = 0;
	private static int lightPositionLocation = 0;
	private static int specularLocation = 0;
	private static int diffuseLocation = 0;
	private static int ambientLocation = 0;
	private static int viewMatrixFragLocation = 0;
	private static int textureSamplerLocation = 0;
	private static int textureKdSamplerLocation = 0;
	private static int textureKsSamplerLocation = 0;
	private static int textureKaSamplerLocation = 0;
	
	private static int currentProgram = 0;

	/**
	 * Creates our ShaderController.
	 */
	public ShaderController() {
		shaderNameToID = new HashMap<String, Integer>();
		shaderIDToType = new HashMap<Integer, Integer>();
	}
	
	/**
	 * Currently setProgram is written like this so different shaders can be 
	 * written for different graphics settings.
	 * 
	 * @return <code>true</code> if the program was successfully set, and false otherwise.
	 */
	public boolean setProgram(Map<String, Integer> shaders){

		//Sets the new current program
		currentProgram = GL20.glCreateProgram();
		
		for(String file : shaders.keySet()){
			int shaderID = this.loadShader(file, shaders.get(file));
			GL20.glAttachShader(currentProgram, shaderID);
			shaderNameToID.put(file, shaderID);
			shaderIDToType.put(shaderID, shaders.get(file));
		}
		
		
		// Binding attribute mappings defined in our Settings class.
		GL20.glBindAttribLocation(currentProgram, Settings.getInteger("in_Position"), "in_Position");
		GL20.glBindAttribLocation(currentProgram, Settings.getInteger("in_Color"), "in_Color");
		GL20.glBindAttribLocation(currentProgram, Settings.getInteger("in_TextureCoord"), "in_TextureCoord");
		GL20.glBindAttribLocation(currentProgram, Settings.getInteger("in_Normal"), "in_Normal");
		GL20.glBindAttribLocation(currentProgram, Settings.getInteger("Ks"), "Ks");
		GL20.glBindAttribLocation(currentProgram, Settings.getInteger("Ka"), "Ka");
		GL20.glBindAttribLocation(currentProgram, Settings.getInteger("specExp"), "specExp");
		GL20.glBindAttribLocation(currentProgram, Settings.getInteger("texture"), "texture");

		GL20.glLinkProgram(currentProgram);
		GL20.glValidateProgram(currentProgram);

		// Get matrices uniform locations
		projectionMatrixLocation = GL20.glGetUniformLocation(currentProgram, "projectionMatrix");
		viewMatrixLocation = GL20.glGetUniformLocation(currentProgram,  "viewMatrix");
		modelMatrixLocation = GL20.glGetUniformLocation(currentProgram,  "modelMatrix");
		viewMatrixFragLocation = GL20.glGetUniformLocation(currentProgram, "viewMatrixFrag");
		// lightPositionLocation = GL20.glGetUniformLocation(currentProgram,  "light_position");
		// specularLocation = GL20.glGetUniformLocation(currentProgram,  "Ls");
		// diffuseLocation = GL20.glGetUniformLocation(currentProgram,  "Ld");
		ambientLocation = GL20.glGetUniformLocation(currentProgram,  "La");		
		textureSamplerLocation = GL20.glGetUniformLocation(currentProgram, "textureSampler");
		textureKdSamplerLocation = GL20.glGetUniformLocation(currentProgram, "textureSamplers[0]"); 
		textureKsSamplerLocation = GL20.glGetUniformLocation(currentProgram, "textureSamplers[1]");		
		textureKaSamplerLocation = GL20.glGetUniformLocation(currentProgram, "textureSamplers[2]");
		
		return true;
	}
	
	
	/**
	 * Gets the current program
	 * @return an integer defining the current program
	 */
	public static int getCurrentProgram(){
		return currentProgram;
	}
	
	/**
	 * Gets the model matrix location.
	 * @return the location of the model matrix
	 */
	public static int getModelMatrixLocation(){
		return modelMatrixLocation;
	}
	
	/**
	 * Gets the projection matrix location
	 * @return the location of the projection matrix
	 */
	public static int getProjectionMatrixLocation(){
		return projectionMatrixLocation;
	}
	
	/**
	 * Gets the view matrix location.
	 * @return the location of the view matrix
	 */
	public static int getViewMatrixLocation(){
		return viewMatrixLocation;
	}
	
	public static int getLightPositionLocation(){
		return lightPositionLocation;
	}
	
	public static int getSpecularLocation(){
		return specularLocation;
	}
	
	public static int getDiffuseLocation(){
		return diffuseLocation;
	}
	
	public static int getAmbientLocation(){
		return ambientLocation;
	}
	
	public static int getViewMatrixFragLocation(){
		return viewMatrixFragLocation;
	}
	
	public static int getTexSamplerLocation(){
		return textureSamplerLocation;
	}
	
	public static int getTexKdSamplerLocation(){
		return textureKdSamplerLocation;
	}
	
	public static int getTexKsSamplerLocation(){
		return textureKsSamplerLocation;
	}
	
	public static int getTexKaSamplerLocation(){
		return textureKaSamplerLocation;
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
