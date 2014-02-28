package renderer;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
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
import org.lwjgl.util.vector.Vector4f;

import renderer.framebuffer.FBTarget;
import renderer.framebuffer.FrameBuffer;
import renderer.framebuffer.ScreenQuad;
import renderer.model.Model;
import renderer.shader.ColorPickingShaderProgram;
import renderer.shader.DefaultShaderProgram;
import renderer.shader.PixelShaderProgram;
import renderer.shader.ShaderController;
import renderer.shader.ShaderProgram;
import renderer.shader.SkyboxShaderProgram;
import renderer.util.Skybox;
import system.Settings;
import texture.TextureManager;
import util.MathUtils;
import util.Plane;

/**
 * The renderer class should set up OpenGL.
 * @TODO Move context setting to the client
 * @ADD Setting a client defined default shader
 * @author Adi
 * @author Max
 */
public class Renderer {
	private static final int MAX_MODELS = 100; // Max models on the temp buffer
	private static final Integer DEFAULT_FRAME_BUFFER = 0;
	private static final float FOV = 45f;
	private static final float FAR_PLANE = 100f;
	private static final float NEAR_PLANE = 0.1f;

	// List of the models that will be rendered
	private Set<Model> models;
	private BlockingQueue<Model> modelBuffer;
	private Map<Integer, Model> mapIdToModel;
	private Model pickedModel = null;
	private Skybox skybox = null;
	
	private Context context;
	
	// Matrix variables (should be moved to camera class in the future)
	private Matrix4f projectionMatrix = null;
	private Matrix4f viewMatrix = null;
	private FloatBuffer matrix44Buffer = null;
	
	// Planes that make up the frustrum.
	Plane[] frustrumPlanes = {
			new Plane(),
			new Plane(),
			new Plane(),
			new Plane(),
			new Plane(),
			new Plane()
	};

	// The view matrix will be calculated based off this camera
	private Camera camera = null;    
	
	// Fog instance
	private Fog fog = null;
	
	private ScreenQuad screen;
	
	// The shader programs currently supported
	private final ShaderProgram DEFAULT_SHADER_PROGRAM;
	private final ShaderProgram POST_PROCESS_SHADER_PROGRAM; 
	private final ShaderProgram COLOR_PICKING_SHADER_PROGRAM;
	private final ShaderProgram SKY_BOX_SHADER_PROGRAM;
	
	// Frame buffers
	private FrameBuffer postProcessFb;
	private FrameBuffer colourPickingFb;
	private Set<Conversion> postProcessConversions;
	
	// Instance of the shared settings object.
	private Settings settings = Settings.getInstance();

