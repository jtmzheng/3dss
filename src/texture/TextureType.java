package texture;

/**
 * Enum for the types of textures which can be added in the texture manager.
 * Each texture type corresponds to a filename which lives in res/textures/.
 * 
 * IMPORTANT: ALL TEXTURES FILES USED IN THE GAME SHOULD BE DECLARED HERE, AS AN ENUM. If not, then it will
 * fall back to the default texture. We do this so that when we ask the TextureManager to
 * add a texture, we avoid the ugliness of adding textures by filename, and we can use this enum instead.
 * 
 * @author Adi
 */
public enum TextureType {
	BUNNY_FUR ("fur_hair.png"),
	ENEMY_SKIN ("enemy_skin.png"),
	BOSS_SKIN ("boss_skin.png");
	
	private final String fileName;
	
	private TextureType(String s) {
		fileName = s;
	}
	
	public String fileName () {
		return fileName;
	}
	
	/**
	 * Reverse mapping from the filename string back to the original enum.
	 * This is needed when parsing .obj files to create Materials, as we need to turn
	 * the mtl filename in the .obj file to a TextureType.
	 * 
	 * @param fileName
	 * @return TextureType
	 */
	public static TextureType getTextureTypeFromFilename (String fileName) {
		if (fileName == null)
			throw new IllegalArgumentException("Please send in a filename that is not null.");
		
		for (TextureType t : values()) {
			if (t.fileName().equals(fileName)) {
				return t;
			}
		}
		
		System.err.println("Cannot find the TextureType. PLEASE CREATE AN ENUM FOR " + fileName + " IN TextureType.java.");
		System.err.println("FALLING BACK TO THE DEFAULT TEXTURE...");
		
		return getDefaultTexture();
	}
	
	// TODO: Find a better default texture.
	public static TextureType getDefaultTexture () {
		return BUNNY_FUR;
	}
}