package renderer;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * ScreenQuad class
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
	
	private int vaoId;
	
	public ScreenQuad() {		
		// Create a new Vertex Array Object in memory and select it (bind)
		vaoId = GL30.glGenVertexArrays();		
		GL30.glBindVertexArray(vaoId);

		// Enable the VAO attributes
		GL20.glEnableVertexAttribArray(0); // position
		GL20.glEnableVertexAttribArray(1); // texture coordinates
		
		// Create VBO for coordinates
		int posVboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, posVboId);
		FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(12);
		dataBuffer.put(SCREEN_QUAD_COORDINATES);
		dataBuffer.flip();
		GL15.glBufferData (GL15.GL_ARRAY_BUFFER, dataBuffer, GL15.GL_STATIC_DRAW);

		// Create VBO for texture coordinates
		int texVboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texVboId);
		dataBuffer = BufferUtils.createFloatBuffer(12);
		dataBuffer.put(SCREEN_QUAD_TEX_COORDINATES);
		dataBuffer.flip();
		GL15.glBufferData (GL15.GL_ARRAY_BUFFER, dataBuffer, GL15.GL_STATIC_DRAW);

		// Put the position coordinates in attribute list 0
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT,
				false, 0, 0);

		// Put the texture coordinates in attribute list 1
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT,
				false, 0, 0);
		
		// Unbind
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	public int getVAOId() {
		return vaoId;
	}

}
