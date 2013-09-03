package renderer;

import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Matrix4f;

/**
 * The renderer class should set up OpenGL
 * In progress.
 * @author Adi
 * @author Max
 */
public class Renderer {
	
	/*
	 * For each model there will have a VAO with all the data bound to it. This ArrayList
	 * will be iterated over every render loop. 
	 */
	public ArrayList<Integer> VAO; //Arraylist of VAO IDs that will be iterated over
	private final int WIDTH = 320;
	private final int HEIGHT = 240;
	
	/*
	 * Constructor will be filled in later
	 */
	public Renderer(int width, int height){
		VAO = new ArrayList<Integer>();
		
		/*
		 * If 0 is passed in for width and height, run it fullscreen
		 */
		if (width == 0 && height == 0)
			this.initOpenGL(true);
		else
			this.initOpenGL(false);
	}
	
	/* 
	 * There will be a class called "Model" which will have all the data that will be bound to a VBO 
	 * (anything needed to render) and then bound to a VAO(such as texture, vertex position, color, etc). 
	 * This will PROBABLY make things easier becaues it will abstract creating new models and the actual 
	 * rendering.  
	 */
	public synchronized boolean bindNewModel(Object model){
		return false;	
	}
	
	/*
	 * Renders the new scene
	 */
	public void renderScene (){
		
		/*INSERT rendering*/
		
		// Force a maximum FPS of about 60
		Display.sync(60);
		// Let the CPU synchronize with the GPU if GPU is tagging behind (I think update refreshs the display)
		Display.update();
	}
	
	/*
	 * Initializes OpenGL (currently using 3.2)
	 */
	private void initOpenGL(boolean fullscreen){
		try{
			PixelFormat pixelFormat = new PixelFormat();
			ContextAttribs contextAtr = new ContextAttribs(3, 2) 
				.withForwardCompatible(true)
				.withProfileCore(true);
			
			if (fullscreen) 
				Display.setFullscreen(true);
			else {
				Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
				
				// Map the internal OpenGL coordinate system to the entire screen (not sure what this does)		
				setViewPort(0, 0, WIDTH, HEIGHT);
			}
			
			Display.setTitle("Game the Name 2.0");
			Display.create(pixelFormat, contextAtr);
		} catch (LWJGLException e){
			e.printStackTrace();
			System.exit(-1); //quit if opengl context fails
		}
		
		//XNA like background color
		GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);		
	}
	
	/*
	 * Sets our view port with a given width and height.
	 */
	private void setViewPort (int x, int y, int width, int height){
		GL11.glViewport(x, y, width, height);
	}
	
	/*
	 * Sets the view matrix to use.
	 */
	private void setViewMatrix (Matrix4f view){
		
	}
	
	/*
	 * Sets the projection matrix to use.
	 */
	private void setProjectionMatrix (Matrix4f proj){
		
	}
	
	/*
	 * Sets the lighting with a provided LightSet (relative to world space)
	 */
	private void setLighting (/*insert a LightSet class here*/){
		
	}

}
