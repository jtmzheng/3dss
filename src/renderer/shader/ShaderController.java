package renderer.shader;

import java.util.HashMap;
import java.util.Map;


/**
 * ShaderController will manage the current shader program
 * @TODO(MZ): Remove deprecated helpers
 * @TODO(MZ): Add in methods to get properties of program (name, shaders, etc)
 * @author Max 
 */
public class ShaderController {	
	private static int currentProgram = 0;
	private static Map<String, Integer> uniformLocations = new HashMap<>();

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
		uniformLocations = program.getUniforms();
		
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
	 * True if uniform exists in current program
	 * @param strName
	 * @return true if uniform exists in current program
	 */
	public static boolean hasUniform(String strName) {
		return uniformLocations.containsKey(strName);
	}
	
	/**
	 * Get the uniform location from a uniform name (no guarentee on type)
	 * @param strName
	 * @return location if it exists or -1
	 */
	public static int getUniformFromName(String strName) {
		return uniformLocations.containsKey(strName) ? uniformLocations.get(strName) : -1;
	}
	
	/**
	 * Gets the model matrix location.
	 * @return the location of the model matrix
	 */
	public static int getModelMatrixLocation(){
		return uniformLocations.containsKey("modelMatrix") ? uniformLocations.get("modelMatrix") : -1;
	}
	
	/**
	 * Gets the projection matrix location
	 * @return the location of the projection matrix
	 */
	public static int getProjectionMatrixLocation(){
		return uniformLocations.containsKey("projectionMatrix") ? uniformLocations.get("projectionMatrix") : -1;
	}
	
	/**
	 * Gets the view matrix location.
	 * @return the location of the view matrix
	 */
	public static int getViewMatrixLocation(){
		return uniformLocations.containsKey("viewMatrix") ? uniformLocations.get("viewMatrix") : -1;
	}
	
	public static int getNormalMatrixLocation(){
		return uniformLocations.containsKey("normMatrix") ? uniformLocations.get("normMatrix") : -1;
	}
	
	public static int getAmbientLocation(){
		return uniformLocations.containsKey("La") ? uniformLocations.get("La") : -1;
	}
	
	public static int getTexSamplerLocation(){
		return uniformLocations.containsKey("textureSampler") ? uniformLocations.get("textureSampler") : -1;
	}
	
	public static int getTexKdSamplerLocation(){
		return uniformLocations.containsKey("textureSamplers[0]") ? uniformLocations.get("textureSamplers[0]") : -1;
	}
	
	public static int getTexKsSamplerLocation(){
		return uniformLocations.containsKey("textureSamplers[1]") ? uniformLocations.get("textureSamplers[1]") : -1;
	}
	
	public static int getTexKaSamplerLocation(){
		return uniformLocations.containsKey("textureSamplers[2]") ? uniformLocations.get("textureSamplers[2]") : -1;
	}
	
	public static int getFogEnabledLocation(){
		return uniformLocations.containsKey("fogOn") ? uniformLocations.get("fogOn") : -1;
	}
	
	public static int getFogColorLocation(){
		return uniformLocations.containsKey("fogColor") ? uniformLocations.get("fogColor") : -1;
	}
	
	public static int getFogMinDistanceLocation(){
		return uniformLocations.containsKey("fogMinDistance") ? uniformLocations.get("fogMinDistance") : -1;
	}
	
	public static int getFogMaxDistanceLocation(){
		return uniformLocations.containsKey("fogMaxDistance") ? uniformLocations.get("fogMaxDistance") : -1;
	}
	
	public static int getFBTexLocation(){
		return uniformLocations.containsKey("fbTex") ? uniformLocations.get("fbTex") : -1;
	}
	
	public static int getDepthTextureLocation(){
		return uniformLocations.containsKey("depthBuffTex") ? uniformLocations.get("depthBuffTex") : -1;
	}
	
	public static int getUniqueIdLocation(){
		return uniformLocations.containsKey("uniqueId") ? uniformLocations.get("uniqueId") : -1;
	}
	
	public static int getSelectedModelLocation(){
		return uniformLocations.containsKey("selectedModel") ? uniformLocations.get("selectedModel") : -1;
	}
	
	public static int getCubeTextureLocation(){
		return uniformLocations.containsKey("cubeTexture") ? uniformLocations.get("cubeTexture") : -1;
	}
	
	public static int getNearPlaneLocation() {
		return uniformLocations.containsKey("nearPlane") ? uniformLocations.get("nearPlane") : -1;
	}
	
	public static int getFarPlaneLocation() {
		return uniformLocations.containsKey("farPlane") ? uniformLocations.get("farPlane") : -1;
	}

	public static int getNormalTextureLocation() {
		return uniformLocations.containsKey("normalTex") ? uniformLocations.get("normalTex") : -1;
	}
	
	public static int getNoiseTextureLocation() {
		return uniformLocations.containsKey("noiseTex") ? uniformLocations.get("noiseTex") : -1;
	}
}