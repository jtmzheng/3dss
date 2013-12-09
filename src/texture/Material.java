package texture;

/**
 * Class for a material to apply to a face. This is a wrapper for the
 * "newmtl" tags in .mtl files. 
 * 
 * @author Adi
 */
public class Material {
	// Default texture map filename.
	public static String DEFAULT_TEXTURE_MAP_FILE = "fur_hair.png";
	
	// Default material values when no material is currently selected.
	public float[] Kd = new float[] {1.0f, 0.5f, 0f, 1.0f}; // diffuse
	public float[] Ks = new float[] {1f, 1f, 1f}; // specular
	public float[] Ka = new float[] {1f, 1f, 1f}; // ambient
	public float Ns = 95f; // specular coefficient
    public int illumModel = 0;
    public double niOpticalDensity = 0.0;
    public String mapKaFilename = null;
    public String mapKdFilename = null;
    public String mapKsFilename = null;

    public String name;
    
	/**
	 * Creates a material given a filename.
	 * @param fileName
	 */
	public Material (String materialName) {
		this.name = materialName;
		//if (fileName == null) fileName = TextureManager.DEFAULT_TEXTURE_FILENAME;
		
		//textureFileName = fileName;
		//TextureManager.getInstance().addTexture(this.textureFileName);
	}

	public String getName () {
		return name;
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
			   "mapKaFilename: " + mapKaFilename + "\n" +
			   "mapKdFilename: " + mapKdFilename + "\n" +
			   "mapKsFilename: " + mapKsFilename + "\n";
			   
	}
}