package debugger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import de.matthiasmann.twl.utils.PNGDecoder;

/**
 * Renders text using a bitmap image.
 * Uses the PNG decoder helper:
 * http://www.lwjgl.org/wiki/index.php?title=Loading_PNG_images_with_TWL's_PNGDecoder
 * 
 * Much of the code borrowed from TheCodingUniverse.
 * 
 * @author Adi
 */
public class DebugWindow {

	private static int fontTexture;

	public static void init() {
		try {
			setupTextures();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void setupTextures() throws FileNotFoundException, IOException {
		fontTexture = GL11.glGenTextures();

		// We're using a 2D texture.
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTexture);
		
		// Load the PNG file using the PNGDecoder helper.
		PNGDecoder fontDecoder = new PNGDecoder(new FileInputStream("res/fonts/courierNewFont.png"));
		ByteBuffer buff = BufferUtils.createByteBuffer(4 * fontDecoder.getWidth() * fontDecoder.getHeight());
		
		// Decode this PNG file.
		fontDecoder.decode(buff,  fontDecoder.getWidth()*4, PNGDecoder.Format.RGBA);
		buff.flip();
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, fontDecoder.getWidth(), fontDecoder.getHeight(), 
				0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buff);
		
		// Unbind the texture.
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public static void write(String s) {
		renderString(s, fontTexture, 16, -0.9f, 0, 0.3f, 0.225f);
	}
	
    /**
     * Renders text using a font bitmap.
     *
     * @param string the string to render
     * @param textureObject the texture object containing the font glyphs
     * @param gridSize the dimensions of the bitmap grid (e.g. 16 -> 16x16 grid; 8 -> 8x8 grid)
     * @param x the x-coordinate of the bottom-left corner of where the string starts rendering
     * @param y the y-coordinate of the bottom-left corner of where the string starts rendering
     * @param characterWidth the width of the character
     * @param characterHeight the height of the character
     */
    private static void renderString(String string, int textureObject, int gridSize, float x, float y,
                                     float characterWidth, float characterHeight) {
        GL11.glPushAttrib(GL11.GL_TEXTURE_BIT | GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureObject);
        // Enable linear texture filtering for smoothed results.
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        // Enable additive blending. This means that the colours will be added to already existing colours in the
        // frame buffer. In practice, this makes the black parts of the texture become invisible.
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
        
        // Store the current model-view matrix.
        GL11.glPushMatrix();
        // Offset all subsequent (at least up until 'glPopMatrix') vertex coordinates.
        GL11.glTranslatef(x, y, 0);
        GL11.glBegin(GL11.GL_QUADS);
        // Iterate over all the characters in the string.
        for (int i = 0; i < string.length(); i++) {
            // Get the ASCII-code of the character by type-casting to integer.
            int asciiCode = (int) string.charAt(i);
            // There are 16 cells in a texture, and a texture coordinate ranges from 0.0 to 1.0.
            final float cellSize = 1.0f / gridSize;
            // The cell's x-coordinate is the greatest integer smaller than remainder of the ASCII-code divided by the
            // amount of cells on the x-axis, times the cell size.
            float cellX = ((int) asciiCode % gridSize) * cellSize;
            // The cell's y-coordinate is the greatest integer smaller than the ASCII-code divided by the amount of
            // cells on the y-axis.
            float cellY = ((int) asciiCode / gridSize) * cellSize;
            System.out.println(cellX + " " + cellY);
            GL11.glTexCoord2f(cellX, cellY + cellSize);
            GL11.glVertex2f(i * characterWidth / 3, y);
            GL11.glTexCoord2f(cellX + cellSize, cellY + cellSize);
            GL11.glVertex2f(i * characterWidth / 3 + characterWidth / 2, y);
            GL11.glTexCoord2f(cellX + cellSize, cellY);
            GL11.glVertex2f(i * characterWidth / 3 + characterWidth / 2, y + characterHeight);
            GL11.glTexCoord2f(cellX, cellY);
            GL11.glVertex2f(i * characterWidth / 3, y + characterHeight);
        }
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }
}
