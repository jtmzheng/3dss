package util;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Static utility class to handle anything string related (EG. regex stuff, parsing, ETC).
 * @author Adi
 *
 */
public class StringUtilities {
	
	/**
	 * Parses a list of two floats.
	 * @param str
	 * @return ret A Vector2f with our results.
	 */
	public static Vector2f parse2FloatList (String str) {
		Vector2f ret = new Vector2f();
		String[] vals = str.split("\\s+");
		ret.x = Float.parseFloat(vals[1]);
		ret.y = Float.parseFloat(vals[2]);
		
		return ret;
	}
	
	/**
	 * Parses a list of three floats.
	 * @param str
	 * @return ret A Vector3f with our results.
	 */
	public static Vector3f parse3FloatList (String str) {
		Vector3f ret = new Vector3f();
		String[] vals = str.split("\\s+");
		ret.x = Float.parseFloat(vals[1]);
		ret.y = Float.parseFloat(vals[2]);
		ret.z = Float.parseFloat(vals[3]);
		
		return ret;
	}
}
