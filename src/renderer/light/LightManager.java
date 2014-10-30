package renderer.light;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.lwjgl.opengl.GL20;

import renderer.shader.ShaderController;

//@TODO: Id management should probably be handled privately
//@TODO: updateLight(LightGL lgl) method

public class LightManager {
	private final static int MAX_LIGHTS = 30;

	private Map<Object, Light> mLightMap; 
	private Map<Light, LightGL> mLightToGLMap;
	
	private final Object LightManagerLock = new Object();
	
	private static BlockingQueue<Integer> mLightIndices; // Holds uniform location of each light
	private static LightManager mLightManager = null;
	private LightGL[] lightsGL;
	
	private LightManager() throws InterruptedException {
		mLightMap = new HashMap<Object, Light>(MAX_LIGHTS);
		mLightToGLMap = new HashMap<Light, LightGL>(MAX_LIGHTS);
		
		mLightIndices = new ArrayBlockingQueue<Integer>(MAX_LIGHTS);
		for(int i = 0; i < MAX_LIGHTS; i++){
			mLightIndices.put(i);
		}
		
		initGL(); //initialize gl components 
	}
	
	public static LightManager getLightManagerHandle(){
		if(mLightManager == null){
			try{
				mLightManager = new LightManager();
			} catch (InterruptedException e){
				return null;
			}
		}
		
		return mLightManager;
	}
	
	/**
	 * Add a LightHandle to the LightManager
	 * @param owner
	 * @param newLight
	 * @return true if successful
	 */
	public boolean addLight(Object owner, Light newLight){
		synchronized(LightManagerLock){
			if(mLightMap.size() < MAX_LIGHTS){
				mLightMap.put(owner, newLight);
				int lightId = getLightID();
				mLightToGLMap.put(newLight, lightsGL[lightId]);
				
				// Update the uniform variables that won't change (for now)
				GL20.glUseProgram(ShaderController.getCurrentProgram());
				newLight.updateIsUsed(lightsGL[lightId], true);
				newLight.updateSpecExp(lightsGL[lightId]);
				newLight.updateDiffuse(lightsGL[lightId]);
				newLight.updateSpecular(lightsGL[lightId]);
				newLight.updateIsDirectional(lightsGL[lightId]);
				newLight.updateAttenuation(lightsGL[lightId]);
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
			if(mLightMap.size() > 0){
				GL20.glUseProgram(ShaderController.getCurrentProgram());
	
				Light toRemove = mLightMap.remove(owner);
				toRemove.updateIsUsed(mLightToGLMap.get(toRemove), false);
				
				LightGL ret = mLightToGLMap.remove(toRemove);
				returnLightID(ret.getIndex()); // return the GL light to available lights

				GL20.glUseProgram(0);

				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * Update the GL uniform variables for all the active lights
	 */
	public void updateAllLights() {
		synchronized(LightManagerLock) {
			for(Light l : mLightMap.values()) {
				GL20.glUseProgram(ShaderController.getCurrentProgram());
				l.updatePosition(mLightToGLMap.get(l));
				l.updateDirection(mLightToGLMap.get(l));
				GL20.glUseProgram(0);
			}
		}
	}
	
	/**
	 * Get a light ID from the manager
	 * @TODO: Remove this method?
	 * @return a light ID or null if all light slots are full
	 */
	private int getLightID(){
		return mLightIndices.poll();
	}
	
	/**
	 * Returns a light ID to the manager
	 * @TODO: Remove this method?
	 * @param id
	 * @return true if success
	 */
	private boolean returnLightID(int id){
		return mLightIndices.add(id);
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