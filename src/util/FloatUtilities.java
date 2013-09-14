package util;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Defines a static utility class relating to floats.
 * @author Adi
 *
 */
public class FloatUtilities {
	
	public static Vector3f FTV3 (float [] f) {
		return new Vector3f (f[0], f[1], f[2]);
	}
	
	public static Vector2f FTV2 (float [] f) {
		return new Vector2f (f[0], f[1]);
	}
}
