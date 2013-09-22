package renderer;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
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

import debugger.DebugWindow;

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
	
	// Matrix variables (should be moved to camera class in the future)
	private Matrix4f projectionMatrix = null;
	private Matrix4f viewMatrix = null;
	
	private FloatBuffer matrix44Buffer = null;
	
	//Camera variables (TODO: will be moved to a camera class in the future)
	private Camera camera = null;
	
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
		
		//Initialize shaders
		HashMap<String, Integer> sh = new HashMap<>();
		sh.put(Settings.getString("vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(Settings.getString("fragment_path"), GL20.GL_FRAGMENT_SHADER);
		
		shader.setProgram(sh); //TO DO: Error checking
		
		//Set up view and projection matrices
		projectionMatrix = new Matrix4f();
		float fieldOfView = 60f;
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
		
		//Initilize camera
		camera = new Camera(new Vector3f(0.0f, 0.0f, 5.0f));
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
		
		/*INSERT altering variables*/
		viewMatrix = camera.getViewMatrix(); //Vector3f.cross(cameraRight, cameraDirection, null)
		System.out.println("VIEW: " + viewMatrix);
		
		projectionMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		GL20.glUniformMatrix4(shader.getProjectionMatrixLocation(), false, matrix44Buffer);
		viewMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		GL20.glUniformMatrix4(shader.getViewMatrixLocation(), false, matrix44Buffer);
		

		for(Model m: models){
			m.getModelMatrix().store(matrix44Buffer); matrix44Buffer.flip();
			GL20.glUniformMatrix4(shader.getModelMatrixLocation(), false, matrix44Buffer);
			
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

		// Deselect
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
	
	public Camera getCamera() throws NullPointerException{
		if(camera == null){
			throw new NullPointerException();
		}
		
		return camera;
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
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE ); //for debug
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		
		//GL11.glEnable(GL11.GL_DEPTH_TEST);
		// Accept fragment if it closer to the camera than the former one
		//GL11.glDepthFunc(GL11.GL_LEQUAL);
	}
	
	/**
	 * Sets our view port with a given width and height.
	 */
	private void setViewPort (int x, int y, int width, int height){
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
	}
	
	
	public static void main(String [] args){
		/*
		 * 1. Bind a few models
		 * 2. renderScene
		 */
		Renderer test = new Renderer(600, 600); //full screen
		DebugWindow.show();
		try{
			test.bindNewModel(ModelFactory.loadModel(new File("res/obj/cube.obj")));	
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		Camera camera;
		//Grab a reference to the camera
		try{
			camera = test.getCamera();
		}
		catch(NullPointerException e){
			System.out.println("Camera not found!");
			camera = new Camera();
			e.printStackTrace();
		}
		
		Mouse.setGrabbed(true); //hides the cursor
		boolean loop = true;
		
		while(!Display.isCloseRequested() && loop){
			
			//POLL FOR INPUT
			if (Mouse.isInsideWindow()) {
				int x = Mouse.getX();
				int y = Mouse.getY();
				
				camera.rotateCamera(300 - x, 300 - y);
				
				Mouse.setCursorPosition(300, 300); //Middle of the screen
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				System.out.println("SPACE KEY IS DOWN");
			}
			
			Keyboard.enableRepeatEvents(true);

			while (Keyboard.next()) {
				if (Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == Keyboard.KEY_W) {
						camera.moveForwards(0.1f);
						System.out.println("A Key Pressed");
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_A) {
						camera.strafeLeft(0.1f);
						System.out.println("A Key Pressed");
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_S) {
						camera.moveBackwards(0.1f);
						System.out.println("S Key Pressed");
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_D) {
						camera.strafeRight(0.1f);
						System.out.println("D Key Pressed");
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE){
						loop = false; //exit (TODO: make this cleaner/use break?)
						Mouse.setGrabbed(false);
						DebugWindow.destroy();
					}
				} 
			}
			//END POLL FOR INPUT
			
			test.renderScene();
			//System.out.println("RENDER");
		}
	}



}
