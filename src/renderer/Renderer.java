package renderer;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import renderer.shader.TextShaderProgram;
import renderer.util.Skybox;
import renderer.util.TextManager;
import renderer.util.TextRenderer;
import system.Settings;
import texture.Texture;
import texture.TextureLoader;
import texture.TextureManager;
import util.MathUtils;
import util.Plane;

/**
 * The renderer class should set up OpenGL.
 * @TODO(MZ): Move context setting to the client
 * @TODO(MZ): Setting a client defined default shader
 * @author Adi
 * @author Max
 */
public class Renderer {
	public static final int MAX_MODELS = 100; // Max models on the temp buffer
	public static final Integer DEFAULT_FRAME_BUFFER = 0; 
	public static final float DEFAULT_FOV = 45f;
	public static final float DEFAULT_FAR_PLANE = 100f;
	public static final float DEFAULT_NEAR_PLANE = 0.1f;
	
	private float mAspectRatio = 1080f / 920f; //@TODO(MZ): Setting FOV, near, far planes
	private float mFov = DEFAULT_FOV;
	private float mNear = DEFAULT_NEAR_PLANE;
	private float mFar = DEFAULT_FAR_PLANE;

	// List of the models that will be rendered
	private Set<Model> mModels;
	private BlockingQueue<Model> mModelBuffer;
	private Map<Integer, Model> mMpIdModel;
	private Model pickedModel = null;
	private Skybox skybox = null;
	
	private Context context;
	
	// Matrix variables (should be moved to camera class in the future)
	private Matrix4f projectionMatrix = null;
	private Matrix4f viewMatrix = null;
	private FloatBuffer matrix44Buffer = null;
	
