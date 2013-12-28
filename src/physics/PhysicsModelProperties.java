package physics;

import java.util.HashMap;
import java.util.Map;

/**
 * Object to specify custom physics model properties. If a model is created
 * with a property that isn't in this object, it falls back to the default value.
 *
 * @author Max
 */
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