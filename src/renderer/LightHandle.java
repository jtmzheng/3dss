package renderer;

public class LightHandle {

	private Object m_owner;
	private Light m_light;
	private LightManager m_lightManager = null; //local reference
	
	public LightHandle(Object owner, Light light){
		m_owner = owner;
		m_light = light;
		
		if(m_lightManager == null){
			m_lightManager = LightManager.getLightManagerHandle();
		}
		
		m_lightManager.addLight(owner, light);
	}
	
	/*
	 * Call this when the light is destroyed by the owner
	 */
	public void invalidate(){
		m_lightManager.removeLight(m_owner);
		m_light = null;
		m_owner = null;
	}

}
