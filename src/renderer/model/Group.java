package renderer.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of faces (corresponding to groups in object files).
 * 
 * @author Adi
 */
public class Group {
	public static final String DEFAULT_GROUP_NAME = "default_group";
	private List<Face> faces;
	private String groupName;

	/**
	 * Creates a group with an empty list of faces.
	 */
	public Group(String name) {
		faces = new ArrayList<Face>();
		groupName = name.equals("") ? DEFAULT_GROUP_NAME : name;
	}

	/**
	 * Creates a group given a list of faces.
	 * @param f
	 */
	public Group(String name, List<Face> f) {
		faces = new ArrayList<Face>();
		groupName = name.equals("") ? DEFAULT_GROUP_NAME : name;
	}
	
	/**
	 * Adds a face to the group.
	 */
	public void addFace(Face f) {
		faces.add(f);
	}
	
	/**
	 * Gets the list of faces belonging to the group.
	 */
	public List<Face> getFaces() {
		return faces;
	}

	/**
	 * Gets the group name.
	 */
	public String getName () {
		return groupName;
	}
}
