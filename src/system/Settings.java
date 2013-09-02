package system;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;

/*
 * Settings of our application
 */
public class Settings extends HashMap<String, Object> {
	
	/*
	 * List of default settings
	 */
	private static final Settings defaults = new Settings();
	
	static {
		defaults.put("Fullscreen", true);
		defaults.put("Framerate", 40);
		defaults.put("GLVersion", GL11.glGetString(GL11.GL_VERSION));
	}
	
	public Settings () {
		this.putAll(defaults);
	}
	
	public void putInteger (String key, int val) {
		this.put(key, new Integer(val));
	}
	
	public void putString (String key, String val) {
		this.put(key, val);
	}
	
	public void putFloat (String key, float val) {
		this.put(key, new Float(val));
	}
	
	public int getInteger (String key) {
		Integer val = (Integer) this.get(key);
		
		if (val == null) 
			throw new IllegalArgumentException ("Key '" + val + "' does not exist");
		
		return val.intValue();
	}
	
	public String getString (String key) {
		String val = (String) this.get(key);
		
		if (val == null) 
			throw new IllegalArgumentException ("Key '" + val + "' does not exist");
		
		return val;
	}
	
	public float getFloat (String key) {
		Float val = (Float) this.get(key);
		
		if (val == null) 
			throw new IllegalArgumentException ("Key '" + val + "' does not exist");
		
		return val.floatValue();
	}
}
