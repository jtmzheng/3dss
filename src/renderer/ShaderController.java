package renderer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;

import system.Settings;

/**
 * ShaderController will manage the current shader program
 * @TODO: Refactor
 * @TODO: Shader class
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
	private static int fogEnabledLocation = 0;
	private static int fogColorLocation = 0;
	private static int fogMinDistanceLocation = 0;
	private static int fogMaxDistanceLocation = 0;
	
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
	public boolean setProgram(ShaderProgram program){

		//Sets the new current program
		currentProgram = program.getProgram();
		
		// Bind attributes
		for(String attribute : program.getAttributes()) {
			GL20.glBindAttribLocation(currentProgram, program.getAttributeValue(attribute), attribute);
		}

		GL20.glLinkProgram(currentProgram);
		GL20.glValidateProgram(currentProgram);

		// Get matrices uniform locations
		projectionMatrixLocation = GL20.glGetUniformLocation(currentProgram, "projectionMatrix");
		viewMatrixLocation = GL20.glGetUniformLocation(currentProgram,  "viewMatrix");
		modelMatrixLocation = GL20.glGetUniformLocation(currentProgram,  "modelMatrix");
		viewMatrixFragLocation = GL20.glGetUniformLocation(currentProgram, "viewMatrixFrag");
		
		// Light uniform location
		ambientLocation = GL20.glGetUniformLocation(currentProgram,  "La");
		
		// Texture uniform locations
		textureSamplerLocation = GL20.glGetUniformLocation(currentProgram, "textureSampler");
		textureKdSamplerLocation = GL20.glGetUniformLocation(currentProgram, "textureSamplers[0]"); 
		textureKsSamplerLocation = GL20.glGetUniformLocation(currentProgram, "textureSamplers[1]");		
		textureKaSamplerLocation = GL20.glGetUniformLocation(currentProgram, "textureSamplers[2]");
		
		// Fog uniform locations
		fogEnabledLocation = GL20.glGetUniformLocation(currentProgram, "fogOn");
		fogColorLocation = GL20.glGetUniformLocation(currentProgram, "fogColor");
		fogMinDistanceLocation = GL20.glGetUniformLocation(currentProgram, "fogMinDistance");
		fogMaxDistanceLocation = GL20.glGetUniformLocation(currentProgram, "fogMaxDistance");
		
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
	
	public static int getFogEnabledLocation(){
		return fogEnabledLocation;
	}
	
	public static int getFogColorLocation(){
		return fogColorLocation;
	}
	
	public static int getFogMinDistanceLocation(){
		return fogMinDistanceLocation;
	}
	
	public static int getFogMaxDistanceLocation(){
		return fogMaxDistanceLocation;
	}
}