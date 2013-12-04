package renderer;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;

//TODO: Id management should probably be handled privately
public class LightManager {

	private HashMap<Object, Light> m_lightMap; 
	private HashMap<Light, LightGL> m_lightToGLMap;
	
	private final Object LightManagerLock = new Object();
	
	private final static int MAX_LIGHTS = 32;
	private static ArrayBlockingQueue<Integer> m_lightIndices; // Holds uniform location of each light
	private static LightManager m_lightManager = null;
	private LightGL[] lightsGL;
	
	private LightManager() throws InterruptedException {
		m_lightMap = new HashMap<Object, Light>(MAX_LIGHTS);
		m_lightToGLMap = new HashMap<Light, LightGL>(MAX_LIGHTS);
		
		m_lightIndices = new ArrayBlockingQueue<Integer>(MAX_LIGHTS);
		for(int i = 0; i < MAX_LIGHTS; i++){
			m_lightIndices.put(i);
		}
		
		initGL(); //initialize gl components 
	}
	
	public static LightManager getLightManagerHandle(){
		if(m_lightManager == null){
			try{
				m_lightManager = new LightManager();
			} catch (InterruptedException e){
				return null;
			}
		}
		
		return m_lightManager;
	}
	
	/**
	 * Add a LightHandle to the LightManager
	 * @param owner
	 * @param newLight
	 * @return true if successful
	 */
	public boolean addLight(Object owner, Light newLight){
		synchronized(LightManagerLock){
			if(m_lightMap.size() < MAX_LIGHTS){
				m_lightMap.put(owner, newLight);
				int lightId = getLightID();
				m_lightToGLMap.put(newLight, lightsGL[lightId]);
				
				// Update the uniform variables that won't change (for now)
				GL20.glUseProgram(ShaderController.getCurrentProgram());
				newLight.updateIsUsed(lightsGL[lightId], true);
				newLight.updateSpecExp(lightsGL[lightId]);
				newLight.updateDiffuse(lightsGL[lightId]);
				newLight.updateSpecular(lightsGL[lightId]);
				GL20.glUseProgram(0);
				
				return true;
			}
			else{
				return false; 
			}
				
		}
	}
	
	/**
	 * Remove a light from the LightManager
	 * @param owner
	 * @return true if successful
	 */
	public boolean removeLight(Object owner){
		synchronized(LightManagerLock){
			if(m_lightMap.size() > 0){
				Light toRemove = m_lightMap.remove(owner);
				toRemove.updateIsUsed(m_lightToGLMap.get(toRemove), false);
				
				LightGL ret = m_lightToGLMap.remove(toRemove);
				returnLightID(ret.getIndex()); // return the GL light to available lights
				return true;
			}
			else{
				return false;
			}
		}
	}
	
	/**
	 * Update the GL uniform variables for all the active lights
	 */
	public void updateAllLights(){
		synchronized(LightManagerLock){
			for(Light l : m_lightMap.values()){
				GL20.glUseProgram(ShaderController.getCurrentProgram());
				l.updatePosition(m_lightToGLMap.get(l));
				GL20.glUseProgram(0);
			}
		}
	}
	
	/**
	 * Get a light ID from the manager
	 * @return a light ID or null if all light slots are full
	 */
	private int getLightID(){
		return m_lightIndices.poll();
	}
	
	/**
	 * Returns a light ID to the manager
	 * @param id
	 * @return true if success
	 */
	private boolean returnLightID(int id){
		return m_lightIndices.add(id);
	}
	
	/**
	 *  Initialize GL stuff (get the uniform locations of all the lights)
	 */
	private void initGL() {
		lightsGL = new LightGL[MAX_LIGHTS];
		for(int i = 0; i < MAX_LIGHTS; i++){
			lightsGL[i] = new LightGL(i);
		}
	}
	
	
}