package renderer.framebuffer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import renderer.shader.ShaderController;

/**
 * ScreenQuad class defines a quad that spans the screen
 * TODO(MZ): This should implement Renderable
 * @author Max
 *
 */
public class ScreenQuad {

	// Screen quad coordinates
	private static final float SCREEN_QUAD_COORDINATES[] = {
		-1.0f, -1.0f,
		1.0f, -1.0f,
		1.0f,  1.0f,
		1.0f,  1.0f,
		-1.0f,  1.0f,
		-1.0f, -1.0f
	};

	// Screen quad texture coordinates
	private static final float SCREEN_QUAD_TEX_COORDINATES[] = {
		0.0f, 0.0f,
		1.0f, 0.0f,
		1.0f, 1.0f,
		1.0f, 1.0f,
		0.0f, 1.0f,
		0.0f, 0.0f
	};
	
	private Matrix4f mFrustrumCorner;
	private FloatBuffer mFloatBuffer;
	private int mVaoId;
	
	public ScreenQuad(float fovy, float farZ, float aspect) {
		init(fovy, farZ, aspect);
		
		// Create a new Vertex Array Object in memory and select it (bind)
		mVaoId = GL30.glGenVertexArrays();		
		GL30.glBindVertexArray(mVaoId);

		GL20.glEnableVertexAttribArray(0); // position
		GL20.glEnableVertexAttribArray(1); // texture coordinates
		GL20.glEnableVertexAttribArray(2); // frustum corner index

		// Create VBO for coordinates
		int posVboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, posVboId);
		FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(12);
		dataBuffer.put(SCREEN_QUAD_COORDINATES);
		dataBuffer.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, dataBuffer, GL15.GL_STATIC_DRAW);
		
		// Put the position coordinates in attribute list 0
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);

		// Create VBO for texture coordinates
		int texVboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texVboId);
		dataBuffer = BufferUtils.createFloatBuffer(12);
		dataBuffer.put(SCREEN_QUAD_TEX_COORDINATES);
		dataBuffer.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, dataBuffer, GL15.GL_STATIC_DRAW);
		
		// Put the texture coordinates in attribute list 1
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
		
		// Create VBO for frustum corner index 
		int iFrustumVboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, iFrustumVboId);
		IntBuffer indBuffer = BufferUtils.createIntBuffer(6);
		indBuffer.put(new int[] { 0, 3, 2, 2, 1, 0 } );
		indBuffer.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, indBuffer, GL15.GL_STATIC_DRAW);
		
		// Put the frustum corner index in attribute list 2
		GL20.glVertexAttribPointer(2, 1, GL11.GL_INT, false, 0, 0);
		
		// Unbind
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	public void setUniforms() {
		int iFrustum = ShaderController.getUniformFromName("gFrustumCorners");
		if(iFrustum != -1)
			GL20.glUniformMatrix4(iFrustum, false, mFloatBuffer);
	}
	
	public int getVAOId() {
		return mVaoId;
	}
	
	private void init(float fovy, float farZ, float aspect) {
		mFloatBuffer = BufferUtils.createFloatBuffer(16);
		
		float halfHeight = farZ * (float)Math.tan(0.5f * fovy);
		float halfWidth = aspect * halfHeight;
		
		// Encode the frustum corners in a Matrix4f
		Matrix4f mFrustrumCorner = new Matrix4f();
		mFrustrumCorner.m00 = -halfWidth;
		mFrustrumCorner.m01 = -halfHeight;
		mFrustrumCorner.m02 = farZ;
		mFrustrumCorner.m03 = 0;
		
		mFrustrumCorner.m10 = -halfWidth;
		mFrustrumCorner.m11 = halfHeight;
		mFrustrumCorner.m12 = farZ;
		mFrustrumCorner.m13 = 0;
		
		mFrustrumCorner.m20 = halfWidth;
		mFrustrumCorner.m21 = halfHeight;
		mFrustrumCorner.m22 = farZ;
		mFrustrumCorner.m23 = 0;

		mFrustrumCorner.m30 = halfWidth;
		mFrustrumCorner.m31 = -halfHeight;
		mFrustrumCorner.m32 = farZ;
		mFrustrumCorner.m33 = 0;

		// mFrustrumCorner = (Matrix4f) mFrustrumCorner.transpose();
		mFrustrumCorner.store(mFloatBuffer);
		mFloatBuffer.flip();
	}

}
