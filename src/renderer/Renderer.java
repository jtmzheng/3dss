package renderer;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
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

import system.Settings;

/**
 * The renderer class should set up OpenGL.
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
	private int frameRate = 60;
	private ShaderController shader;
	
	// Matrix variables (should be moved to camera class in the future)
	private Matrix4f projectionMatrix = null;
	private Matrix4f viewMatrix = null;
	
	private FloatBuffer matrix44Buffer = null;
	
	// The view matrix will be calculated based off this camera
	private Camera camera = null;    
	
	// Fog instance
	private Fog fog = null;

	/**
	 * Creates the renderer.
	 * If zero is passed in for width and height, it runs fullscreen.
	 * @param width The width of the renderer.
	 * @param height The height of the renderer.
	 * @param camera The camera associated with the renderer.
	 */
	public Renderer(int width, 
			int height, 
			Camera camera, 
			int frameRate,
			Fog fog){
		
		this.camera = camera;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.frameRate = frameRate;
		this.fog = fog;
		
		if (width == 0 && height == 0)
			this.initOpenGL(true); 
		else
			this.initOpenGL(false);

		models = new ArrayList<Model>();
		shader = new ShaderController();
		
		// Initialize shaders
		Map<String, Integer> sh = new HashMap<String, Integer>();
		sh.put(Settings.getString("vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(Settings.getString("fragment_path"), GL20.GL_FRAGMENT_SHADER);
		
		shader.setProgram(sh); //TO DO: Error checking
		
		// Set up view and projection matrices
		projectionMatrix = new Matrix4f();
		float fieldOfView = 45f;
		float aspectRatio = (float)WIDTH / (float)HEIGHT;
		float near_plane = 0.1f;
		float far_plane = 100f;
		
		float y_scale = (float)(1/Math.tan((Math.toRadians(fieldOfView / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = far_plane - near_plane;
		
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((far_plane + near_plane) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * near_plane * far_plane) / frustum_length);
		projectionMatrix.m33 = 0;
		
		viewMatrix = new Matrix4f();
		
		// Create a FloatBuffer with the proper size to store our matrices later
		matrix44Buffer = BufferUtils.createFloatBuffer(16);
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
	public boolean bindNewModel(Model model){
		models.add(model);
		return true;
	}
	
	/**
	 * Renders the new scene.
	 */
	public void renderScene (){		
		// Clear the color and depth buffers
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		
		// Select our shader program.
		GL20.glUseProgram(ShaderController.getCurrentProgram());
			
		// Set the uniform values of the projection and view matrices 
		viewMatrix = camera.getViewMatrix();
		viewMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		GL20.glUniformMatrix4(ShaderController.getViewMatrixLocation(), false, matrix44Buffer);
		GL20.glUniformMatrix4(ShaderController.getViewMatrixFragLocation(), false, matrix44Buffer);
		projectionMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		GL20.glUniformMatrix4(ShaderController.getProjectionMatrixLocation(), false, matrix44Buffer);
		
		for(Model m: models){
			// Render the model
			m.render();
		}
        		
		// Deselect
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(4);
		GL20.glDisableVertexAttribArray(5);
		GL20.glDisableVertexAttribArray(6);
		GL30.glBindVertexArray(0);
		GL20.glUseProgram(0);

		// Force a maximum FPS of about 60
		Display.sync(frameRate);
		// Let the CPU synchronize with the GPU if GPU is tagging behind (I think update refreshs the display)
		Display.update();
	}
	
	/**
	 * Get the camera associated with this renderer.
	 * @return the camera
	 * @throws NullPointerException
	 */
	public Camera getCamera() throws NullPointerException{
		if(camera == null){
			throw new NullPointerException();
		}
		
		return camera;
	}
	
	/**
	 * Get the max frame rate of the renderer
	 * @return
	 */
	public int getFrameRate() {
		return frameRate;
	}
	
	/**
	 * Initializes OpenGL (currently using 3.2).
	 * @param fullscreen Determines whether we should run in fullscreen.
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
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE ); //for debug
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	/**
	 * Sets our view port at (x,y) given a width and height.
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	private void setViewPort (int x, int y, int width, int height){
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
	}	
}
