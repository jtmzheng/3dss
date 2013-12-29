package renderer;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;

/**
 * Class handles fogging for the renderer
 * @author Max
 */
public class Fog {
	// Default parameters for Fog
	public final static Vector3f DEFAULT_COLOR = new Vector3f(0.2f, 0.2f, 0.2f);
	public final static float DEFAULT_MIN_DISTANCE = 2.0f;
	public final static float DEFAULT_MAX_DISTANCE = 10.0f;

	private Vector3f color;
	private float minDistance;
	private float maxDistance;
	private boolean enabled;
	
	private FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(3);
	
	/**
	 * Default constructor 
	 * @param enabled Whetehr the Fog is enabled or not
	 */
	public Fog(boolean enabled) {
		this.color = DEFAULT_COLOR;
		this.minDistance = DEFAULT_MIN_DISTANCE;
		this.maxDistance = DEFAULT_MAX_DISTANCE;
		this.enabled = enabled;
	}
	
	/**
	 * Constructor for the Fog class 
	 * @param minDistance The minimum distance before fog is mixed
	 * @param maxDistance The maximum distance before color is entirely the fog color
	 * @param enabled Whether the Fog is enabled
	 */
	public Fog(float minDistance,
			float maxDistance,
			boolean enabled) {
		this.color = DEFAULT_COLOR;
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
		this.enabled = enabled;
	}
	
	/**
	 * Constructor for the Fog class 
	 * @param color The color of the fog
	 * @param enabled Whether the Fog is enabled
	 */
	public Fog(Vector3f color,
			boolean enabled) {
		this.color = color;
		this.minDistance = DEFAULT_MIN_DISTANCE;
		this.maxDistance = DEFAULT_MAX_DISTANCE;
		this.enabled = enabled;
	}
	
	/**
	 * Constructor for the Fog class 
	 * @param color The color of the fog
	 * @param minDistance The minimum distance before fog is mixed
	 * @param maxDistance The maximum distance before color is entirely the fog color
	 * @param enabled Whether the Fog is enabled
	 */
	public Fog(Vector3f color,
			float minDistance,
			float maxDistance,
			boolean enabled) {
		this.color = color;
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
		this.enabled = enabled;
	}
	
	/**
	 * Update the uniform variables in the fragment shader
	 * @param colorLocation the fog color location (vec3) 
	 * @param minLocation the minimum distance location (float)
	 * @param maxLocation the maximum distance location (float)
	 * @param enabledLocation location for fog flag (int)
	 * @return success whether the uniforms were updated successfully
	 */
	public boolean updateFogUniforms(int colorLocation,
			int minLocation,
			int maxLocation,
			int enabledLocation) {
		
		// Fail if any location is invalid
		if(colorLocation < 0 || minLocation < 0 || maxLocation < 0 || enabledLocation < 0)
			return false;
		
		// Set the color
		color.store(dataBuffer);
		dataBuffer.flip();
		GL20.glUniform3(colorLocation, dataBuffer);
		
		// Set the min and max distances	
		GL20.glUniform1f(minLocation, minDistance);
		GL20.glUniform1f(maxLocation, maxDistance);
		
		// Set enabled flag
		GL20.glUniform1i(enabledLocation, enabled ? 1 : 0);
		
		return true;
	}
	
	/**
	 * Update the fog color uniform variable
	 * @param colorLocation the uniform location in the shader (vec3)
	 * @return success whether the color was updated successfully
	 */
	public boolean updateFogColorUniform(int colorLocation) {
		if(colorLocation < 0) 
			return false;
		
		// Set the color
		color.store(dataBuffer);
		dataBuffer.flip();
		GL20.glUniform3(colorLocation, dataBuffer);
		
		return true;
	}
	
	/**
	 * @param minLocation
	 * @return
	 */
	public boolean updateFogMinDistanceUniform(int minLocation) {
		if(minLocation < 0) 
			return false;
		
		// Set the min and max distances	
		GL20.glUniform1f(minLocation, minDistance);
		return true;
	}
	
	/**
	 * 
	 * @param maxLocation
	 * @return
	 */
	public boolean updateFogMaxDistanceUniform(int maxLocation) {
		if(maxLocation < 0) 
			return false;
		
		// Set the min and max distances	
		GL20.glUniform1f(maxLocation, minDistance);
		return true;
	}
	
	/**
	 * 
	 * @param enabledLocation
	 * @return
	 */
	public boolean updateEnabledUniform(int enabledLocation) {
		if(enabledLocation < 0) 
			return false;
		
		// Set enabled flag
		GL20.glUniform1i(enabledLocation, enabled ? 1 : 0);		
		return true;
	}

	/**
	 * Get the minimum distance for fogging to occur
	 * @return minDistance minimum distance
	 */
	public float getMinDistance() {
		return minDistance;
	}
	
	/**
	 * Get the distance where fragment is entirely fogged
	 * @return maxDistance maximum distance
	 */
	public float getMaxDistance() {
		return maxDistance;
	}
	
	/**
	 * Get the fog colour 
	 * @return color fog color
	 */
	public Vector3f getColor() {
		return color;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getEnabled() {
		return enabled;
	}
	
	/**
	 * 
	 * @param minDistance
	 */
	public void setMinDistance(float minDistance) {
		this.minDistance = minDistance;
	}
	
	/**
	 * 
	 * @param maxDistance
	 */
	public void setMaxDistance(float maxDistance) {
		this.maxDistance = maxDistance;
	}
	
	/**
	 * 
	 * @param color
	 */
	public void setColor(Vector3f color) {
		this.color = color;
	}
	
	/**
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
