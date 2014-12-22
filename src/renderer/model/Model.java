package renderer.model;

import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderer.Renderable;
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
}
