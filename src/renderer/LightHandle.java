package renderer;

/*
 * LightHandle class (Description to be added)
 * @author Max
 */
public class LightHandle {

	private Object m_owner;
	private Light m_light;
	private boolean m_valid;
	
	private LightManager m_lightManager = null; //local reference
	
	public LightHandle(Object owner, Light light){
		m_owner = owner;
		m_light = light;
		
		if(m_lightManager == null){
			m_lightManager = LightManager.getLightManagerHandle();
		}
		
		if(!m_lightManager.addLight(m_owner, m_light)){
			m_owner = null; //Invalid light handle
			m_light = null;
			m_valid = false;
		}
		else {
			m_valid = true;
		}
	}
	
	/*
	 * Checks whether the light handle is valid or not
	 */
	public boolean isValid(){
		return m_valid;
	}
	
	
	/*
	 * Call this when the light is destroyed by the owner
	 */
	public void invalidate(){
		if(!m_valid){
			return;
		}
		
		m_lightManager.removeLight(m_owner);
		m_light = null;
		m_owner = null;
		m_valid = false;
	}
	
	/*
	 * Resets the light handle 
	 * @brief Owner doesn't change with reset
	 */
	public boolean reset(Light light) {
		Object owner = m_owner;
		
		// Invalidate first if handle holds a valid light
		if(m_valid) {
			invalidate();
		}
		
		m_light = light;
		m_owner = owner;
		
		// Add the light to the light manager
		if(!m_lightManager.addLight(m_owner, m_light)){
			m_owner = null; //Invalid light handle
			m_light = null;
			m_valid = false;
		}
		
		m_valid = true;		
		
		return m_valid;
	}

	public Light getLight() {
		return m_light;
	}
}