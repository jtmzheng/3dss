package texture;

/**
 * Wrapper class for a material to apply to a face, from .mtl files.
 * Note: The name of a material is the filename of the texture which is loaded
 * by the TextureLoader.
 * 
 * @author Adi
 */
public class Material {
	// Default material values when no material is currently selected.
	public float[] Kd = new float[] {1.0f, 0.5f, 0f, 1.0f}; // diffuse
	public float[] Ks = new float[] {1f, 1f, 1f}; // specular
	public float[] Ka = new float[] {1f, 1f, 1f}; // ambient
	public float Ns = 95f; // specular coefficient
	
	// Should be the same as the filename of the texture referred to in the .mtl file.
	public String name = null;
	
	public Material (String name) {
		this.name = name;
		
		TextureManager.getInstance().addTexture(name);
	}
}