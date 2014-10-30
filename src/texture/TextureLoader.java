package texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import util.MathUtils;

/**
 * Utility class to load textures. This should (other than unit tests) only be 
 * used in the texture manager.
 * 
 * @author Adi
 * @author Max
 */
public class TextureLoader {	
	
	/**
	 * Generate a random 2D texture
	 * 
	 * @param width
	 * @param height
	 * @param name
	 * @param bpp
	 * @return
	 */
	public static Texture loadRandomTexture2D(int width, int height, String name, int bpp) {
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
        try {
			TextureLoader.fillBufferRandom(buffer, width, height, bpp);
		} catch(ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
        
        Texture tex = new Texture2D(width, 
        		height, 
        		buffer,
        		name,
        		bpp == 4);
        
        System.out.println("Random Texture: " + tex.getID());
        return tex;
	}
	
	/**
	 * Loads a texture given an image filename.
	 * This file must reside in res/textures/
	 * 
	 * @param fileName
	 * @param slotId
	 * @return Texture tex (a bound texture)
	 */
	public static Texture loadTexture2D(String filename) throws IOException, IllegalArgumentException {
		BufferedImage image = null;
		image = loadImage(filename);
		
		int bpp = 4;		
		if (!image.getColorModel().hasAlpha())
			bpp = 3;
		
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * bpp);
        try {
			fillBuffer(buffer, image.getHeight(), image.getWidth(), bpp, pixels);
		} catch(ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
        
        Texture tex = new Texture2D(image.getWidth(), 
        		image.getHeight(), 
        		buffer,
        		filename,
        		image.getColorModel().hasAlpha());

        return tex;
	}

	public static Texture loadCubeMapTexture(List<String> files, String name) throws IOException, IllegalArgumentException {
		if(files.size() != 6) {
			throw new IllegalArgumentException();
		}
		
		BufferedImage image = null;
		List<ByteBuffer> imgBuffers = new ArrayList<>();
		
		for(String filename : files) {
			image = loadImage(filename);

			int bpp = 4;		
			if (!image.getColorModel().hasAlpha())
				bpp = 3;

			int[] pixels = new int[image.getWidth() * image.getHeight()];
			image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
			ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * bpp);
			try {
				fillBuffer(buffer, image.getHeight(), image.getWidth(), bpp, pixels);
			} catch(ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				throw new IllegalArgumentException();
			}
			imgBuffers.add(buffer);
		}
		
		return new CubeTexture(image.getWidth(), 
				image.getHeight(), 
				imgBuffers,
				name,
				image.getColorModel().hasAlpha());
	}

	private static void fillBuffer(ByteBuffer buffer, int width, int height, int bpp, int [] pixels) throws ArrayIndexOutOfBoundsException {
		// Iterates through the image and adds each pixel to the buffer.
		for (int y = 0; y < height; y++){
			for (int x = 0; x < width; x++){
				int pixel = pixels[y * width + x];

				buffer.put((byte) ((pixel >> 16) & 0xFF));  // Red component.
				buffer.put((byte) ((pixel >> 8) & 0xFF));   // Green component.
				buffer.put((byte) (pixel & 0xFF));          // Blue component

				if (bpp == 4)
					buffer.put((byte) ((pixel >> 24) & 0xFF));  // Alpha component, if it has one.

			}
		}
		
		buffer.flip();
	}
	
	private static void fillBufferRandom(ByteBuffer buffer, int width, int height, int bpp) {
		// Iterates through the image and adds each pixel to the buffer.
		for (int y = 0; y < height; y++){
			for (int x = 0; x < width; x++){
				buffer.put((byte) MathUtils.randInt(0, Byte.MAX_VALUE));  // Red component.
				buffer.put((byte) MathUtils.randInt(0, Byte.MAX_VALUE));   // Green component.
				buffer.put((byte) MathUtils.randInt(0, Byte.MAX_VALUE));          // Blue component

				if (bpp == 4)
					buffer.put((byte) MathUtils.randInt(0, Byte.MAX_VALUE));  // Alpha component, if it has one.
			}
		}

		buffer.flip();
	}
	
	private static BufferedImage loadImage (String textureName) throws IOException {
		// Get the absolute path to the texture.
		String abspath = System.getProperty("user.dir") + "/res/textures/" + textureName;
        BufferedImage im = ImageIO.read(new File(abspath));
        
        if ((im.getHeight() & im.getHeight() - 1) != 0 || (im.getWidth() & im.getWidth() - 1) != 0) {
        	throw new IllegalArgumentException("Invalid dimensions for texture. Please use width and heights of powers of two.");
        }
        
        return im;
    }
}