package texture;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton to manage textures globally, abstracting away the file loading
 * and GL calls. Textures must be 2^n x 2^n in pixel size, and PNG is the only
 * file format currently supported.
 * 
 * TODO: Make this thread-safe.
 * @author Adi
 *
 */
public class TextureManager {
	private static TextureManager instance = null;
	
	// Mapping of texture file names to textures. We refer to textures as any file
	// that lives in res/textures, and the string key in the mapping is the name of that file.
	// so "foo.png" would be a correct string name.
	private Map<String, Texture> textureFileMapping = new HashMap<String, Texture>();
	
	public static final int TEXTURE_NOT_FOUND = -1;
	
	private TextureManager(){}
	
	public static TextureManager getInstance () {
		if (instance == null) {
			instance = new TextureManager();
		} 
		
		return instance;
	}
	/**
	 * Adds a texture to the file manager. The texture should reside in
	 * res/textures and should contain no spaces or special characters in the textureName.
	 * The texture MUST be of size 2^n x 2^n, or else an exception will be thrown.
	 * @param textureName
	 */
	public void addTexture (String textureName) {
		if (textureFileMapping.containsKey(textureName)) return;
		
		Texture tex = TextureLoader.loadTexture(textureName);
		textureFileMapping.put(textureName, tex);
	}
	
	/**
	 * Removes a texture from the manager.
	 * @param textureName
	 */
	public void removeTexture (String textureName) {
		if (textureFileMapping.containsKey(textureName)) {
			textureFileMapping.remove(textureName);
		}
	}
	
	/**
	 * Gets the GL ID of a given texture.
	 * @param textureName
	 */
	public int getTextureID (String textureName) {
		if (!textureFileMapping.containsKey(textureName)) {
			return TEXTURE_NOT_FOUND;
		} else {
			return textureFileMapping.get(textureName).getID();
		}
	}
	
	/**
	 * Gets a texture with a given textureName.
	 * @param textureName
	 */
	public Texture getTexture (String textureName) {
		if (!textureFileMapping.containsKey(textureName)) return null;
		return textureFileMapping.get(textureName);
	}
	
	/**
	 * Gets the number of textures in the manager.
	 */
	public int getNumTextures () {
		return textureFileMapping.size();
	}
}
