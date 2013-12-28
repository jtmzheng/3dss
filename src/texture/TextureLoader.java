package texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import system.Settings;

/**
 * Utility class to load textures. This should (other than unit tests) only be 
 * used in the texture manager.
 * 
 * @author Adi
 */
public class TextureLoader {
	/**
	 * Number of bytes per pixel.
	 * Three bytes for RGB, 4 for RGBA.
	 */
	private static int BYTES_PER_PIXEL = 4;
	
	/**
	 * Loads a texture given an image filename.
	 * This file must reside in res/textures/
	 * 
	 * @param fileName
	 * @param slotId
	 * @return Texture tex (a bound texture)
	 */
	public static Texture loadTexture(String fileName, int slotId) throws IOException{
		BufferedImage image = null;
		image = loadImage(fileName);
		
		if (!image.getColorModel().hasAlpha())
			BYTES_PER_PIXEL = 3;
		else 
			BYTES_PER_PIXEL = 4;
		
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL);
       
        // Iterates through the image and adds each pixel to the buffer.
        for (int y = 0; y < image.getHeight(); y++){
            for (int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                
                buffer.put((byte) ((pixel >> 16) & 0xFF));  // Red component.
                buffer.put((byte) ((pixel >> 8) & 0xFF));   // Green component.
                buffer.put((byte) (pixel & 0xFF));          // Blue component
                
                if (BYTES_PER_PIXEL == 4)
                	buffer.put((byte) ((pixel >> 24) & 0xFF));  // Alpha component, if it has one.
                
            }
        }

        buffer.flip();
        Texture tex = new Texture(image.getWidth(), 
        		image.getHeight(), 
        		buffer,
        		fileName,
        		image.getColorModel().hasAlpha());

        return tex;
	}

	private static BufferedImage loadImage (String textureName) throws IOException {
		// Get the absolute path to the texture.
		String abspath = Settings.getString("pwd") + "/res/textures/" + textureName;
        BufferedImage im = ImageIO.read(new File(abspath));
        
        if ((im.getHeight() & im.getHeight() - 1) != 0 || (im.getWidth() & im.getWidth() - 1) != 0) {
        	throw new IllegalArgumentException("Invalid dimensions for texture. Please use width and heights of powers of two.");
        }
        
        return im;
    }
}