package renderer;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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

import renderer.framebuffer.FrameBuffer;
import renderer.framebuffer.ScreenQuad;
import renderer.model.Model;
import renderer.shader.ColorPickingShaderProgram;
import renderer.shader.DefaultShaderProgram;
import renderer.shader.PixelShaderProgram;
import renderer.shader.ShaderController;
import renderer.shader.ShaderProgram;
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
	private static final int MAX_MODELS = 100;

	// List of the models that will be rendered
	private Set<Model> models;
	private BlockingQueue<Model> modelBuffer;
	private Map<Integer, Model> mapIdToModel;
	private Model pickedModel = null;
	
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
	private final ShaderProgram COLOR_PICKING_SHADER_PROGRAM;
	
	// Frame buffers
	private FrameBuffer postProcessFb;
	private FrameBuffer colourPickingFb;
	private final Integer DEFAULT_FRAME_BUFFER = 0;
	private Set<Conversion> postProcessConversions;
	
	// The frame buffer has its own unit id (for safety)
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
		sh = new HashMap<>();
		sh.put(Settings.getString("post_vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(Settings.getString("post_fragment_path"), GL20.GL_FRAGMENT_SHADER);
		POST_PROCESS_SHADER_PROGRAM = new PixelShaderProgram(sh);
		sh = new HashMap<>();
		sh.put(Settings.getString("picking_vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(Settings.getString("picking_frag_path"), GL20.GL_FRAGMENT_SHADER);
		COLOR_PICKING_SHADER_PROGRAM = new ColorPickingShaderProgram(sh);
		
		// Initialize the texture manager
		texManager = TextureManager.getInstance();
		fbTexUnitId = texManager.getTextureSlot();
		
		// Initialize the ScreenQuad
		screen = new ScreenQuad();
		postProcessFb = new FrameBuffer(width, height);
		colourPickingFb = new FrameBuffer(width, height);
		
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
		sh = new HashMap<>();
		sh.put(Settings.getString("picking_vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(Settings.getString("picking_frag_path"), GL20.GL_FRAGMENT_SHADER);
		COLOR_PICKING_SHADER_PROGRAM = new ColorPickingShaderProgram(sh);
		
		// Initialize the ScreenQuad
		screen = new ScreenQuad();
		postProcessFb = new FrameBuffer(width, height);
		colourPickingFb = new FrameBuffer(width, height);
		
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
		sh = new HashMap<>();
		sh.put(Settings.getString("picking_vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(Settings.getString("picking_frag_path"), GL20.GL_FRAGMENT_SHADER);
		COLOR_PICKING_SHADER_PROGRAM = new ColorPickingShaderProgram(sh);
		
		// Initialize the ScreenQuad
		screen = new ScreenQuad();
		postProcessFb = new FrameBuffer(width, height);
		colourPickingFb = new FrameBuffer(width, height);
		
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
		sh = new HashMap<>();
		sh.put(Settings.getString("picking_vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(Settings.getString("picking_fragment_path"), GL20.GL_FRAGMENT_SHADER);
		COLOR_PICKING_SHADER_PROGRAM = new ColorPickingShaderProgram(sh);
		
		// Initialize the ScreenQuad
		screen = new ScreenQuad();
		postProcessFb = new FrameBuffer(width, height);
		colourPickingFb = new FrameBuffer(width, height);
		
		// Initialize the texture manager
		texManager = TextureManager.getInstance();
		fbTexUnitId = texManager.getTextureSlot();
		
		init();
	}	
	
	/**
	 * Bind a new model to the renderer
	 * @see Model
	 */
	public void bindNewModel(Model model) {
		modelBuffer.add(model);
		mapIdToModel.put(model.getUID(), model);
	}

	/**
	 * Removes a model from the renderer
	 * @see Model
	 */
	public void removeModel(Model model) {
		models.remove(model);
		
		mapIdToModel.remove(model.getUID());
	}

	public void renderColourPicking() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, colourPickingFb.getFrameBuffer());	
		GL11.glViewport(0, 0, width, height);

		// Select shader program.
		ShaderController.setProgram(COLOR_PICKING_SHADER_PROGRAM);
		GL20.glUseProgram(ShaderController.getCurrentProgram());
		
		// Clear the color and depth buffers
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		// Set the uniform values of the view matrix 
		viewMatrix = camera.getViewMatrix();
		viewMatrix.store(matrix44Buffer); 
		matrix44Buffer.flip();
		GL20.glUniformMatrix4(ShaderController.getViewMatrixLocation(), false, matrix44Buffer);
		
		// Render each model
		for(Model m: models){
			if (!m.isGLsetup()) {
				m.setupGL();
			} 
			m.renderPicking();
		}
		
		// Deselect
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, DEFAULT_FRAME_BUFFER);
		ShaderController.setProgram(DEFAULT_SHADER_PROGRAM);
		GL20.glUseProgram(0);
	}
	/**
	 * Renders the new scene.
	 */
	public void renderScene () {
		// Render to texture if post process set is not empty
		if(!postProcessConversions.isEmpty()) {
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, postProcessFb.getFrameBuffer());
		}
		
		GL11.glViewport(0, 0, width, height);

		// Select shader program.
		ShaderController.setProgram(DEFAULT_SHADER_PROGRAM);
		GL20.glUseProgram(ShaderController.getCurrentProgram());
		
		// Clear the color and depth buffers
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		// Set the uniform values of the view matrix 
		viewMatrix = camera.getViewMatrix();
		viewMatrix.store(matrix44Buffer); 
		matrix44Buffer.flip();
		GL20.glUniformMatrix4(ShaderController.getViewMatrixLocation(), false, matrix44Buffer);
		GL20.glUniformMatrix4(ShaderController.getViewMatrixFragLocation(), false, matrix44Buffer);
		
		// Render each model
		for(Model m: models){
			if (!m.isGLsetup()) {
				m.setupGL();
			} 
			m.render(m.equals(pickedModel));
		}
        		
		// Deselect
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

		// Render frame buffer to screen if needed
		if(!postProcessConversions.isEmpty()) {
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, DEFAULT_FRAME_BUFFER);
			GL11.glViewport(-width, -height, width * 2, height * 2); // @TODO: Hack

			int testVal = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
			if(testVal == GL30.GL_FRAMEBUFFER_COMPLETE) {
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

				ShaderController.setProgram(POST_PROCESS_SHADER_PROGRAM);
				GL20.glUseProgram(ShaderController.getCurrentProgram());

				GL13.glActiveTexture(fbTexUnitId);
				GL20.glUniform1i(ShaderController.getFBTexLocation(), fbTexUnitId - GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, postProcessFb.getFrameBufferTexture());

				// Regenerate the mip map
				GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

				// Bind the VAO for the Screen Quad
				GL30.glBindVertexArray(screen.getVAOId());

				// Draw the quad
				GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

				// Unbind 
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
				GL30.glBindVertexArray(0);
				ShaderController.setProgram(DEFAULT_SHADER_PROGRAM);
			} else {
				System.out.println("Error: " + testVal);
			}
		}

		GL20.glUseProgram(0);
		// Force a maximum FPS of about 60
		Display.sync(frameRate);
		// Let the CPU synchronize with the GPU if GPU is tagging behind (I think update refreshs the display)
		Display.update();
	}
	
	/**
	 * Takes model buffer and places it in the main set
	 */
	public void updateModels() {
		modelBuffer.drainTo(models);
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
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	/**
	 * Get the fog 
	 * @return fog 
	 */
	public Fog getFog() {
		return fog;
	}
	
	/**
	 * Get model based off of UID
	 * @return model
	 */
	public Model getModel(int id) {
		return mapIdToModel.get(id);
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
	 * Select or deselect current colour picked model 
	 * @param x x-coordinate to sample pixel
	 * @param y y-coordinate to sample pixel
	 */
	public void selectPickedModel(int x, int y) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, colourPickingFb.getFrameBuffer());	
		FloatBuffer pixel = BufferUtils.createFloatBuffer(4);
		GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGBA, GL11.GL_FLOAT, pixel);
		int modelId = getModelId(pixel.get(0), pixel.get(1), pixel.get(2));
		
		// Check if the model is valid
		if(mapIdToModel.containsKey(modelId)) {
			// Select if not picked
			if(!mapIdToModel.get(modelId).equals(pickedModel)) {
				pickedModel = mapIdToModel.get(modelId);
			} else {
				pickedModel = null;
			}
		} else {
			pickedModel = null;
		}
		
		// System.out.println("Id = " + modelId + " Exists? = " + this.mapIdToModel.containsKey(modelId) + " Keys: " + this.mapIdToModel.keySet());
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, DEFAULT_FRAME_BUFFER);
	}
	
	public boolean addImageConversion(Conversion conversion) {
		return postProcessConversions.add(conversion);
	}
	
	public boolean removeImageConversion(Conversion conversion) {
		return postProcessConversions.remove(conversion);
	}
	
	public void resetImageConversions() {
		postProcessConversions = new HashSet<>();
	}
	
	/**
	 * Initializes the renderer
	 */
	private void init() {		
		modelBuffer = new ArrayBlockingQueue<>(MAX_MODELS);
		models = new HashSet<>();
		mapIdToModel = new HashMap<>();
		postProcessConversions = new HashSet<>();
		
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
		viewMatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		
		// Initialize the uniform variables
		ShaderController.setProgram(DEFAULT_SHADER_PROGRAM);
		GL20.glUseProgram(ShaderController.getCurrentProgram());
		GL20.glUniformMatrix4(ShaderController.getViewMatrixLocation(), false, matrix44Buffer);
		fog.updateFogUniforms(ShaderController.getFogColorLocation(),
				ShaderController.getFogMinDistanceLocation(), 
				ShaderController.getFogMaxDistanceLocation(), 
				ShaderController.getFogEnabledLocation());
		
		ShaderController.setProgram(COLOR_PICKING_SHADER_PROGRAM);
		GL20.glUseProgram(ShaderController.getCurrentProgram());
		GL20.glUniformMatrix4(ShaderController.getViewMatrixLocation(), false, matrix44Buffer);
		
		// Set projection matrix
		projectionMatrix.store(matrix44Buffer); 
		matrix44Buffer.flip();
		
		ShaderController.setProgram(DEFAULT_SHADER_PROGRAM);
		GL20.glUseProgram(ShaderController.getCurrentProgram());
		GL20.glUniformMatrix4(ShaderController.getProjectionMatrixLocation(), false, matrix44Buffer);
		ShaderController.setProgram(COLOR_PICKING_SHADER_PROGRAM);
		GL20.glUseProgram(ShaderController.getCurrentProgram());
		GL20.glUniformMatrix4(ShaderController.getProjectionMatrixLocation(), false, matrix44Buffer);
		
		ShaderController.setProgram(DEFAULT_SHADER_PROGRAM);
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
	
	/**
	 * Get model ID from colour (for colour picking) 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	private int getModelId(float r, float g, float b) {
		int red = (int)Math.ceil(r * 255);
		int green = (int)Math.ceil(g * 255);
		int blue = (int)Math.ceil(b * 255);
		
		int rgb = ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff);
		System.out.println("Decode: Num = " + rgb + ", R = " + red + ", G = " + green + ", B = " + blue);
		return rgb;
	}
}
