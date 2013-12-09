package texture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton to manage textures globally, abstracting away the file loading
 * and GL calls. Textures must be 2^n x 2^n in pixel size, and PNG is the only
 * file format currently supported.
 * 
 * @author Adi
 */
public class TextureManager {
	private static TextureManager instance = null;
	
	// This is a mapping of a texture type enums to texture objects.
	// Any texture living in this mapping has already been loaded and assigned
	// a unique ID by OpenGL.
	private Map<TextureType, Texture> textureMapping = new ConcurrentHashMap<TextureType, Texture>();
	
	private final Object TextureManagerLock = new Object();

	public static final String DEFAULT_TEXTURE_FILENAME = "fur_hair.png";
	
	/**
	 * UID of a texture that cannot be found.
	 */
	private static final int TEXTURE_NOT_FOUND_ID = -1;
	
	private TextureManager() {}
	
	/**
	 * Gets the instance of the texture manager.
	 * @return instance
	 */
	public static TextureManager getInstance () {
		if (instance == null) {
			instance = new TextureManager();
		} 
		
		return instance;
	}
	
	/**
	 * Adds a texture to the file manager. The texture should reside in
	 * res/textures and should contain no spaces or special characters in the textureType.
	 * The texture MUST be of size 2^n x 2^n, or else an exception will be thrown.
	 * @param textureType
	 */
	public void addTexture (TextureType textureType) {
		synchronized(TextureManagerLock) {
			if (textureMapping.containsKey(textureType)) return;
			
			Texture tex = TextureLoader.loadTexture(textureType.fileName());
			textureMapping.put(textureType, tex);
		}
	}
	
	/**
	 * Removes a texture from the manager and frees associated memory.
	 * @param textureType
	 */
	public void removeTexture (TextureType textureType) {
		synchronized(TextureManagerLock) {
			if (textureMapping.containsKey(textureType)) {
				Texture tex = textureMapping.get(textureType);
				tex.unbindAndDestroy();
				textureMapping.remove(textureType);
			}
		}
	}
	
	/**
	 * Gets the GL ID of a given texture.
	 * @param textureType
	 */
	public int getTextureID (TextureType textureType) {
		if (!textureMapping.containsKey(textureType)) {
			return TEXTURE_NOT_FOUND_ID;
		} else {
			return textureMapping.get(textureType).getID();
		}
	}
	
	/**
	 * Gets a texture with a given textureType.
	 * @param textureType
	 */
	public Texture getTexture (TextureType textureType) {
		if (!textureMapping.containsKey(textureType)) return null;
		return textureMapping.get(textureType);
	}
	
	/**
	 * Gets the number of textures in the manager.
	 */
	public int getNumTextures () {
		return textureMapping.size();
	}
}