package renderer;

import org.lwjgl.opengl.GL20;

/**
 * This class holds all the uniform locations for a given light
 * @author Max
 *
 */
public class LightGL {
	
	private int position = -1;
	private int specular = -1;
	private int diffuse = -1;
	private int specExp = -1;
	private int isUsed = -1;
	private int index = -1;
	
	public LightGL(int index){
		this.index = index;
		
		String light = "lights" + "[" + index + "].";
		position = GL20.glGetUniformLocation(ShaderController.getCurrentProgram(), light + "position" );
		specular = GL20.glGetUniformLocation(ShaderController.getCurrentProgram(), light + "Ls");
		diffuse = GL20.glGetUniformLocation(ShaderController.getCurrentProgram(),  light + "Ld");
		specExp = GL20.glGetUniformLocation(ShaderController.getCurrentProgram(),  light + "specExp");
		isUsed = GL20.glGetUniformLocation(ShaderController.getCurrentProgram(),  light + "isUsed");
	}
	
	public int getPosition(){
		return position;
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
	
	public int getIndex(){
		return index;
	}
	
}