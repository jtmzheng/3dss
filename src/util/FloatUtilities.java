package util;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Defines a static utility class for anything relating to floats.
 * @author Adi
 */
public class FloatUtilities {
	
	/**
	 * Utilities class prevents instantiation
	 */
	private FloatUtilities() {}
	
	/**
	 * Converts a 3 float array to a Vector3f object.
	 * @param f
	 * @return the converted vector
	 */
	public static Vector3f FTV3 (float [] f) {
		return new Vector3f (f[0], f[1], f[2]);
	}
	
	/**
	 * Converts a 2 float array to a Vector2f object.
	 * @param f
	 * @return the converted vector
	 */
	public static Vector2f FTV2 (float [] f) {
		return new Vector2f (f[0], f[1]);
	}
}