	/**
	 * Default constructor
	 * @param context The context to build the renderer with 
	 * @param camera The camera associated with the renderer
	 */
	public Renderer(Context context, Camera camera) {
		this.camera = camera;
		this.context = context;
		
		this.fog = new Fog(false);		

		// Initialize the OpenGL context
		initOpenGL();
		
		// Initialize shader programs
		Map<String, Integer> sh = new HashMap<String, Integer>();
		sh.put(settings.get("paths", "vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(settings.get("paths", "fragment_path"), GL20.GL_FRAGMENT_SHADER);
		DEFAULT_SHADER_PROGRAM = new DefaultShaderProgram(sh);
		sh = new HashMap<>();
		sh.put(settings.get("paths", "post_vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(settings.get("paths", "post_fragment_path"), GL20.GL_FRAGMENT_SHADER);
		POST_PROCESS_SHADER_PROGRAM = new PixelShaderProgram(sh);
		sh = new HashMap<>();
		sh.put(settings.get("paths", "picking_vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(settings.get("paths", "picking_frag_path"), GL20.GL_FRAGMENT_SHADER);
		COLOR_PICKING_SHADER_PROGRAM = new ColorPickingShaderProgram(sh);
		sh = new HashMap<>();
		sh.put(settings.get("paths", "skybox_vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(settings.get("paths", "skybox_fragment_path"), GL20.GL_FRAGMENT_SHADER);
		SKY_BOX_SHADER_PROGRAM = new SkyboxShaderProgram(sh);
		
		// Initialize the ScreenQuad
		screen = new ScreenQuad(); 
		List<FBTarget> targets = new ArrayList<>();
		targets.add(FBTarget.GL_COLOR_ATTACHMENT);
		targets.add(FBTarget.GL_DEPTH_ATTACHMENT);
		
		postProcessFb = new FrameBuffer(context.width, context.height, targets);
		colourPickingFb = new FrameBuffer(context.width, context.height, Collections.singletonList(FBTarget.GL_COLOR_ATTACHMENT));
		
		init();
	}
	
	/**
	 * Constructor for the renderer
	 * @param context The context to build the renderer with
	 * @param frameRate The frame rate. 
	 * @param fog The fog
	 */
	public Renderer(Context context, Camera camera, Fog fog) {
		this.camera = camera;
		this.context = context;
		
		this.fog = fog;
		
		// Initialize the OpenGL context
		initOpenGL();
		
		// Initialize shader programs
		Map<String, Integer> sh = new HashMap<String, Integer>();
		sh.put(settings.get("paths", "vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(settings.get("paths", "fragment_path"), GL20.GL_FRAGMENT_SHADER);		
		DEFAULT_SHADER_PROGRAM = new DefaultShaderProgram(sh);
		sh = new HashMap<String, Integer>();
		sh.put(settings.get("paths", "post_vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(settings.get("paths", "post_fragment_path"), GL20.GL_FRAGMENT_SHADER);
		POST_PROCESS_SHADER_PROGRAM = new PixelShaderProgram(sh);
		sh = new HashMap<>();
		sh.put(settings.get("paths", "picking_vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(settings.get("paths", "picking_fragment_path"), GL20.GL_FRAGMENT_SHADER);
		COLOR_PICKING_SHADER_PROGRAM = new ColorPickingShaderProgram(sh);
		sh = new HashMap<>();
		sh.put(settings.get("paths", "skybox_vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(settings.get("paths", "skybox_fragment_path"), GL20.GL_FRAGMENT_SHADER);
		SKY_BOX_SHADER_PROGRAM = new SkyboxShaderProgram(sh);
		
		// Initialize the ScreenQuad
		screen = new ScreenQuad();
		List<FBTarget> targets = new ArrayList<>();
		targets.add(FBTarget.GL_COLOR_ATTACHMENT);
		targets.add(FBTarget.GL_DEPTH_ATTACHMENT);
		
		postProcessFb = new FrameBuffer(context.width, context.height, targets);
		colourPickingFb = new FrameBuffer(context.width, context.height, Collections.singletonList(FBTarget.GL_COLOR_ATTACHMENT));
		
		init();
	}	
	
	/**
	 * Bind a new model to the renderer (buffers up model to be added during main render loop)
	 * @see Model
	 */
	public void addModel(Model model) throws IllegalStateException {
		modelBuffer.add(model);
		mapIdToModel.put(model.getUID(), model);
	}
	
	/**
	 * Add a Skybox to the renderer
	 * @param skybox
	 */
	public void addSkybox(Skybox skybox) {
		this.skybox = skybox;
	}

	/**
	 * Removes a model from the renderer
	 * @see Model
	 */
	public void removeModel(Model model) {
		models.remove(model);	
		mapIdToModel.remove(model.getUID());
	}
	
	/**
	 * Remove the Skybox currently attached to the renderer
	 */
	public void removeSkybox() {
		skybox = null;
	}

	public void renderColourPicking() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, colourPickingFb.getFrameBuffer());	
		GL11.glViewport(0, 0, context.width, context.height);

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
		for(Model m: models) {
			if (!m.isBound()) {
				m.bind();
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
		
		GL11.glViewport(0, 0, context.width, context.height);
		
		// Clear the color and depth buffers
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		// Render the skybox first 
		if(skybox != null) {
			ShaderController.setProgram(SKY_BOX_SHADER_PROGRAM);
			GL20.glUseProgram(ShaderController.getCurrentProgram());
			Matrix4f rotMatrix = camera.getRotationMatrix();
			rotMatrix.store(matrix44Buffer);
			matrix44Buffer.flip();
			GL20.glUniformMatrix4(ShaderController.getViewMatrixLocation(), false, matrix44Buffer);
			skybox.render();
			ShaderController.setProgram(DEFAULT_SHADER_PROGRAM);
		}
		
		// Select shader program.
		ShaderController.setProgram(DEFAULT_SHADER_PROGRAM);
		GL20.glUseProgram(ShaderController.getCurrentProgram());
		
		// Set the uniform values of the view matrix 
		viewMatrix = camera.getViewMatrix();
		viewMatrix.store(matrix44Buffer); 
		matrix44Buffer.flip();
		GL20.glUniformMatrix4(ShaderController.getViewMatrixLocation(), false, matrix44Buffer);
		GL20.glUniformMatrix4(ShaderController.getViewMatrixFragLocation(), false, matrix44Buffer);

		// Render each model
		int modelsRendered = 0;
		for(Model m: models){
			if (!m.isBound()) {
				m.bind();
			}
			if (!m.shouldCull() || isInView(m)) {
				m.setPickedFlag(m.equals(pickedModel));
				m.render(viewMatrix);
				modelsRendered++;
			}
		}
		System.out.println("Rendered " + modelsRendered + " models");
        		
		// Deselect
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

		// Render frame buffer to screen if needed
		if(!postProcessConversions.isEmpty()) {
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, DEFAULT_FRAME_BUFFER);
			GL11.glViewport(-context.width, -context.height, context.width * 2, context.height * 2); // @TODO: Fix hack

			int testVal = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
			if(testVal == GL30.GL_FRAMEBUFFER_COMPLETE) {
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

				ShaderController.setProgram(POST_PROCESS_SHADER_PROGRAM);
				GL20.glUseProgram(ShaderController.getCurrentProgram());

				TextureManager tm = TextureManager.getInstance();
				Integer unitIdColour = tm.getTextureSlot();
				Integer unitIdDepth = tm.getTextureSlot();
				
				GL13.glActiveTexture(unitIdColour);
				GL20.glUniform1i(ShaderController.getFBTexLocation(), unitIdColour - GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, postProcessFb.getFrameBufferTexture(FBTarget.GL_COLOR_ATTACHMENT));

				// Regenerate the mip map
				GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
				
				GL13.glActiveTexture(unitIdDepth);
				GL20.glUniform1i(ShaderController.getDepthTextureLocation(), unitIdDepth - GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, postProcessFb.getFrameBufferTexture(FBTarget.GL_DEPTH_ATTACHMENT));

				// Bind the VAO for the Screen Quad
				GL30.glBindVertexArray(screen.getVAOId());

				// Draw the quad
				GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

				// Unbind 
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
				GL30.glBindVertexArray(0);
				
				ShaderController.setProgram(DEFAULT_SHADER_PROGRAM);
				tm.returnTextureSlot(unitIdColour);
				tm.returnTextureSlot(unitIdDepth);
			} else {
				System.out.println("Error: " + testVal);
			}
		}

		GL20.glUseProgram(0);
		Display.sync(context.frameRate);
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
	public Camera getCamera() throws NullPointerException {
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
		return context.frameRate;
	}
	
	public int getWidth() {
		return context.width;
	}
	
	public int getHeight() {
		return context.height;
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
	 * Returns true if the model is currently in view, and false otherwise.
	 * This is used for frustum culling to only render models whose bounding boxes are in view.
	 */
	private boolean isInView (Model m) {
		float[] pts = m.getBoundingBox().getVertexList();
		Vector4f[] transformedPts = new Vector4f[8];
		Matrix4f tMat = m.getPhysicsModel().getTransformMatrix();

		for (int i = 0; i < pts.length; i+=4) {
			Vector4f mPt = new Vector4f(pts[i], pts[i+1], pts[i+2], pts[i+3]);
			Matrix4f.transform(tMat, mPt, mPt);
			Matrix4f.transform(viewMatrix, mPt, mPt);
			transformedPts[i/4] = mPt;
		}
		
		boolean outsideFrustrum = true;
		for (int i = 0; i < frustrumPlanes.length; i++) {
			for (int j = 0; j < transformedPts.length; j++) {
				float dP = MathUtils.dotPlaneWithVector(frustrumPlanes[i], transformedPts[j]);
				outsideFrustrum &= dP < 0f;
			}
			if (outsideFrustrum == true) return false;
			outsideFrustrum = true;
		}
		return true;
	}

	/**
	 * Computes the frustum planes using the projection matrix (see http://graphics.cs.ucf.edu/cap4720/fall2008/plane_extraction.pdf).
	 * This algorithm gives the planes in view space (camera space).
	 */
	private void computeFrustumPlanes() {
		// Left plane.
		frustrumPlanes[0].a = projectionMatrix.m03 + projectionMatrix.m00; 
		frustrumPlanes[0].b = projectionMatrix.m13 + projectionMatrix.m10;
		frustrumPlanes[0].c = projectionMatrix.m23 + projectionMatrix.m20;
		frustrumPlanes[0].d = projectionMatrix.m33 + projectionMatrix.m30;

		// Right plane.
		frustrumPlanes[1].a = projectionMatrix.m03 - projectionMatrix.m00; 
		frustrumPlanes[1].b = projectionMatrix.m13 - projectionMatrix.m10;
		frustrumPlanes[1].c = projectionMatrix.m23 - projectionMatrix.m20;
		frustrumPlanes[1].d = projectionMatrix.m33 - projectionMatrix.m30;

		// Top plane.
		frustrumPlanes[2].a = projectionMatrix.m03 - projectionMatrix.m01; 
		frustrumPlanes[2].b = projectionMatrix.m13 - projectionMatrix.m11;
		frustrumPlanes[2].c = projectionMatrix.m23 - projectionMatrix.m21;
		frustrumPlanes[2].d = projectionMatrix.m33 - projectionMatrix.m31;

		// Bottom plane.
		frustrumPlanes[3].a = projectionMatrix.m03 + projectionMatrix.m01;
		frustrumPlanes[3].b = projectionMatrix.m13 + projectionMatrix.m11;
		frustrumPlanes[3].c = projectionMatrix.m23 + projectionMatrix.m21;
		frustrumPlanes[3].d = projectionMatrix.m33 + projectionMatrix.m31;

		// Near plane.
		frustrumPlanes[4].a = projectionMatrix.m30 + projectionMatrix.m20;
		frustrumPlanes[4].b = projectionMatrix.m31 + projectionMatrix.m21;
		frustrumPlanes[4].c = projectionMatrix.m32 + projectionMatrix.m22;
		frustrumPlanes[4].d = NEAR_PLANE;
		
		// Far plane.
		frustrumPlanes[5].a = projectionMatrix.m30 - projectionMatrix.m20;
		frustrumPlanes[5].b = projectionMatrix.m31 - projectionMatrix.m21;
		frustrumPlanes[5].c = projectionMatrix.m32 - projectionMatrix.m22;
		frustrumPlanes[5].d = FAR_PLANE;

		// Normalize plane normals.
		for (int i = 0; i < frustrumPlanes.length; i++) {
			MathUtils.normalizePlane(frustrumPlanes[i]);
		}
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
		float fieldOfView = FOV;
		float aspectRatio = (float)context.width / (float)context.height;
		float near_plane = NEAR_PLANE;
		float far_plane = FAR_PLANE;

		float y_scale = (float)(1 / Math.tan((Math.toRadians(fieldOfView / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = far_plane - near_plane;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((far_plane + near_plane) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * near_plane * far_plane) / frustum_length);
		projectionMatrix.m33 = 0;
		
		computeFrustumPlanes();

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
		ShaderController.setProgram(SKY_BOX_SHADER_PROGRAM);
		GL20.glUseProgram(ShaderController.getCurrentProgram());
		GL20.glUniformMatrix4(ShaderController.getProjectionMatrixLocation(), false, matrix44Buffer);
		ShaderController.setProgram(POST_PROCESS_SHADER_PROGRAM);
		GL20.glUseProgram(ShaderController.getCurrentProgram());
		System.out.println("Current Program = " + ShaderController.getCurrentProgram());
		System.out.println("Proj = " + ShaderController.getProjectionMatrixLocation());
		GL20.glUniformMatrix4(ShaderController.getProjectionMatrixLocation(), false, matrix44Buffer);
		
		ShaderController.setProgram(DEFAULT_SHADER_PROGRAM);
		GL20.glUseProgram(0);
	}
	
	/**
	 * Initializes OpenGL (currently using 3.2).
	 * @param fullscreen Determines whether we should run in fullscreen.
	 */
	private void initOpenGL() {
		try{
			PixelFormat pixelFormat = new PixelFormat();
			ContextAttribs contextAtr = new ContextAttribs(context.majorVersion, context.minorVersion)
				.withForwardCompatible(true)
				.withProfileCore(context.useCore);

			if (context.useFullscreen) 
				Display.setFullscreen(true);
			else 
				Display.setDisplayMode(new DisplayMode(context.width, context.height));

			Display.setTitle(context.title);
			Display.create(pixelFormat, contextAtr);
			
			if (context.width != 0 && context.height != 0)
				setViewPort(0, 0, context.width, context.height);
			
		} catch (LWJGLException e){
			e.printStackTrace();
			System.exit(-1); // Quit if OpenGL context fails
		}
		
		// XNA like background color
		GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);		
		// GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE); //for debug
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
