package renderer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.lwjgl.opengl.GL20;

/*
 * ShaderController will manage shaders 
 * @author Max 
 */
public class ShaderController {

	private HashMap<String, Integer> shaderNameToID = null;
	private HashMap<Integer, Integer> shaderIDToType = null;
	
	private int currentProgram;

	public ShaderController() {
		shaderNameToID = new HashMap<>();
		shaderIDToType = new HashMap<>();
		currentProgram = 0;
	}

	
	/*
	 * Currently setProgram is written like this so different shaders can be 
	 * written for different graphics settings 
	 */
	public boolean setProgram(HashMap<String, Integer> shaders){
		if(currentProgram != 0){
			//TO DO: Cleanup
		}
		
		currentProgram = GL20.glCreateProgram();
		
		for(String file : shaders.keySet()){
			int shaderID = this.loadShader(file, shaders.get(file));
			GL20.glAttachShader(currentProgram, shaderID);
			shaderNameToID.put(file, shaderID);
			shaderIDToType.put(shaderID, shaders.get(file));
		}
		
		/*
		 * NOTE: Should attributes be a constant, or should the client
		 * be allowed to change attributes?
		 */
		// Position information will be attribute 0
		GL20.glBindAttribLocation(currentProgram, 0, "in_Position");
		// Color information will be attribute 1
		GL20.glBindAttribLocation(currentProgram, 1, "in_Color");
		// Texture information will be attribute 2
		GL20.glBindAttribLocation(currentProgram, 2, "in_TextureCoord");
		//Normal information will be attribute 3
		GL20.glBindAttribLocation(currentProgram, 3, "in_Normal");
		
		GL20.glLinkProgram(currentProgram);
		GL20.glValidateProgram(currentProgram);
		
		return true;
	}
	
	public int getCurrentProgram(){
		return currentProgram;
	}
	
	private int loadShader(String filename, int type) {
		
		StringBuilder shaderSource = new StringBuilder();
		int shaderID = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read file.");
			e.printStackTrace();
			System.exit(-1);
		}

		shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		return shaderID;
	}


}
