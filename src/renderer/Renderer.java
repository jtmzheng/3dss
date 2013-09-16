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
	private Vector3f cameraPosition = null;
	private float cameraFOV = 45f;
	private float cameraHorizontalAngle = 3.14f; //arbitrarily defined right now
	private float cameraVerticalAngle = 0.0f; 
	private Vector3f cameraDirection = new Vector3f(
			(float)(Math.cos(cameraVerticalAngle) * Math.sin(cameraHorizontalAngle)),
			(float)(Math.sin(cameraVerticalAngle)),
			(float)(Math.cos(cameraVerticalAngle) * Math.cos(cameraHorizontalAngle))); 
	private Vector3f cameraRight = new Vector3f(
			(float)(Math.sin(cameraHorizontalAngle - 3.14f/2.0f)),
			(float)(0f),
			(float)(Math.cos(cameraHorizontalAngle - 3.14f/2.0f))); //NOTE: Vector3f has built in cross product
	
	private float cameraSensitivity = 0.005f; //Larger equals more sensitive
	
	
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
		cameraPosition = new Vector3f(0f, 0f, 5f);
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
		
		/*INSERT altering variables*/
		viewMatrix = lookAt(cameraPosition, Vector3f.add(cameraPosition, cameraDirection, null), new Vector3f(0f, 1f, 0f)); //Vector3f.cross(cameraRight, cameraDirection, null)
		
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
	 * TODO: Should be moved to camera class
	 */
	private Matrix4f lookAt(Vector3f cam, Vector3f center, Vector3f up) {
		Vector3f f = normalize(Vector3f.sub(center, cam, null));
		Vector3f u = normalize(up);
		Vector3f s = normalize(Vector3f.cross(f, u, null));
		u = Vector3f.cross(s, f, null);

		Matrix4f result = new Matrix4f();
		result.m00 = s.x;
		result.m10 = s.y;
		result.m20 = s.z;
		result.m01 = u.x;
		result.m11 = u.y;
		result.m21 = u.z;
		result.m02 = -f.x;
		result.m12 = -f.y;
		result.m22 = -f.z;
		
		return Matrix4f.translate(new Vector3f(-cam.x, -cam.y, -cam.z),  result, result);
	}

	private Vector3f normalize(Vector3f v){
		float mag = (float)(Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z));
		return new Vector3f(v.x/mag, v.y/mag, v.z/mag);
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
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
	}
	

	/**
	 * Sets the lighting with a provided LightSet (relative to world space)
	 */
	private void setLighting (/*insert a LightSet class here*/){
		
	}
	
	public void rotateCamera(int deltaX, int deltaY){
		cameraVerticalAngle += deltaY * cameraSensitivity;
		cameraHorizontalAngle += deltaX * cameraSensitivity;
		
		cameraDirection.x = (float)(Math.cos(cameraVerticalAngle) * Math.sin(cameraHorizontalAngle));
		cameraDirection.y = (float)(Math.sin(cameraVerticalAngle));
		cameraDirection.z = (float)(Math.cos(cameraVerticalAngle) * Math.cos(cameraHorizontalAngle));
		
		cameraRight.x = (float)(Math.sin(cameraHorizontalAngle - 3.14f/2.0f));
		cameraRight.y = (float)(Math.sin(cameraVerticalAngle));
		cameraRight.z = (float)(Math.cos(cameraVerticalAngle - 3.14f/2.0f));
		
		
		/*
		 * Update matrices here?
		 */
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
		
		
		Mouse.setGrabbed(true); //hides the cursor
		boolean loop = true;
		
		while(!Display.isCloseRequested() && loop){
			
			//POLL FOR INPUT
			if (Mouse.isInsideWindow()) {
				int x = Mouse.getX();
				int y = Mouse.getY();
				
				test.rotateCamera(300 - x, 300 - y);
				
				Mouse.setCursorPosition(300, 300); //Middle of the screen
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				System.out.println("SPACE KEY IS DOWN");
			}

			while (Keyboard.next()) {
				if (Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == Keyboard.KEY_A) {
						System.out.println("A Key Pressed");
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_S) {
						System.out.println("S Key Pressed");
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_D) {
						System.out.println("D Key Pressed");
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE){
						loop = false; //exit (TODO: make this cleaner/use break?)
						Mouse.setGrabbed(false);
					}
				} 
			}
			//END POLL FOR INPUT
			
			test.renderScene();
			//System.out.println("RENDER");
		}
	}



}
