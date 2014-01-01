package renderer;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Matrix4f;

import system.Settings;
import texture.TextureManager;

/**
 * The renderer class should set up OpenGL.
 * @author Adi
 * @author Max
 */
public class Renderer {
	// Defaults 
	private static final int DEFAULT_WIDTH = 320;
	private static final int DEFAULT_HEIGHT = 240;
	private static final int DEFAULT_FRAME_RATE = 60;
	
	// List of the models that will be rendered
	private List<Model> models; 
	private int width;
	private int height;
	private int frameRate;
	
	// Matrix variables (should be moved to camera class in the future)
	private Matrix4f projectionMatrix = null;
	private Matrix4f viewMatrix = null;
	
	private FloatBuffer matrix44Buffer = null;
	
	// The view matrix will be calculated based off this camera
	private Camera camera = null;    
	
	// Fog instance
	private Fog fog = null;
	
	private ScreenQuad screen;
	
	// The shader programs currently supported
	private final ShaderProgram DEFAULT_SHADER_PROGRAM;
	private final ShaderProgram POST_PROCESS_SHADER_PROGRAM; 
	
	private FrameBuffer fb;
	private final Integer DEFAULT_FRAME_BUFFER = 0;
	private int fbTexUnitId;
	
	private TextureManager texManager;

