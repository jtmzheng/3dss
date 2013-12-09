package texture;

/**
 * Wrapper class for a material to apply to a face, from .mtl files.
 * 
 * @author Adi
 */
public class Material {
	// Default material values when no material is currently selected.
	public float[] Kd = new float[] {1.0f, 0.5f, 0f, 1.0f}; // diffuse
	public float[] Ks = new float[] {1f, 1f, 1f}; // specular
	public float[] Ka = new float[] {1f, 1f, 1f}; // ambient
	public float Ns = 95f; // specular coefficient
	
	// The texture applied to this material.
	public TextureType textureType;

	/**
	 * Creates a material given a filename.
	 * @param fileName
	 */
	public Material (String fileName) {
		if (fileName == null) fileName = TextureManager.DEFAULT_TEXTURE_FILENAME;
		this.textureType = TextureType.getTextureTypeFromFilename(fileName);
		
		//TextureManager.getInstance().addTexture(this.textureType);
	}
}