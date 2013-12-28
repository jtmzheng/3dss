package renderer;

import org.lwjgl.opengl.GL20;

/**
 * This class holds all the uniform locations for a given light.
 *
 * @author Max
 */
public class LightGL {

	private int position = -1;
	private int direction = -1;
	private int specular = -1;
	private int diffuse = -1;
	private int specExp = -1;
	private int isUsed = -1;
	private int isDirectional = -1;
	private int index = -1;
	private int attenuation = -1;

	/**
	 * Constructor for the LightGL 
	 * @param[in] index 
	 */
	public LightGL(int index){
		this.index = index;

		// Prefix for light array in shader
		String light = "lights" + "[" + index + "].";

		// Get the uniform locations of all the fields
		position = GL20.glGetUniformLocation(ShaderController.getCurrentProgram(), light + "position" );
		direction = GL20.glGetUniformLocation(ShaderController.getCurrentProgram(), light + "direction");
		specular = GL20.glGetUniformLocation(ShaderController.getCurrentProgram(), light + "Ls");
		diffuse = GL20.glGetUniformLocation(ShaderController.getCurrentProgram(),  light + "Ld");
		specExp = GL20.glGetUniformLocation(ShaderController.getCurrentProgram(),  light + "specExp");
		isUsed = GL20.glGetUniformLocation(ShaderController.getCurrentProgram(),  light + "isUsed");
		isDirectional = GL20.glGetUniformLocation(ShaderController.getCurrentProgram(), light + "isDirectional");
		attenuation = GL20.glGetUniformLocation(ShaderController.getCurrentProgram(), light + "attenuation");
	}

	public int getPosition(){
		return position;
	}

	public int getDirection(){
		return direction;
	}

	public int getSpecular(){
		return specular;
	}

	public int getDiffuse(){
		return diffuse;
	}

	public int getSpecExp(){
		return specExp;
	}

	public int getIsUsed(){
		return isUsed;
	}

	public int getIsDirectional(){
		return isDirectional;
	}

	public int getIndex(){
		return index;
	}
	
	public int getAttenuation(){
		return attenuation;
	}
}