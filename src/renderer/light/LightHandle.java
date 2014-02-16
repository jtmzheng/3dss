package renderer.light;


/*
 * LightHandle class (Description to be added)
 * @author Max
 */
public class LightHandle {

	private Object mOwner;
	private Light mLight;
	private boolean mValid;
	
	private LightManager mLightManager = null; //local reference
	
	public LightHandle(Object owner, Light light){
		mOwner = owner;
		mLight = light;
		
		if(mLightManager == null){
			mLightManager = LightManager.getLightManagerHandle();
		}
		
		if(!mLightManager.addLight(mOwner, mLight)){
			mOwner = null; //Invalid light handle
			mLight = null;
			mValid = false;
		} else {
			mValid = true;
		}
	}
	
	/*
	 * Checks whether the light handle is valid or not
	 */
	public boolean isValid(){
		return mValid;
	}
	
	
	/*
	 * Call this when the light is destroyed by the owner
	 */
	public void invalidate(){
		if(!mValid){
			return;
		}
		
		mLightManager.removeLight(mOwner);
		mLight = null;
		mOwner = null;
		mValid = false;
	}
	
	/*
	 * Resets the light handle 
	 * @brief Owner doesn't change with reset
	 */
	public boolean reset(Light light) {
		Object owner = mOwner;
		
		// Invalidate first if handle holds a valid light
		if(mValid) {
			invalidate();
		}
		
		mLight = light;
		mOwner = owner;
		
		// Add the light to the light manager
		if(!mLightManager.addLight(mOwner, mLight)) {
			mOwner = null; //Invalid light handle
			mLight = null;
			mValid = false;
		}
		
		mValid = true;		
		
		return mValid;
	}

	public Light getLight() {
		return mLight;
	}
}