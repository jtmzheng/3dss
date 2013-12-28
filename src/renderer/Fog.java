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
	private Vector3f color;
	private float minDistance;
	private float maxDistance;
	
	private FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(3);
	
	/**
	 * Constructor for the Fog class 
	 * @param colour 
	 * @param minDistance
	 * @param maxDistance
	 */
	public Fog(Vector3f color,
			float minDistance,
			float maxDistance) {
		this.color = color;
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
	}
	
	/**
	 * Update the uniform variables in the fragment shader
	 * @param colorLocation the fog color location (vec3) 
	 * @param minLocation the minimum distance location (float)
	 * @param maxLocation the maximum distance location (float)
	 * @return success whether the uniforms were updated successfully
	 */
	public boolean updateFog(int colorLocation,
			int minLocation,
			int maxLocation) {
		
		// Fail if any location is invalid
		if(colorLocation < 0 || minLocation < 0 || maxLocation < 0)
			return false;
		
		// Set the color
		color.store(dataBuffer);
		dataBuffer.flip();
		GL20.glUniform3(colorLocation, dataBuffer);
		
		// Set the min and max distances	
		GL20.glUniform1f(minLocation, minDistance);
		GL20.glUniform1f(maxLocation, maxDistance);
		
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
	
	public void setMinDistance(float minDistance) {
		this.minDistance = minDistance;
	}
	
	public void setMaxDistance(float maxDistance) {
		this.maxDistance = maxDistance;
	}
	
	public void setColor(Vector3f color) {
		this.color = color;
	}
}
