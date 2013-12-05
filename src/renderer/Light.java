package renderer;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;

import system.Settings;

/**
 * Phong lighting implementation
 * @author Max
 *
 */
public class Light {
	
	// Location of our camera.
	private Vector3f m_position;
	private Vector3f m_Ls; //white specular colour
	private Vector3f m_Ld; // dull white diffuse light colour
	private Vector3f m_La; // grey ambient colour
	
	// TODO: Currently not supported
	private Vector3f m_spot = null; // spot light direction
	private float m_attentuation; // attentuation of the light
	
	private FloatBuffer m_DataBuffer = BufferUtils.createFloatBuffer(3);
	
	public Light(Vector3f pos, Vector3f spec, Vector3f diff, Vector3f ambi, Vector3f dir){
		//Use current shader program
		m_position = pos;
		m_Ls = spec;
		m_Ld = diff;
		m_La = ambi;
		m_spot = dir; //TODO: Can be null		
	}
	
	public void updatePosition(LightGL lgl){
		m_position.store(m_DataBuffer); m_DataBuffer.flip();
		GL20.glUniform3(lgl.getPosition(), m_DataBuffer);
	}
	
	public void updateSpecular(LightGL lgl){
		m_Ls.store(m_DataBuffer); m_DataBuffer.flip();
		GL20.glUniform3(lgl.getSpecular(), m_DataBuffer);
	}
	
	public void updateDiffuse(LightGL lgl){
		m_Ld.store(m_DataBuffer); m_DataBuffer.flip();
		GL20.glUniform3(lgl.getDiffuse(), m_DataBuffer);
	}
	
	public void updateSpecExp(LightGL lgl){
		GL20.glUniform1f(lgl.getSpecExp(), 100.0f);
	}
	
	public void updateIsUsed(LightGL lgl, boolean isUsed){
		if(isUsed)
			GL20.glUniform1f(lgl.getIsUsed(), 1.0f);
		else 
			GL20.glUniform1f(lgl.getIsUsed(), 0.0f);
	}

}
