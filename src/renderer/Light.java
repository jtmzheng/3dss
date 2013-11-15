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
	private FloatBuffer m_DataBuffer = BufferUtils.createFloatBuffer(3);
	
	
	public Light(Vector3f pos, Vector3f spec, Vector3f diff, Vector3f ambi){
		//Use current shader program
		GL20.glUseProgram(ShaderController.getCurrentProgram());

		m_position = pos;
		m_Ls = spec;
		m_Ld = diff;
		m_La = ambi;

		//Bind to uniform variables
		m_position.store(m_DataBuffer); m_DataBuffer.flip();
		GL20.glUniform3(ShaderController.getLightPositionLocation(), m_DataBuffer);
		m_Ls.store(m_DataBuffer); m_DataBuffer.flip();
		GL20.glUniform3(ShaderController.getSpecularLocation(), m_DataBuffer);
		m_Ld.store(m_DataBuffer); m_DataBuffer.flip();
		GL20.glUniform3(ShaderController.getDiffuseLocation(), m_DataBuffer);
		m_La.store(m_DataBuffer); m_DataBuffer.flip();
		GL20.glUniform3(ShaderController.getAmbientLocation(), m_DataBuffer);
		
		//Unbind
		GL20.glUseProgram(0);
	}

	public void updatePosition(){
		GL20.glUseProgram(ShaderController.getCurrentProgram());
		m_position.store(m_DataBuffer); m_DataBuffer.flip();
		GL20.glUniform3(ShaderController.getLightPositionLocation(), m_DataBuffer);
		GL20.glUseProgram(0);

	}

}
