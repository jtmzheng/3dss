package renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LightManager {

	private HashMap<Object, Light> m_lightMap; //TODO: Use a concurrent hashmap?
	private final Object LightManagerLock = new Object();
	
	private static LightManager m_lightManager = null;
	
	private LightManager(){
		m_lightMap = new HashMap<Object, Light>();
	}
	
	public static LightManager getLightManagerHandle(){
		if(m_lightManager == null){
			m_lightManager = new LightManager();
		}
		
		return m_lightManager;
	}
	
	/*
	 * Add a light to the manager
	 */
	public void addLight(Object owner, Light newLight){
		synchronized(LightManagerLock){
			m_lightMap.put(owner, newLight);
		}
	}
	
	/*
	 * Remove a light from the manager
	 */
	public void removeLight(Object owner){
		synchronized(LightManagerLock){
			m_lightMap.remove(owner);
		}
	}
	
	/*
	 * Update lights (uniform variables in the shader)
	 */
	public void updateAllLights(){
		synchronized(LightManagerLock){
			//TODO: Update code
		}
	}
}
