package renderer;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import system.Settings;

/**
 * The renderer class should set up OpenGL.
 * In progress.
 * @author Adi
 * @author Max
 */
public class Renderer {
	/*
	 * For each model there will have a VAO with all the data bound to it. This ArrayList
	 * will be iterated over every render loop. 
	 */
	public ArrayList<Model> models; //Arraylist of the models that will be renderered
	private int WIDTH = 320;
	private int HEIGHT = 240;
	private ShaderController shader;
	
	// Moving variables (NOT DONE)
	private int projectionMatrixLocation = 0;
	private int viewMatrixLocation = 0;
	private int modelMatrixLocation = 0;
	private Matrix4f projectionMatrix = null;
	private Matrix4f viewMatrix = null;
	private Matrix4f modelMatrix = null;
	private Vector3f modelPos = null;
	private Vector3f modelAngle = null;
	private Vector3f modelScale = null;
	private Vector3f cameraPos = null;
	private FloatBuffer matrix44Buffer = null;
	
	/*
	 * Initializes OpenGL. If zero is passed in for both the width and height,
	 * we call this.initOpenGL with a true "fullscreen" flag.
	 */
	public Renderer(int width, int height){
		this.WIDTH = width;
		this.HEIGHT = height;
		
		if (width == 0 && height == 0)
			this.initOpenGL(true); 
		else
			this.initOpenGL(false);
		
		models = new ArrayList<>();
		shader = new ShaderController();
		
		/*
		 * Initialize shaders
		 */
		HashMap<String, Integer> sh = new HashMap<>();
		sh.put(Settings.getString("vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(Settings.getString("fragment_path"), GL20.GL_FRAGMENT_SHADER);
		shader.setProgram(sh); //TO DO: Error checking
	}
	
	/**
	 * There will be a class called "Model" which will have all the data that will be bound to a VBO 
	 * (anything needed to render) and then bound to a VAO(such as texture, vertex position, color, etc). 
	 * This will PROBABLY make things easier becaues it will abstract creating new models and the actual 
	 * rendering.  
	 * 
	 * @return <code>true</code> if the binding was successful and false otherwise.
	 * @see Model
	 */
	public synchronized boolean bindNewModel(Model model){
		/*
		 * Initialize model
		 */
		models.add(model);
		return true;
	}
	
	/*
	 * Renders the new scene.
	 */
	public void renderScene (){
		/*INSERT rendering*/
		
		// Render
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		GL20.glUseProgram(shader.getCurrentProgram());

		for(Model m: models){
			// Bind to the VAO that has all the information about the vertices
			GL30.glBindVertexArray(m.getVAO());
			GL20.glEnableVertexAttribArray(0); //position
			GL20.glEnableVertexAttribArray(1); //color
			GL20.glEnableVertexAttribArray(2); //texture
			GL20.glEnableVertexAttribArray(3); //normal

			// Bind to the index VBO that has all the information about the order of the vertices
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m.getIndexVBO());
			
			// Draw the vertices
			GL11.glDrawElements(GL11.GL_TRIANGLES, m.getIndicesCount(), GL11.GL_UNSIGNED_BYTE, 0);
		}

		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL30.glBindVertexArray(0);


		GL20.glUseProgram(0);
		
		// Force a maximum FPS of about 60
		Display.sync(60);
		// Let the CPU synchronize with the GPU if GPU is tagging behind (I think update refreshs the display)
		Display.update();
	}
	
	/*
	 * Initializes OpenGL (currently using 3.2).
	 */
	private void initOpenGL(boolean fullscreen){
		try{
			PixelFormat pixelFormat = new PixelFormat();
			ContextAttribs contextAtr = new ContextAttribs(3, 2) 
				.withForwardCompatible(true)
				.withProfileCore(true);
			
			if (fullscreen) 
				Display.setFullscreen(true);
			else 
				Display.setDisplayMode(new DisplayMode(this.WIDTH, this.HEIGHT));
			
			Display.setTitle("Game the Name 2.0");
			Display.create(pixelFormat, contextAtr);
			
			if (WIDTH != 0 && HEIGHT != 0)
				setViewPort(0, 0, this.WIDTH, this.HEIGHT);
			
		} catch (LWJGLException e){
			e.printStackTrace();
			System.exit(-1); //quit if opengl context fails
		}
		
		//XNA like background color
		GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);		
	}
	
	/**
	 * Sets our view port with a given width and height.
	 */
	private void setViewPort (int x, int y, int width, int height){
		GL11.glViewport(x, y, width, height);
	}
	
	/**
	 * Sets the view matrix to use.
	 */
	private void setViewMatrix (Matrix4f view){
		
	}
	
	/**
	 * Sets the projection matrix to use.
	 */
	private void setProjectionMatrix (Matrix4f proj){
		
	}
	
	/**
	 * Sets the lighting with a provided LightSet (relative to world space)
	 */
	private void setLighting (/*insert a LightSet class here*/){
		
	}
	
	public static void main(String [] args){
		
		/*
		 * 1. Bind a few models
		 * 2. renderScene
		 */
		Renderer test = new Renderer(800, 800); //full screen
		try{
			test.bindNewModel(ModelFactory.loadModel(new File("res/obj/cube.obj")));	
		}
		catch(IOException e){
			e.printStackTrace();
		}
				
		while(!Display.isCloseRequested()){
			test.renderScene();
			System.out.println("RENDER");
		}
	}

}
