package texture;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
	 * Default texture (set on first instance of this class).
	 */
	public Texture defaultTexture;
	
	private static final Object TextureManagerLock = new Object();
	
	private TextureManager() {
		try {
			defaultTexture = TextureLoader.loadTexture(DEFAULT_TEXTURE_FILENAME);
		} catch (IOException e) {
			System.err.println("Could not find default texture file in res/textures/!");
			System.err.println("Please add the file " + DEFAULT_TEXTURE_FILENAME + " in that directory.");
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
					Texture loadedTexture = TextureLoader.loadTexture(textureFileName);
					return loadedTexture;
				} catch (IOException e) {
					System.err.println("Could not find " + textureFileName + " in res/textures/.");
					System.err.println("Falling back to the default texture map -> " + DEFAULT_TEXTURE_FILENAME);
					
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
}
