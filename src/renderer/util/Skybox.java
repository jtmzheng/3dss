package renderer.util;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import renderer.shader.ShaderController;
import texture.Texture;
import texture.TextureManager;

/**
 * The Skybox class uses a cube map texture for a Skybox
 * @author Max
 *
 */
public class Skybox {

	public Skybox(Texture texture) {
		this.texture = texture;
		this.vaoId = GL30.glGenVertexArrays();
		
		TextureManager tm = TextureManager.getInstance();
		Integer unitId = tm.getTextureSlot();
		texture.bind(unitId);
		tm.returnTextureSlot(unitId);
		
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(0); // position
		
		int posVboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, posVboId);
		FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(POINTS.length);
		dataBuffer.put(POINTS);
		dataBuffer.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, dataBuffer, GL15.GL_STATIC_DRAW);

		// Put the position coordinates in attribute list 0
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	public void render() {
		TextureManager tm = TextureManager.getInstance();
		Integer unitId = tm.getTextureSlot();
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL11.glDepthMask(false);
		GL13.glActiveTexture(unitId);
		GL20.glUniform1i(ShaderController.getCubeTextureLocation(), unitId - GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture.getID());
		GL30.glBindVertexArray(vaoId);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36);

		// note: calling glActiveTexture(0) results in an 'invalid enum' opengl error.
		// i don't think it is required, either.

		GL11.glDepthMask(true);
		GL30.glBindVertexArray(0);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		tm.returnTextureSlot(unitId);
	}

	private int vaoId;
	private Texture texture;
	private static final float [] POINTS = {
			-10.0f,  10.0f, -10.0f,
			-10.0f, -10.0f, -10.0f,
			10.0f, -10.0f, -10.0f,
			10.0f, -10.0f, -10.0f,
			10.0f,  10.0f, -10.0f,
			-10.0f,  10.0f, -10.0f,

			-10.0f, -10.0f,  10.0f,
			-10.0f, -10.0f, -10.0f,
			-10.0f,  10.0f, -10.0f,
			-10.0f,  10.0f, -10.0f,
			-10.0f,  10.0f,  10.0f,
			-10.0f, -10.0f,  10.0f,

			10.0f, -10.0f, -10.0f,
			10.0f, -10.0f,  10.0f,
			10.0f,  10.0f,  10.0f,
			10.0f,  10.0f,  10.0f,
			10.0f,  10.0f, -10.0f,
			10.0f, -10.0f, -10.0f,

			-10.0f, -10.0f,  10.0f,
			-10.0f,  10.0f,  10.0f,
			10.0f,  10.0f,  10.0f,
			10.0f,  10.0f,  10.0f,
			10.0f, -10.0f,  10.0f,
			-10.0f, -10.0f,  10.0f,

			-10.0f,  10.0f, -10.0f,
			10.0f,  10.0f, -10.0f,
			10.0f,  10.0f,  10.0f,
			10.0f,  10.0f,  10.0f,
			-10.0f,  10.0f,  10.0f,
			-10.0f,  10.0f, -10.0f,

			-10.0f, -10.0f, -10.0f,
			-10.0f, -10.0f,  10.0f,
			10.0f, -10.0f, -10.0f,
			10.0f, -10.0f, -10.0f,
			-10.0f, -10.0f,  10.0f,
			10.0f, -10.0f,  10.0f
	};

}
