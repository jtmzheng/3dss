package renderer.light;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;


/**
 * Phong lighting implementation.
 *
 * @author Max
 */
public class Light {
	
	private Vector3f mPosition; // position of camera
	private Vector3f mLs; // white specular colour
	private Vector3f mLd; // dull white diffuse light colour
	@SuppressWarnings("unused")
	private Vector3f mLa; // grey ambient colour (currently ambient light is global)
	
	private Vector3f mSpot = null; // spot light direction
	private boolean mDirectional;
	
	private Vector3f mAttenuation;	// Attenuation
	
	private FloatBuffer m_DataBuffer = BufferUtils.createFloatBuffer(3);
	
	/**
	 * Constructor for Light takes parameters needed to define light 
	 * @param pos
	 * @param spec
	 * @param diff
	 * @param ambi
	 * @param dir
	 */
	public Light(Vector3f pos, Vector3f spec, Vector3f diff, Vector3f ambi, Vector3f dir){
		mPosition = pos;
		mLs = spec;
		mLd = diff;
		mLa = ambi;
		mSpot = dir; 
		
		mDirectional = mSpot != null; // If m_spot is null, it is non-directional
		
		// Default attenuation
		mAttenuation = new Vector3f(0.3f, 0.007f, 0.008f); //constant, linear, and quadratic
	}
	
	public void updatePosition(LightGL lgl){
		mPosition.store(m_DataBuffer); m_DataBuffer.flip();
		GL20.glUniform3(lgl.getPosition(), m_DataBuffer);
	}
	
	public void updateDirection(LightGL lgl){
		if(mDirectional && mSpot != null) {
			mSpot.store(m_DataBuffer); m_DataBuffer.flip();
			GL20.glUniform3(lgl.getDirection(), m_DataBuffer);
		}
	}

	public void updateSpecular(LightGL lgl){
		mLs.store(m_DataBuffer); m_DataBuffer.flip();
		GL20.glUniform3(lgl.getSpecular(), m_DataBuffer);
	}
	
	public void updateDiffuse(LightGL lgl){
		mLd.store(m_DataBuffer); m_DataBuffer.flip();
		GL20.glUniform3(lgl.getDiffuse(), m_DataBuffer);
	}
	
	public void updateAttenuation(LightGL lgl){
		mAttenuation.store(m_DataBuffer); m_DataBuffer.flip();
		GL20.glUniform3(lgl.getAttenuation(), m_DataBuffer);
	}
	
	public void updateSpecExp(LightGL lgl){
		GL20.glUniform1f(lgl.getSpecExp(), 100.0f); // Currently a hardcoded constant
	}
	
	public void updateIsUsed(LightGL lgl, boolean isUsed){
		if(isUsed) {
			GL20.glUniform1f(lgl.getIsUsed(), 1.0f);
		} else { 
			GL20.glUniform1f(lgl.getIsUsed(), 0.0f);
		}
	}
	
	public void updateIsDirectional(LightGL lgl) {
		if(mDirectional) {
			GL20.glUniform1f(lgl.getIsDirectional(), 1.0f);
		} else {
			GL20.glUniform1f(lgl.getIsDirectional(), 0.0f);
		}
	}
	
	public void setPosition(Vector3f position) {
		mPosition = position;
	}

	public void setDirection(Vector3f direction) {
		mSpot = direction;
	}
}