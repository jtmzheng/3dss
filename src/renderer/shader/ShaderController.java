package renderer.shader;

import java.util.Map;


/**
 * ShaderController will manage the current shader program
 * @TODO: Refactor (For real this time)
 * @author Max 
 */
public class ShaderController {	
	private static int projectionMatrixLocation = -1;
	private static int viewMatrixLocation = -1;
	private static int modelMatrixLocation = -1;
	private static int lightPositionLocation = -1;
	private static int specularLocation = -1;
	private static int diffuseLocation = -1;
	private static int ambientLocation = -1;
	private static int viewMatrixFragLocation = -1;
	private static int textureSamplerLocation = -1;
	private static int textureKdSamplerLocation = -1;
	private static int textureKsSamplerLocation = -1;
	private static int textureKaSamplerLocation = -1;
	private static int fogEnabledLocation = -1;
	private static int fogColorLocation = -1;
	private static int fogMinDistanceLocation = -1;
	private static int fogMaxDistanceLocation = -1;
	private static int fbTextureSamplerLocation = -1;
	private static int uniqueIdLocation = -1;
	private static int selectedModelLocation = -1;
	
	private static int currentProgram = 0;

	/**
	 * Do not allow instantiation of this class
	 */
	private ShaderController() {}
	
	/**
	 * Currently setProgram is written like this so different shaders can be 
	 * written for different graphics settings.
	 * 
	 * @return <code>true</code> if the program was successfully set, and false otherwise.
	 */
	public static boolean setProgram(ShaderProgram program){

		//Sets the new current program
		currentProgram = program.getProgram();
		Map<String, Integer> uniformLocations = program.getUniforms();

		// Get matrices uniform locations
		projectionMatrixLocation = uniformLocations.containsKey("projectionMatrix") ? uniformLocations.get("projectionMatrix") : -1; 
		viewMatrixLocation = uniformLocations.containsKey("viewMatrix") ? uniformLocations.get("viewMatrix") : -1; 
		modelMatrixLocation = uniformLocations.containsKey("modelMatrix") ? uniformLocations.get("modelMatrix") : -1; 
		viewMatrixFragLocation = uniformLocations.containsKey("viewMatrixFrag") ? uniformLocations.get("viewMatrixFrag") : -1;
		
		// Light uniform location
		ambientLocation = uniformLocations.containsKey("La") ? uniformLocations.get("La") : -1;
		
		// Texture uniform locations
		textureSamplerLocation = uniformLocations.containsKey("textureSampler") ? uniformLocations.get("textureSampler") : -1;
		textureKdSamplerLocation = uniformLocations.containsKey("textureSamplers[0]") ? uniformLocations.get("textureSamplers[0]") : -1;
		textureKsSamplerLocation = uniformLocations.containsKey("textureSamplers[1]") ? uniformLocations.get("textureSamplers[1]") : -1;		
		textureKaSamplerLocation = uniformLocations.containsKey("textureSamplers[2]") ? uniformLocations.get("textureSamplers[2]") : -1;  
		
		// Fog uniform locations
		fogEnabledLocation = uniformLocations.containsKey("fogOn") ? uniformLocations.get("fogOn") : -1;  
		fogColorLocation = uniformLocations.containsKey("fogColor") ? uniformLocations.get("fogColor") : -1;  
		fogMinDistanceLocation = uniformLocations.containsKey("fogMinDistance") ? uniformLocations.get("fogMinDistance") : -1;  
		fogMaxDistanceLocation = uniformLocations.containsKey("fogMaxDistance") ? uniformLocations.get("fogMaxDistance") : -1;  
		
		// Sampler for the FB texture
		fbTextureSamplerLocation = uniformLocations.containsKey("fbTex") ? uniformLocations.get("fbTex") : -1; 
		
		// Unique ID location
		uniqueIdLocation = uniformLocations.containsKey("uniqueId") ? uniformLocations.get("uniqueId") : -1;
		
		// Selected model location
		selectedModelLocation = uniformLocations.containsKey("selectedModel") ? uniformLocations.get("selectedModel") : -1;
		
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
	
	public static int getFBTexLocation(){
		return fbTextureSamplerLocation;
	}
	
	public static int getUniqueIdLocation(){
		return uniqueIdLocation;
	}
	
	public static int getSelectedModelLocation(){
		return selectedModelLocation;
	}
}