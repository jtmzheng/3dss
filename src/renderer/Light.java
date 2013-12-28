package renderer;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;

/**
 * Phong lighting implementation
 * @author Max
 *
 */
public class Light {
	
	private Vector3f m_position; // position of camera
	private Vector3f m_Ls; // white specular colour
	private Vector3f m_Ld; // dull white diffuse light colour
	@SuppressWarnings("unused")
	private Vector3f m_La; // grey ambient colour (currently ambient light is global)
	
	private Vector3f m_spot = null; // spot light direction
	private boolean m_directional;
	
	private Vector3f m_attenuation;	// Attenuation
	
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
		m_position = pos;
		m_Ls = spec;
		m_Ld = diff;
		m_La = ambi;
		m_spot = dir; 
		
		m_directional = m_spot != null; // If m_spot is null, it is non-directional
		
		// Default attenuation
		m_attenuation = new Vector3f(0.3f, 0.007f, 0.008f); //constant, linear, and quadratic
	}
	
	public void updatePosition(LightGL lgl){
		m_position.store(m_DataBuffer); m_DataBuffer.flip();
		GL20.glUniform3(lgl.getPosition(), m_DataBuffer);
	}
	
	public void updateDirection(LightGL lgl){
		if(m_directional && m_spot != null) {
			m_spot.store(m_DataBuffer); m_DataBuffer.flip();
			GL20.glUniform3(lgl.getDirection(), m_DataBuffer);
		}
	}

	public void updateSpecular(LightGL lgl){
		m_Ls.store(m_DataBuffer); m_DataBuffer.flip();
		GL20.glUniform3(lgl.getSpecular(), m_DataBuffer);
	}
	
	public void updateDiffuse(LightGL lgl){
		m_Ld.store(m_DataBuffer); m_DataBuffer.flip();
		GL20.glUniform3(lgl.getDiffuse(), m_DataBuffer);
	}
	
	public void updateAttenuation(LightGL lgl){
		m_attenuation.store(m_DataBuffer); m_DataBuffer.flip();
		GL20.glUniform3(lgl.getAttenuation(), m_DataBuffer);
	}
	
	public void updateSpecExp(LightGL lgl){
		GL20.glUniform1f(lgl.getSpecExp(), 100.0f); // Currently a hardcoded constant
	}
	
	public void updateIsUsed(LightGL lgl, boolean isUsed){
		if(isUsed) {
			GL20.glUniform1f(lgl.getIsUsed(), 1.0f);
		}
		else { 
			GL20.glUniform1f(lgl.getIsUsed(), 0.0f);
		}
	}
	
	public void updateIsDirectional(LightGL lgl) {
		if(m_directional) {
			GL20.glUniform1f(lgl.getIsDirectional(), 1.0f);
		}
		else {
			GL20.glUniform1f(lgl.getIsDirectional(), 0.0f);
		}
	}
	
	public void setPosition(Vector3f position) {
		m_position = position;
	}

	public void setDirection(Vector3f direction) {
		m_spot = direction;
	}

}
