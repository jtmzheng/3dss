package util;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Static utility class to handle things string related (EG regex stuff, parsing, ETC)
 * @author Adi
 *
 */
public class StringUtilities {
	
	public static Vector2f parse2FloatList (String str) {
		Vector2f ret = new Vector2f();
		String[] vals = str.split("\\s+");
		ret.x = Float.parseFloat(vals[1]);
		ret.y = Float.parseFloat(vals[2]);
		
		return ret;
	}
	
	public static Vector3f parse3FloatList (String str) {
		Vector3f ret = new Vector3f();
		String[] vals = str.split("\\s+");
		ret.x = Float.parseFloat(vals[1]);
		ret.y = Float.parseFloat(vals[2]);
		ret.z = Float.parseFloat(vals[3]);
		
		return ret;
	}
	
	public static void main (String[] args) {
		String s = "f 1/2/3 4/5/6 7/8/9";
		String[] tokens = s.split("\\s+");
		
		System.out.println(tokens[1].matches("^[1-9]/[1-9]/[1-9]$"));
	}
}
