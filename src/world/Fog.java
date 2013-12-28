package world;

import org.lwjgl.util.vector.Vector3f;

/**
 * Class handles fogging for the renderer
 * @author Max
 */
public class Fog {
	private Vector3f color;
	private float minDistance;
	private float maxDistance;
	
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
