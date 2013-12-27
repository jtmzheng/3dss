package physics;

import java.util.HashMap;
import java.util.Map;

public class PhysicsModelProperties {

	private Map<String, Object> properties;
	
	public PhysicsModelProperties() {
		properties = new HashMap<String, Object> ();
	}
	
	public void setProperty(String name, Object value) {
		properties.put(name, value);
	}
	
	public Object getProperty(String name) {
		return properties.get(name);
	}
	
}
