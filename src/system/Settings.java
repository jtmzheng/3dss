package system;

import java.util.HashMap;

/*
 * A static utility class defining the settings globally used in the applications.
 * This creates an instance at load time. There isn't a need to use a singleton
 * because we don't need control over when to create or destroy this, it needs
 * to be initialized at load time.
 */
public class Settings {
	
	public static HashMap<String, Object> settings = new HashMap<String, Object> ();
	
	// List of default settings.
	static {
		settings.put("Fullscreen", true);
		settings.put("Framerate", 40);
		settings.put("in_Position", 0);
		settings.put("in_Color", 1);
		settings.put("in_TextureCoord", 2);
		settings.put("in_Normal", 3);
		settings.put("vertex_path", "src/shaders/vertex.vert");
		settings.put("fragment_path",  "src/shaders/fragment.frag");
	}
	
	public static void putInteger (String key, int val) {
		settings.put(key, new Integer(val));
	}
	
	public static void putString (String key, String val) {
		settings.put(key, val);
	}
	
	public static void putFloat (String key, float val) {
		settings.put(key, new Float(val));
	}
	
	public static int getInteger (String key) {
		Integer val = (Integer) settings.get(key);
		
		if (val == null) 
			throw new IllegalArgumentException ("Key '" + val + "' does not exist");
		
		return val.intValue();
	}
	
	public static String getString (String key) {
		String val = (String) settings.get(key);
		
		if (val == null) 
			throw new IllegalArgumentException ("Key '" + val + "' does not exist");
		
		return val;
	}
	
	public static float getFloat (String key) {
		Float val = (Float) settings.get(key);
		
		if (val == null) 
			throw new IllegalArgumentException ("Key '" + val + "' does not exist");
		
		return val.floatValue();
	}
	
	public static HashMap<String, Float> getDefaultPlayerAttributes () {
		HashMap<String, Float> playerAttributes = new HashMap<String, Float>();
		playerAttributes.put("HP", 100f);
		playerAttributes.put("SHIELD", 100f);
		return playerAttributes;
	}
	
	public static String getStringRepresentation () {
		String ret = "";
		for (String str : settings.keySet()) {
			ret += str + ": [" + settings.get(str).toString() + "]";
			ret += "\r\n";
		}
		return ret;
	}
}
