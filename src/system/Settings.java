package system;

import java.util.HashMap;
import java.util.Map;

/*
 * A static utility class defining the settings globally used in the applications.
 * This creates an instance at load time. There isn't a need to use a singleton
 * because we don't need control over when to create or destroy this, it needs
 * to be initialized at load time.
 * 
 * @author Adi
 */
public class Settings {
	
	public static Map<String, Object> settings = new HashMap<String, Object> ();
	
	// List of default settings.
	static {
		settings.put("Fullscreen", true);
		settings.put("Framerate", 40);
		settings.put("in_Position", 0);
		settings.put("in_Color", 1);
		settings.put("in_TextureCoord", 2);
		settings.put("in_Normal", 3);
		settings.put("Ks", 4);
		settings.put("Ka", 5);
		settings.put("specExp", 6);
		settings.put("texture", 7);
		
		// Paths
		settings.put("vertex_path", "src/shaders/vertex.vert");
		settings.put("fragment_path",  "src/shaders/fragment.frag");
		settings.put("post_vertex_path", "src/shaders/postVertex.vert");
		settings.put("post_fragment_path", "src/shaders/postFragment.frag");
		settings.put("picking_vertex_path", "src/shaders/colorPicking.vert");
		settings.put("picking_fragment_path", "src/shaders/colorPicking.frag");
		settings.put("skybox_vertex_path", "src/shaders/skybox.vert");
		settings.put("skybox_fragment_path", "src/shaders/skybox.frag");
		settings.put("log_folder", "logs/");
		settings.put("pwd", System.getProperty("user.dir"));
		settings.put("playerAcceleration", false);

		// Default physics model properties.
		settings.put("defaultRestitution", 0.5f);
		settings.put("defaultMass", 100f);
		settings.put("defaultAngularDamping", 0.1f);
		settings.put("defaultLinearDamping", 0.1f);
		settings.put("defaultFriction", 1f);
	}

	/**
	 * Adds an integer value to our settings.
	 * @param key
	 * @param val
	 */
	public static void putInteger (String key, int val) {
		settings.put(key, new Integer(val));
	}

	/**
	 * Adds a string value to our settings.
	 * @param key
	 * @param val
	 */
	public static void putString (String key, String val) {
		settings.put(key, val);
	}

	/**
	 * Adds a float value to our settings.
	 * @param key
	 * @param val
	 */
	public static void putFloat (String key, float val) {
		settings.put(key, new Float(val));
	}

	/**
	 * Adds a boolean value to our settings.
	 * @param key
	 * @param val
	 */
	public static void putBoolean (String key, boolean val) {
		settings.put(key, new Boolean(val));
	}

	/**
	 * Gets an integer.
	 * @param key
	 * @return the integer value
	 */
	public static int getInteger (String key) {
		Integer val = (Integer) settings.get(key);
		
		if (val == null) 
			throw new IllegalArgumentException ("Key '" + val + "' does not exist");
		
		return val.intValue();
	}

	/**
	 * Gets a String.
	 * @param key
	 * @return the string value
	 */
	public static String getString (String key) {
		String val = (String) settings.get(key);
		
		if (val == null) 
			throw new IllegalArgumentException ("Key '" + val + "' does not exist");
		
		return val;
	}

	/**
	 * Gets a float.
	 * @param key
	 * @return the float value
	 */
	public static float getFloat (String key) {
		Float val = (Float) settings.get(key);
		
		if (val == null) 
			throw new IllegalArgumentException ("Key '" + val + "' does not exist");
		
		return val.floatValue();
	}

	/**
	 * Gets a boolean.
	 * @param key
	 * @return the boolean value
	 */
	public static boolean getBoolean (String key) {
		Boolean val = (Boolean) settings.get(key);
		
		if (val == null) 
			throw new IllegalArgumentException ("Key '" + val + "' does not exist");
		
		return val.booleanValue();
	}

	/**
	 * Gets a string representation of our settings.
	 * @return the string representation
	 */
	public static String getStringRepresentation () {
		String ret = "";
		for (String str : settings.keySet()) {
			ret += str + ": [" + settings.get(str).toString() + "]";
			ret += "\r\n";
		}
		return ret;
	}
}