	// Planes that make up the frustum.
	Plane[] frustumPlanes = {
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
	private final ShaderProgram TEXT_SHADER_PROGRAM;

	// Frame buffers
	private FrameBuffer postProcessFb;
	private FrameBuffer colourPickingFb;
	private Set<Conversion> postProcessConversions;
	
	// Instance of the shared settings object.
	private Settings settings = Settings.getInstance();
	private TextManager textManager = TextManager.getInstance();

	private TextRenderer textRenderer;
	
	// Noise texture
	private Texture noiseTex;

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
		
		sh = new HashMap<>();
		sh.put(settings.get("paths", "text_vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(settings.get("paths", "text_fragment_path"), GL20.GL_FRAGMENT_SHADER);
		TEXT_SHADER_PROGRAM = new TextShaderProgram(sh);

		// Initialize the ScreenQuad
		List<FBTarget> targets = new ArrayList<>();
		targets.add(FBTarget.GL_COLOR_ATTACHMENT);
		targets.add(FBTarget.GL_DEPTH_ATTACHMENT);
		targets.add(FBTarget.GL_NORMAL_ATTACHMENT);
		
		postProcessFb = new FrameBuffer(context.width, context.height, targets);
		colourPickingFb = new FrameBuffer(context.width, context.height, Collections.singletonList(FBTarget.GL_COLOR_ATTACHMENT));

		textRenderer = new TextRenderer("consolas.png", TEXT_SHADER_PROGRAM);
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
		
		sh = new HashMap<>();
		sh.put(settings.get("paths", "text_vertex_path"), GL20.GL_VERTEX_SHADER);
		sh.put(settings.get("paths", "text_fragment_path"), GL20.GL_FRAGMENT_SHADER);
		TEXT_SHADER_PROGRAM = new TextShaderProgram(sh);

		
		// Shader outputs (order must match output of fragment shader)
		List<FBTarget> targets = new ArrayList<>();
		targets.add(FBTarget.GL_COLOR_ATTACHMENT);
		targets.add(FBTarget.GL_DEPTH_ATTACHMENT);
		targets.add(FBTarget.GL_NORMAL_ATTACHMENT);

		postProcessFb = new FrameBuffer(context.width, context.height, targets);
		colourPickingFb = new FrameBuffer(context.width, context.height, Collections.singletonList(FBTarget.GL_COLOR_ATTACHMENT));
		
		textRenderer = new TextRenderer("consolas.png", TEXT_SHADER_PROGRAM);
		init();
	}	
	
	/**
	 * Bind a new model to the renderer (buffers up model to be added during main render loop)
	 * @see Model
	 */
	public void addModel(Model model) throws IllegalStateException {
		mModelBuffer.add(model);
		mMpIdModel.put(model.getUID(), model);
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
		mModels.remove(model);	
		mMpIdModel.remove(model.getUID());
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
		for(Model m: mModels) {
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
		
		// Clear the color and depth buffers
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glViewport(0, 0, context.width, context.height);

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

		// Render each model
		for(Model m: mModels){
			if (!m.isBound()) {
				m.bind();
			}
			if (!m.shouldCull() || isInView(m)) {
				m.setPickedFlag(m.equals(pickedModel));
				m.render(viewMatrix);
			}
		}

		// Deselect
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

		// Render frame buffer to screen if needed (@TODO: Move to ScreenQuad)
		if(!postProcessConversions.isEmpty()) {
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, DEFAULT_FRAME_BUFFER);
			GL11.glViewport(0, 0, context.width, context.height);

			int testVal = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
			if(testVal == GL30.GL_FRAMEBUFFER_COMPLETE) {
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

				ShaderController.setProgram(POST_PROCESS_SHADER_PROGRAM);
				GL20.glUseProgram(ShaderController.getCurrentProgram());
				
				viewMatrix.store(matrix44Buffer); 
				matrix44Buffer.flip();
				GL20.glUniformMatrix4(ShaderController.getViewMatrixLocation(), false, matrix44Buffer);
				
				TextureManager tm = TextureManager.getInstance();
				Integer unitIdColour = tm.getTextureSlot();
				Integer unitIdDepth = tm.getTextureSlot();
				Integer unitIdNormal = tm.getTextureSlot();
				Integer unitIdNoise = tm.getTextureSlot();
				
				GL13.glActiveTexture(unitIdColour);
				GL20.glUniform1i(ShaderController.getFBTexLocation(), unitIdColour - GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, postProcessFb.getFrameBufferTexture(FBTarget.GL_COLOR_ATTACHMENT));
				
				GL13.glActiveTexture(unitIdDepth);
				GL20.glUniform1i(ShaderController.getDepthTextureLocation(), unitIdDepth - GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, postProcessFb.getFrameBufferTexture(FBTarget.GL_DEPTH_ATTACHMENT));
				
				GL13.glActiveTexture(unitIdNormal);
				GL20.glUniform1i(ShaderController.getNormalTextureLocation(), unitIdNormal - GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, postProcessFb.getFrameBufferTexture(FBTarget.GL_NORMAL_ATTACHMENT));
				
				GL13.glActiveTexture(unitIdNoise);
				GL20.glUniform1i(ShaderController.getNoiseTextureLocation(), unitIdNoise - GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, noiseTex.getID());
				
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
				tm.returnTextureSlot(unitIdNormal);
				tm.returnTextureSlot(unitIdNoise);
			} else {
				System.out.println("Error: " + testVal);
			}
		}

		// Render text on the screen.
		ShaderController.setProgram(TEXT_SHADER_PROGRAM);
		GL20.glUseProgram(ShaderController.getCurrentProgram());
		textRenderer.render();
		ShaderController.setProgram(DEFAULT_SHADER_PROGRAM);

		GL20.glUseProgram(0);
		Display.sync(context.frameRate);
		Display.update();
	}
	
	/**
	 * Takes model buffer and places it in the main set
	 */
	public void updateModels() {
		mModelBuffer.drainTo(mModels);
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
		return mMpIdModel.get(id);
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
		if(mMpIdModel.containsKey(modelId)) {
			// Select if not picked
			if(!mMpIdModel.get(modelId).equals(pickedModel)) {
				pickedModel = mMpIdModel.get(modelId);
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
		
		boolean outsidefrustum = true;
		for (int i = 0; i < frustumPlanes.length; i++) {
			for (int j = 0; j < transformedPts.length; j++) {
				float dP = MathUtils.dotPlaneWithVector(frustumPlanes[i], transformedPts[j]);
				outsidefrustum &= dP < 0f;
			}
			if (outsidefrustum == true) return false;
			outsidefrustum = true;
		}
		return true;
	}

	/**
	 * Computes the frustum planes using the projection matrix (see http://graphics.cs.ucf.edu/cap4720/fall2008/plane_extraction.pdf).
	 * This algorithm gives the planes in view space (camera space).
	 */
	private void computeFrustumPlanes() {
		// Left plane.
		frustumPlanes[0].a = projectionMatrix.m03 + projectionMatrix.m00; 
		frustumPlanes[0].b = projectionMatrix.m13 + projectionMatrix.m10;
		frustumPlanes[0].c = projectionMatrix.m23 + projectionMatrix.m20;
		frustumPlanes[0].d = projectionMatrix.m33 + projectionMatrix.m30;

		// Right plane.
		frustumPlanes[1].a = projectionMatrix.m03 - projectionMatrix.m00; 
		frustumPlanes[1].b = projectionMatrix.m13 - projectionMatrix.m10;
		frustumPlanes[1].c = projectionMatrix.m23 - projectionMatrix.m20;
		frustumPlanes[1].d = projectionMatrix.m33 - projectionMatrix.m30;

		// Top plane.
		frustumPlanes[2].a = projectionMatrix.m03 - projectionMatrix.m01; 
		frustumPlanes[2].b = projectionMatrix.m13 - projectionMatrix.m11;
		frustumPlanes[2].c = projectionMatrix.m23 - projectionMatrix.m21;
		frustumPlanes[2].d = projectionMatrix.m33 - projectionMatrix.m31;

		// Bottom plane.
		frustumPlanes[3].a = projectionMatrix.m03 + projectionMatrix.m01;
		frustumPlanes[3].b = projectionMatrix.m13 + projectionMatrix.m11;
		frustumPlanes[3].c = projectionMatrix.m23 + projectionMatrix.m21;
		frustumPlanes[3].d = projectionMatrix.m33 + projectionMatrix.m31;

		// Near plane.
		frustumPlanes[4].a = projectionMatrix.m30 + projectionMatrix.m20;
		frustumPlanes[4].b = projectionMatrix.m31 + projectionMatrix.m21;
		frustumPlanes[4].c = projectionMatrix.m32 + projectionMatrix.m22;
		frustumPlanes[4].d = DEFAULT_NEAR_PLANE;
		
		// Far plane.
		frustumPlanes[5].a = projectionMatrix.m30 - projectionMatrix.m20;
		frustumPlanes[5].b = projectionMatrix.m31 - projectionMatrix.m21;
		frustumPlanes[5].c = projectionMatrix.m32 - projectionMatrix.m22;
		frustumPlanes[5].d = DEFAULT_FAR_PLANE;

		// Normalize plane normals.
		for (int i = 0; i < frustumPlanes.length; i++) {
			MathUtils.normalizePlane(frustumPlanes[i]);
		}
	}

	/**
	 * Initializes the renderer
	 */
	private void init() {		
		mModelBuffer = new ArrayBlockingQueue<>(MAX_MODELS);
		mModels = new HashSet<>();
		mMpIdModel = new HashMap<>();
		postProcessConversions = new HashSet<>();
		
		// Set up view and projection matrices
		projectionMatrix = new Matrix4f();
		mAspectRatio = (float)context.width / (float)context.height; //@TODO(MZ): Setting FOV, near, far planes

		float ySfl = (float)(1 / Math.tan((Math.toRadians(mFov / 2f))));
		float xSfl = ySfl / mAspectRatio;
		float dulFrustum = mFar - mNear;

		projectionMatrix.m00 = xSfl;
		projectionMatrix.m11 = ySfl;
		projectionMatrix.m22 = -((mFar + mNear) / dulFrustum);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * mNear * mFar) / dulFrustum);
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
		
		// Set the near and far planes for the post processing shader
		ShaderController.setProgram(POST_PROCESS_SHADER_PROGRAM);
		// region: @TODO: Move setting uniforms to Renderable 
		GL20.glUseProgram(ShaderController.getCurrentProgram());
		GL20.glUniform1f(ShaderController.getNearPlaneLocation(), mNear);
		GL20.glUniform1f(ShaderController.getFarPlaneLocation(), mFar);
		screen = new ScreenQuad(mFov, mFar, mAspectRatio); 
		screen.setUniforms();	
		// endregion
		
		// Set up the noise texture
		TextureManager tm = TextureManager.getInstance();
		Integer unitIdNoise = tm.getTextureSlot();
		noiseTex = TextureLoader.loadRandomTexture2D(this.getWidth(), this.getHeight(), "noiseTex", 4);
		noiseTex.bind(unitIdNoise, ShaderController.getNoiseTextureLocation());
		tm.returnTextureSlot(unitIdNoise);
		
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
