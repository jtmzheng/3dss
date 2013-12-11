package texture;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.lwjgl.opengl.GL13;

/**
 * Singleton that manages loading, adding, and removing texture maps.
 * 
 * @author Adi
 */
public class TextureManager {
	private static TextureManager instance = null;
	
	/**
	 *  Holds a mapping of texture file names to texture objects.
	 */
	Map<String, Texture> textureFileMapping = new HashMap<String, Texture>();
	
	/**
	 * GL texture ID for a texture not found.
	 */
	public static final int TEXTURE_NOT_FOUND_ID = -1;
	
	/**
	 * Default texture map to apply to a material when there is none specified.
	 */
	public static final String DEFAULT_TEXTURE_FILENAME = "fur_hair.png";
	
	/**
	 * OpenGL Texture Slot IDs
	 */
	private BlockingQueue<Integer> texSlotIds;
	
	/**
	 * LWJGL only supports 32 slots 
	 */
	private final int MAX_SUPPORTED_SLOTS = 50; 
	
	/**
	 * Default texture (set on first instance of this class).
	 */
	private Texture defaultTexture;
	
	private static final Object TextureManagerLock = new Object();
	
	private TextureManager() {
		try {
			texSlotIds = new ArrayBlockingQueue<Integer>(MAX_SUPPORTED_SLOTS);
			
			// Add all supported LWJGL texture slot Ids to list of available slot IDs
			for(int i = GL13.GL_TEXTURE0; i < GL13.GL_TEXTURE31; i++) {
				texSlotIds.add(i);
			}
			
			// Load the default texture
			defaultTexture = TextureLoader.loadTexture(DEFAULT_TEXTURE_FILENAME, texSlotIds.poll(2000, TimeUnit.MILLISECONDS));
			
		} catch (IOException e) {
			System.err.println("Could not find default texture file in res/textures/!");
			System.err.println("Please add the file " + DEFAULT_TEXTURE_FILENAME + " in that directory.");
			System.exit(1);
		} catch (InterruptedException e) {
			System.exit(1);
		}
		textureFileMapping.put(DEFAULT_TEXTURE_FILENAME, defaultTexture);
	}
	
	/**
	 * Gets the instance of this singleton. Lazily instantiates if not already created.
	 * @return
	 */
	public static TextureManager getInstance () {
		synchronized(TextureManagerLock) {
			if (instance == null) {
				instance = new TextureManager();
			}
			
			return instance;
		}
	}
	
	/**
	 * Gets or creates a texture.
	 * @param textureFileName
	 * @return
	 */
	public Texture getOrCreateTexture (String textureFileName) {
		synchronized (TextureManagerLock) {
			if (textureFileMapping.containsKey(textureFileName)) {
				return textureFileMapping.get(textureFileName);
			} else {
				try {
					Texture loadedTexture = TextureLoader.loadTexture(textureFileName, texSlotIds.poll(2000, TimeUnit.MILLISECONDS));
					textureFileMapping.put(textureFileName, loadedTexture);
					return loadedTexture;
				} catch (IOException e) {
					System.err.println("Could not find " + textureFileName + " in res/textures/.");
					System.err.println("Falling back to the default texture map -> " + DEFAULT_TEXTURE_FILENAME);
					return defaultTexture;
				} catch (InterruptedException e) {
					System.err.println("Timed out waiting for texture slot!");					
					return defaultTexture;
				}
			}
		}
	}
	
	/**
	 * Removes a texture from the mapping.
	 * @param textureFileName
	 */
	public void removeTexture (String textureFileName) {
		synchronized (TextureManagerLock) {
			if (textureFileMapping.containsKey(textureFileName)) {
				textureFileMapping.get(textureFileName).unbindAndDestroy();
				textureFileMapping.remove(textureFileName);
			}
		}
	}
	
	/**
	 * Gets the number of loaded textures (including the default texture).
	 * @return
	 */
	public int numLoadedTextures () {
		synchronized (TextureManagerLock) {
			return textureFileMapping.size();
		}
	}
	
	/**
	 * Gets the GL ID of a texture by filename.
	 * Returns TEXTURE_NOT_FOUND_ID if not found in the mapping.
	 * @param fileName
	 * @return
	 */
	public int getTextureIdByFilename (String fileName) {
		synchronized (TextureManagerLock) {
			if (!textureFileMapping.containsKey(fileName)) {
				return TEXTURE_NOT_FOUND_ID;
			} else {
				return textureFileMapping.get(fileName).getID();
			}
		}
	}
	
	public Texture getDefaultTexture() {
		return defaultTexture;
	}
}
