package renderer.util;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import renderer.shader.ShaderController;
import renderer.shader.ShaderProgram;
import texture.Texture;
import texture.Texture2D;
import texture.TextureLoader;
import texture.TextureManager;

/**
 * Class responsible to render TextBoxes on the screen.
 * Retrieves the current TextBoxes from the TextManager.
 * @author Adi
 */
public class TextRenderer {
	// Buffers for vertices and texture coordinates.
	int unitId;

	Texture fontTex;
	
	// the shader this renderer uses
	ShaderProgram sp;

	public TextRenderer (String textureFileName, ShaderProgram sp) {
		this.sp = sp;

		try {
			this.fontTex = (Texture2D) TextureLoader.loadTexture2D(textureFileName);
			TextureManager tm = TextureManager.getInstance();
			unitId = tm.getTextureSlot();
			fontTex.bind(unitId);
		} catch (IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}
	}

	public void render() {
		for (TextBox t : TextManager.getInstance().getTextBoxes()) {
			this.printText(
			    t.text,
			    t.x,
			    t.y,
			    t.size
			);
		}
	}

	private void printText (String text, int x, int y, int size) {
		int L = text.length();
 
		float[] vertices = new float[2 * 6 * L];
		float[] uv_coords = new float[2 * 6 * L];
		int vIndex = 0, uIndex = 0;
		for (int i = 0; i < L; i++) {
			float[] glyph_ul = { x+i*size, y+size };
			float[] glyph_ur = { x+i*size+size, y+size };
			float[] glyph_dr = { x+i*size+size, y };
			float[] glyph_dl = { x+i*size, y };

			// 6 vertices (two triangles) to make up the rectangle where the glyph is placed.
			vertices[vIndex++] = glyph_ul[0];
			vertices[vIndex++] = glyph_ul[1];
			vertices[vIndex++] = glyph_dl[0];
			vertices[vIndex++] = glyph_dl[1];
			vertices[vIndex++] = glyph_ur[0];
			vertices[vIndex++] = glyph_ur[1];
			vertices[vIndex++] = glyph_dr[0];
			vertices[vIndex++] = glyph_dr[1];
			vertices[vIndex++] = glyph_ur[0];
			vertices[vIndex++] = glyph_ur[1];
			vertices[vIndex++] = glyph_dl[0];
			vertices[vIndex++] = glyph_dl[1];

			char c = text.charAt(i);
			
			// x coordinate of the character in the texture
			// for example, the character code for 'A' is 65.
			// 65%16 = 1 (column #1), 65/16 = 4 (row #4).
			// divide both by 16.0 to fit in the [0.0 - 1.0] range for OpenGL textures.
			float ux = (c % 16)/16.0f;
			float uy = (c / 16)/16.0f;

			// Get the character from the texture. 0.85f is used instead of 1f because
			// the characters are left-aligned on the glyphs (open up consolas.png).
			float[] uv_ul = { ux, uy };
			float[] uv_ur = { ux+0.85f/16.0f, uy };
			float[] uv_dr = { ux+0.85f/16.0f, (uy+1.0f/16.0f) };
			float[] uv_dl = { ux, (uy+1.0f/16.0f) };

			uv_coords[uIndex++] = uv_ul[0];
			uv_coords[uIndex++] = uv_ul[1];
			uv_coords[uIndex++] = uv_dl[0];
			uv_coords[uIndex++] = uv_dl[1];
			uv_coords[uIndex++] = uv_ur[0];
			uv_coords[uIndex++] = uv_ur[1];
			uv_coords[uIndex++] = uv_dr[0];
			uv_coords[uIndex++] = uv_dr[1];
			uv_coords[uIndex++] = uv_ur[0];
			uv_coords[uIndex++] = uv_ur[1];
			uv_coords[uIndex++] = uv_dl[0];
			uv_coords[uIndex++] = uv_dl[1];
		}

		FloatBuffer verticesFloatBuffer = BufferUtils.createFloatBuffer(6*2*L).put(vertices);
		FloatBuffer uvFloatBuffer = BufferUtils.createFloatBuffer(6*2*L).put(uv_coords);

		verticesFloatBuffer.flip();
		uvFloatBuffer.flip();

		draw(verticesFloatBuffer, uvFloatBuffer, L);		
	}
	
	private void draw (FloatBuffer vp, FloatBuffer uv, int L) {
		int vaoId = GL30.glGenVertexArrays();

		// Activate the font texture.
		GL13.glActiveTexture(unitId);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTex.getID());
		GL20.glUniform1i(ShaderController.getTexSamplerLocation(), unitId - GL13.GL_TEXTURE0);

		// Send in width and height for appropriate scaling.
		GL20.glUniform2f(ShaderController.getDimensions(), Display.getWidth(), Display.getHeight());

		GL30.glBindVertexArray(vaoId);

		// Bind vertices and texture coordinates.
		int vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vp, GL15.GL_STATIC_DRAW);

		int uvId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, uv, GL15.GL_STATIC_DRAW);

		GL20.glEnableVertexAttribArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);

		GL20.glEnableVertexAttribArray(1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvId);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);

		// Enable transparency to blend out the alpha channel of the PNG.
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		// Draw it out.
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6*L);

		GL11.glDisable(GL11.GL_BLEND);

		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
}