package texture;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for a material to apply to a face. This is a wrapper for the
 * "newmtl" tags in .mtl files. 
 * 
 * @author Adi
 */
public class Material {	
	// Default material values when no material is currently selected.
	public float[] Kd = new float[] {0.8f, 0.8f, 0.8f, 1.0f}; // diffuse
	public float[] Ks = new float[] {1f, 1f, 1f}; // specular
	public float[] Ka = new float[] {0.2f, 0.2f, 0.2f}; // ambient
	public float Ns = 0f; // specular coefficient
    public int illumModel = 1;
    public double niOpticalDensity = 0.0;

    public Texture mapKaTexture = null;
    public Texture mapKdTexture = null;
    public Texture mapKsTexture = null;
    
    public String name;
    
	/**
	 * Creates a material given a filename.
	 * @param fileName
	 */
	public Material (String materialName) {
		this.name = materialName;
		
		System.out.println("New texture + " + materialName);
		mapKdTexture = TextureManager.getInstance().getDefaultTexture();
	}
	
	/**
	 * Creates the default material using the default Kd texture map.
	 */
	public Material() {
		// Very descriptive default material name to make sure that no .MTL files
		// accidentally use this name and cause issues.
		this.name = "defaultaraghavajtmzheng1234material";
		mapKdTexture = TextureManager.getInstance().getDefaultTexture();
	}

	public String getName () {
		return name;
	}
	
	public void setMapKaFile (String file) {
		mapKaTexture = TextureManager.getInstance().getOrCreateTexture(file);
	}
	
	public void setMapKdFile (String file) {
		mapKdTexture = TextureManager.getInstance().getOrCreateTexture(file);
	}
	
	public void setMapKsFile (String file) {
		mapKsTexture = TextureManager.getInstance().getOrCreateTexture(file);
	}
	
	@Override
	public String toString() {
		String Kd = "", Ka = "", Ks = "";
		for (int i = 0; i < 3; i++) {
			Kd += this.Kd[i] + ",";
			Ka += this.Ka[i] + ",";
			Ks += this.Ks[i] + ",";
		}
		
		return "NAME: " + name + "\n" +
			   "Kd: " + Kd + "\n" +
			   "Ks: " + Ks + "\n" +
			   "Ka: " + Ka + "\n" +
			   "Ns: " + Ns + "\n" +
			   "illumModel: " + illumModel + "\n" +
			   "niOpticalDensity: " + niOpticalDensity + "\n" +
			   "mapKa texture ID: " + mapKaTexture.getID() + "\n" +
			   "mapKd texture ID: " + mapKdTexture.getID() + "\n" +
			   "mapKs texture ID: " + mapKsTexture.getID() + "\n";
			   
	}
	
	/**
	 * Gets the texture Ids of all bound textures
	 * @return
	 */
	public List<Integer> getActiveTextureIds() {
		List<Integer> activeTextures = new ArrayList<Integer>();
		if(mapKaTexture != null) {
			if(mapKaTexture.isBound()) {
				activeTextures.add(mapKaTexture.getID());
			}
		}
		
		if(mapKdTexture != null) {
			if(mapKdTexture.isBound()) {
				activeTextures.add(mapKdTexture.getID());
			}
		}
		
		if(mapKsTexture != null) {
			if(mapKsTexture.isBound()) {
				activeTextures.add(mapKsTexture.getID());
			}
		}
		
		return activeTextures;
	}
}