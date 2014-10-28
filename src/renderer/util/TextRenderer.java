package renderer.util;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import renderer.model.VertexData;
import renderer.shader.ShaderController;
import renderer.shader.ShaderProgram;
import texture.Texture;
import texture.TextureLoader;
import texture.TextureManager;

/**
 * Singleton that manages loading, adding, and removing texture maps.
 * @TODO This class severely needs cleaning up 
 * @author Adi
 * @author Max
 */
public class TextRenderer {	
	// List of textboxes the text renderer is responsible for drawing
	Set<TextBox> textAreas;

	private final Object TEXT_BOX_LIST_LOCK = new Object();
	
	// buffers for vertices and texture coordiantes
	int vboId;
	int uvId;

	Texture fontTex;
	
	// the shader this renderer uses
	ShaderProgram sp;

	public TextRenderer (String textureFileName, ShaderProgram sp) {
		textAreas = new HashSet<TextBox>();

		try {
			this.fontTex = TextureLoader.loadTexture2D(textureFileName);
			vboId = GL15.glGenBuffers();
			uvId = GL15.glGenBuffers();
			this.sp = sp;			
		} catch (IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}
	}

	public void render() {
		// mutually exclude text box list to ensure it isn't modified during rendering.
		synchronized(TEXT_BOX_LIST_LOCK) {
			for (TextBox t : this.textAreas) {
				this.printText(
				    t.text,
				    t.x,
				    t.y,
				    t.size
				);
			}
		}
	}
	
	public void removeTextBox (TextBox t) {
		synchronized(TEXT_BOX_LIST_LOCK) {
			this.textAreas.remove(t);
		}
	}

	public void addTextBox (TextBox t) {
		synchronized(TEXT_BOX_LIST_LOCK) {
			this.textAreas.add(t);
		}
	}

	private void printText (String text, int x, int y, int size) {
		int L = text.length();
 
		FloatBuffer verticesFloatBuffer = BufferUtils.createFloatBuffer(6 * 2 * L);
		FloatBuffer uvFloatBuffer = BufferUtils.createFloatBuffer(6 * 2 * L);
		int vIndex = 0, uIndex = 0;
		for (int i = 0; i < L; i++) {
			float[] glyph_ul = { x+i*size, y+size };
			float[] glyph_ur = { x+i*size+size, y+size };
			float[] glyph_dr = { x+i*size+size, y };
			float[] glyph_dl = { x+i*size, y };
			
			verticesFloatBuffer.put(vIndex++, glyph_ul[0]);
			verticesFloatBuffer.put(vIndex++, glyph_ul[1]);
			verticesFloatBuffer.put(vIndex++, glyph_dl[0]);
			verticesFloatBuffer.put(vIndex++, glyph_dl[1]);
			verticesFloatBuffer.put(vIndex++, glyph_ur[0]);
			verticesFloatBuffer.put(vIndex++, glyph_ur[1]);
			verticesFloatBuffer.put(vIndex++, glyph_dr[0]);
			verticesFloatBuffer.put(vIndex++, glyph_dr[1]);
			verticesFloatBuffer.put(vIndex++, glyph_ur[0]);
			verticesFloatBuffer.put(vIndex++, glyph_ur[1]);
			verticesFloatBuffer.put(vIndex++, glyph_dl[0]);
			verticesFloatBuffer.put(vIndex++, glyph_dl[1]);

			char c = text.charAt(i);
			
			// x coordinate of the character in the texture
			// for example, the character code for 'A' is 65.
			// 65%16 = 1 (column #1), 65/16 = 4 (row #4).
			// divide both by 16.0 to fit in the [0.0 - 1.0] range for OpenGL textures.
			float ux = (c % 16)/16.0f;
			float uy = (c/16)/16.0f;
			
			float[] uv_ul = { ux, 1.0f-uy };
			float[] uv_ur = { ux+1.0f/16.0f, 1.0f-uy };
			float[] uv_dr = { ux+1.0f/16.0f, 1.0f - (uy+1.0f/16.0f) };
			float[] uv_dl = { ux, 1.0f - (uy+1.0f/16.0f) };

			uvFloatBuffer.put(uIndex++, uv_ul[0]);
			uvFloatBuffer.put(uIndex++, uv_ul[1]);
			uvFloatBuffer.put(uIndex++, uv_dl[0]);
			uvFloatBuffer.put(uIndex++, uv_dl[1]);
			uvFloatBuffer.put(uIndex++, uv_ur[0]);
			uvFloatBuffer.put(uIndex++, uv_ur[1]);
			uvFloatBuffer.put(uIndex++, uv_dr[0]);
			uvFloatBuffer.put(uIndex++, uv_dr[1]);
			uvFloatBuffer.put(uIndex++, uv_ur[0]);
			uvFloatBuffer.put(uIndex++, uv_ur[1]);
			uvFloatBuffer.put(uIndex++, uv_dl[0]);
			uvFloatBuffer.put(uIndex++, uv_dl[1]);
		}
		verticesFloatBuffer.flip();
		uvFloatBuffer.flip();

		draw(verticesFloatBuffer, uvFloatBuffer, L);		
	}
	
	private void draw (FloatBuffer vp, FloatBuffer uv, int L) {
		ShaderController.setProgram(sp);
		GL20.glUseProgram(ShaderController.getCurrentProgram());

		TextureManager tm = TextureManager.getInstance();
		Integer unitId = tm.getTextureSlot();
		fontTex.bind(unitId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vp, GL15.GL_STATIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, uv, GL15.GL_STATIC_DRAW);

		// (x, y) positions of the characters
		GL20.glEnableVertexAttribArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);

		// texture coordinates
		GL20.glEnableVertexAttribArray(1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvId);
		GL20.glVertexAttribPointer(1, VertexData.textureElementCount, GL11.GL_FLOAT,
				false, VertexData.stride, VertexData.textureByteOffset);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, L * 6);
		
		GL13.glActiveTexture(0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		tm.returnTextureSlot(unitId);
	}

}