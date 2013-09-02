package system;

import java.util.HashMap;


/*
 * Settings of our application
 */
public class Settings extends HashMap<String, Object> {
	
	/*
	 * List of default settings
	 */
	private static final Settings defaults = new Settings(false);
	
	static {
		defaults.put("Fullscreen", true);
		defaults.put("Framerate", 40);
	}
	
	public Settings (boolean useDefaults) {
		if (useDefaults) 
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
	
	@Override
	public String toString () {
		String ret = "";
		for (String str : this.keySet()) {
			ret += str + ": [" + this.get(str).toString() + "]";
			ret += "\r\n";
		}
		return ret;
	}
}
