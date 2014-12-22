package renderer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderer.Renderable;
import renderer.light.Light;
import renderer.light.LightHandle;
import system.Settings;
import texture.Material;

/**
 * Super class for all kinds of models
 * Currently: ModelScene, Model
 * @author Max
 *
 */
public abstract class Model implements Renderable {
	// Defaults
	protected static final Vector3f DEFAULT_INITIAL_POSITION = new Vector3f(0, 0, 0);

	// Enable culling for this Model or not.
	protected boolean enableCulling = true;
	
	// Map of VBOs and indices for each material in the Model
	protected Map<Material, Integer> mapVBOIndexIds;
	protected Map<Material, Integer> mapIndiceCount;

	// Vertex Array Objects
	protected Map<Material, Integer> mapVAOIds;

	// The Model matrix assosciated with this Model.
	protected Matrix4f modelMatrix;

	// Faces that make up this Model.
	protected List<Face> faces;
	protected Map<Material, List<Face>> mapMaterialToFaces;
	
	// LightHandle of the Model
	protected LightHandle mLightHandle = null;
	
	// Flag for whether this Model should be rendered
	protected boolean renderFlag;	
	
	// Initial position of the Model.
	protected Vector3f initialPos;

	// Bounding box for the Model
	protected BoundingBox boundBox;

	// Instance of the shared settings object.
	protected Settings settings = Settings.getInstance();
	
	// If the Model has been bound yet.
	protected boolean isBound = false;
	
	public Model(List<Face> f, Vector3f pos, Vector3f ld, Vector3f ls, Vector3f la) {
		this.faces = f;
		this.initialPos = pos;
		
		// Setup the light associated with this Model
		this.mLightHandle = new LightHandle(this, new Light(pos, ls, ld, la, null));	
		setup();
	}
	
	public Model(List<Face> f, Vector3f pos) {
		this.faces = f;
		this.initialPos = pos;
		setup();
	}
	
	public Model(List<Face> f) {
		this.faces = f;
		this.initialPos = DEFAULT_INITIAL_POSITION;
		setup();
	}
	
	public Model(Model model, Vector3f pos) {
		// Copy the ModelInt faces
		List<Face> faceList = new ArrayList<>();
		for (Face face : model.faces) {
			faceList.add(new Face(face));
		}

		// Set member variables
		this.faces = faceList;
		this.initialPos = pos;
		
		setup();
	}
	
	/**
	 * Returns the list of faces that make up this ModelInt.
	 * @return the list of faces
	 */
	public List<Face> getFaceList () {
		return faces;
	}
	
	/**
	 * Setup the Model
	 */
	private void setup() {
		isBound = false;
		
		// Strip any quads / polygons. 
		triangulate();
	}
	
	/**
	 * Remove the non-triangle faces from the Model (triangulates quads)
	 * @param List to remove non-triangles from
	 */
	private void triangulate() {
		List<Face> removeFaces = new ArrayList<Face>();
		List<Face> addFaces = new ArrayList<Face>();
		for (Face face : this.faces) {
			if (face.faceData.size() == 4) {
				removeFaces.add(face);
				addFaces.add(new Face(face.getVertex(0) , face.getVertex(1) , face.getVertex(2), face.getMaterial()));
				addFaces.add(new Face(face.getVertex(0) , face.getVertex(2) , face.getVertex(3), face.getMaterial()));
			} else if (face.faceData.size() > 4){
				removeFaces.add(face); //TODO(MZ): Currently just culls any face > 4 vertices
			}
		}

		this.faces.removeAll(removeFaces);
		this.faces.addAll(addFaces); 
	}
}
