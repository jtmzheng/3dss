package texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import system.Settings;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

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
	 * Loads a texture given a image location
	 * @param image
	 * @return Texture tex
	 */
	public static Texture loadTexture(String imageLoc){
		BufferedImage image = loadImage(imageLoc);
		
		if (image.getColorModel().hasAlpha())
			BYTES_PER_PIXEL = 3;
		
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

        int textureID = glGenTextures(); // Generate texture ID.
        
        Texture tex = new Texture(imageLoc, image.getWidth(), image.getHeight(), textureID);
        
        // Bind texture ID to target.
        tex.bind();
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

        // Send texel data to OpenGL.
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        return tex;
	}

	private static BufferedImage loadImage(String textureName) {
		// Get the absolute path to the texture.
		String abspath = Settings.getString("pwd") + "/res/textures/" + textureName;
	    try {
	        BufferedImage im = ImageIO.read(new File(abspath));
	        
	        if ((im.getHeight() & im.getHeight() - 1) != 0 || (im.getWidth() & im.getWidth() - 1) != 0) {
	        	throw new IOException("Invalid dimensions for texture. Please use width and heights of powers of two.");
	        }
	        
	        return im;
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
        return null;
	}
}