	/**
	 * Default constructor
	 * @param camera The camera associated with the renderer
	 */
	public Renderer(Camera camera) {
		this.camera = camera;
		this.width = DEFAULT_WIDTH;
		this.height = DEFAULT_HEIGHT;
		this.frameRate = DEFAULT_FRAME_RATE;
		this.fog = new Fog(false);		

		// Initialize the OpenGL context
		initOpenGL(width <= 0 && height <= 0);
		
		// Initialize shader programs
		Map<String, Integer> sh = new HashMap<String, Integer>();
		sh.put(Settings.getString("vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(Settings.getString("fragment_path"), GL20.GL_FRAGMENT_SHADER);
		DEFAULT_SHADER_PROGRAM = new DefaultShaderProgram(sh);
		sh = new HashMap<String, Integer>();
		sh.put(Settings.getString("post_vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(Settings.getString("post_fragment_path"), GL20.GL_FRAGMENT_SHADER);
		POST_PROCESS_SHADER_PROGRAM = new PixelShaderProgram(sh);
		
		// Initialize the ScreenQuad
		screen = new ScreenQuad();
		fb = new FrameBuffer(width, height);
		
		// Initialize the texture manager
		texManager = TextureManager.getInstance();
		fbTexUnitId = texManager.getTextureSlot();
		
		init();
	}
	
	/**
	 * Constructor for the renderer (no fog) 
	 * @param width The width of the renderer.
	 * @param height The height of the renderer.
	 * @param camera The camera associated with the renderer.
	 */
	public Renderer(int width, int height, Camera camera) {
		this.camera = camera;
		this.width = width;
		this.height = height;
		this.frameRate = DEFAULT_FRAME_RATE;
		this.fog = new Fog(false);
		
		// Initialize the OpenGL context
		initOpenGL(width <= 0 && height <= 0);
		
		// Initialize shader programs
		Map<String, Integer> sh = new HashMap<String, Integer>();
		sh.put(Settings.getString("vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(Settings.getString("fragment_path"), GL20.GL_FRAGMENT_SHADER);
		DEFAULT_SHADER_PROGRAM = new DefaultShaderProgram(sh);
		sh = new HashMap<String, Integer>();
		sh.put(Settings.getString("post_vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(Settings.getString("post_fragment_path"), GL20.GL_FRAGMENT_SHADER);
		POST_PROCESS_SHADER_PROGRAM = new PixelShaderProgram(sh);
		
		// Initialize the ScreenQuad
		screen = new ScreenQuad();
		fb = new FrameBuffer(width, height);
		
		// Initialize the texture manager
		texManager = TextureManager.getInstance();
		fbTexUnitId = texManager.getTextureSlot();
		
		init();
	}
	
	/**
	 * Constructor for the renderer (no fog)
	 * @param width The width of the renderer.
	 * @param height The height of the renderer.
	 * @param camera The camera associated with the renderer.
	 * @param frameRate The frame rate. 
	 */
	public Renderer(int width, int height, Camera camera, int frameRate) {
		this.camera = camera;
		this.width = width;
		this.height = height;
		this.frameRate = frameRate;
		this.fog = new Fog(false);
		
		// Initialize the OpenGL context
		initOpenGL(width <= 0 && height <= 0);
		
		// Initialize shader programs
		Map<String, Integer> sh = new HashMap<String, Integer>();
		sh.put(Settings.getString("vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(Settings.getString("fragment_path"), GL20.GL_FRAGMENT_SHADER);
		DEFAULT_SHADER_PROGRAM = new DefaultShaderProgram(sh);
		sh = new HashMap<String, Integer>();
		sh.put(Settings.getString("post_vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(Settings.getString("post_fragment_path"), GL20.GL_FRAGMENT_SHADER);
		POST_PROCESS_SHADER_PROGRAM = new PixelShaderProgram(sh);
		
		// Initialize the ScreenQuad
		screen = new ScreenQuad();
		fb = new FrameBuffer(width, height);
		
		// Initialize the texture manager
		texManager = TextureManager.getInstance();
		fbTexUnitId = texManager.getTextureSlot();
		
		init();
	}
	
	/**
	 * Constructor for the renderer
	 * @param width The width of the renderer.
	 * @param height The height of the renderer.
	 * @param camera The camera associated with the renderer.
	 * @param frameRate The frame rate. 
	 * @param fog The fog
	 */
	public Renderer(int width, 
			int height, 
			Camera camera, 
			int frameRate,
			Fog fog) {
		
		this.camera = camera;
		this.width = width;
		this.height = height;
		this.frameRate = frameRate;
		this.fog = fog;
		
		// Initialize the OpenGL context
		initOpenGL(width <= 0 && height <= 0);
		
		// Initialize shader programs
		Map<String, Integer> sh = new HashMap<String, Integer>();
		sh.put(Settings.getString("vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(Settings.getString("fragment_path"), GL20.GL_FRAGMENT_SHADER);		
		DEFAULT_SHADER_PROGRAM = new DefaultShaderProgram(sh);
		sh = new HashMap<String, Integer>();
		sh.put(Settings.getString("post_vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(Settings.getString("post_fragment_path"), GL20.GL_FRAGMENT_SHADER);
		POST_PROCESS_SHADER_PROGRAM = new PixelShaderProgram(sh);
		
		// Initialize the ScreenQuad
		screen = new ScreenQuad();
		fb = new FrameBuffer(width, height);
		
		// Initialize the texture manager
		texManager = TextureManager.getInstance();
		fbTexUnitId = texManager.getTextureSlot();
		
		init();
	}	
	
	/**
	 * Bind a new model to the renderer
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
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, DEFAULT_FRAME_BUFFER);

		// Select shader program.
		ShaderController.setProgram(DEFAULT_SHADER_PROGRAM);
		GL20.glUseProgram(ShaderController.getCurrentProgram());
		
		// Clear the color and depth buffers
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		
		// Set the uniform values of the projection and view matrices 
		viewMatrix = camera.getViewMatrix();
		viewMatrix.store(matrix44Buffer); 
		matrix44Buffer.flip();
		GL20.glUniformMatrix4(ShaderController.getViewMatrixLocation(), false, matrix44Buffer);
		GL20.glUniformMatrix4(ShaderController.getViewMatrixFragLocation(), false, matrix44Buffer);
		
		// Render each model
		for(Model m: models){
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
		
		/*
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, DEFAULT_FRAME_BUFFER);
		
		// If not the default frame buffer, render to the screen
		if(fb.getFrameBuffer() != DEFAULT_FRAME_BUFFER) {
			int testVal = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
			if(testVal == GL30.GL_FRAMEBUFFER_COMPLETE) {
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				
				shader.setProgram(POST_PROCESS_SHADER_PROGRAM);
				GL20.glUseProgram(ShaderController.getCurrentProgram());
				
				GL20.glUniform1i(ShaderController.getFBTexLocation(), fbTexUnitId - GL13.GL_TEXTURE0);
				GL13.glActiveTexture(fbTexUnitId);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, fb.getFrameBufferTexture());
				
				GL30.glBindVertexArray(screen.getVAOId());
				GL20.glEnableVertexAttribArray(0);
				GL20.glEnableVertexAttribArray(1);

				// Draw the quad
				GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

				GL20.glDisableVertexAttribArray(0);
				GL20.glDisableVertexAttribArray(1);
				GL30.glBindVertexArray(0);
			} else {
				System.out.println("Error: " + testVal);
			}
		}
		*/
	
		GL20.glUseProgram(0);

		// Force a maximum FPS of about 60
		Display.sync(frameRate);
		// Let the CPU synchronize with the GPU if GPU is tagging behind (I think update refreshs the display)
		Display.update();
	}
	
	/**
	 * Get the camera associated with this renderer.
	 * @return camera the camera
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
	 * @return frameRate the frame rate 
	 */
	public int getFrameRate() {
		return frameRate;
	}
	
	/**
	 * Get the fog 
	 * @return fog 
	 */
	public Fog getFog() {
		return fog;
	}
	
	/**
	 * Set the fog
	 * @param fog New fog for the renderer
	 */
	public void setFog(Fog fog) {
		this.fog = fog;
		
		// Update the shader uniforms
		if(ShaderController.getCurrentProgram() > 0) {
			GL20.glUseProgram(ShaderController.getCurrentProgram());
			fog.updateFogUniforms(ShaderController.getFogColorLocation(),
					ShaderController.getFogMinDistanceLocation(), 
					ShaderController.getFogMaxDistanceLocation(), 
					ShaderController.getFogEnabledLocation());
			GL20.glUseProgram(ShaderController.getCurrentProgram());
		}
	}
	
	/**
	 * Initializes the renderer
	 */
	private void init() {
		// Set to default shader program
		ShaderController.setProgram(DEFAULT_SHADER_PROGRAM);
		
		models = new ArrayList<Model>();
		
		// Set up view and projection matrices
		projectionMatrix = new Matrix4f();
		float fieldOfView = 45f;
		float aspectRatio = (float)width / (float)height;
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

		// Initialize the uniform variables
		GL20.glUseProgram(ShaderController.getCurrentProgram());

		viewMatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4(ShaderController.getProjectionMatrixLocation(), false, matrix44Buffer);
		projectionMatrix.store(matrix44Buffer); 
		matrix44Buffer.flip();
		GL20.glUniformMatrix4(ShaderController.getProjectionMatrixLocation(), false, matrix44Buffer);
		fog.updateFogUniforms(ShaderController.getFogColorLocation(),
				ShaderController.getFogMinDistanceLocation(), 
				ShaderController.getFogMaxDistanceLocation(), 
				ShaderController.getFogEnabledLocation());

		GL20.glUseProgram(0);
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
				Display.setDisplayMode(new DisplayMode(this.width, this.height));

			Display.setTitle("Game the Name 2.0");
			Display.create(pixelFormat, contextAtr);
			
			if (width != 0 && height != 0)
				setViewPort(0, 0, this.width, this.height);
			
		} catch (LWJGLException e){
			e.printStackTrace();
			System.exit(-1); //quit if opengl context fails
		}
		
		// XNA like background color
		GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);		
		// GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE ); //for debug
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	/**
	 * Sets our view port at (x,y) given a width and height.
	 * @param x - not used
	 * @param y - not used
	 * @param width
	 * @param height
	 */
	private void setViewPort (int x, int y, int width, int height){
		GL11.glViewport(0, 0, width, height);
	}	
}
