package renderer;

import java.util.List;

/**
 * Represents a face in a model.
 * @author Max
 * @author Adi
 */
public class Face {
	public List<VertexData> faceData;
	
	public Face() {}
	
	/**
	 * Constructs a face given vertex data.
	 * @param faceData List of vertex data.
	 */
	public Face(List<VertexData> faceData) {
		this.faceData = faceData;
	}
	
	/**
	 * Adds a vertex to this face.
	 * @param vertex The VertexData describing the vertex to add.
	 */
	public void add (VertexData vertex) {
		faceData.add(vertex);
	}
	
	/**
	 * Returns a String representation of this face.
	 * @return the string representation
	 */
	public String toString() { 
		String result = "\nVertices: " + faceData.size() + "\n-----------\n";
		for(VertexData f : faceData) {
			result += f.toString() + "\n";
		}
		return result;	
	}	
}