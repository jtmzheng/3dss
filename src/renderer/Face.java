package renderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a face in a model.
 * @author Max
 * @author Adi
 */
public class Face {
	public List<VertexData> faceData;
	
	/*
	 * Private default constructor to prevent use
	 */
	private Face() {}
	
	/**
	 * Constructs a face given vertex data.
	 * @param faceData List of vertex data.
	 */
	public Face(List<VertexData> faceData) {
		this.faceData = faceData;
	}
	
	/**
	 * Constructs a triangular face from given VertexData
	 * @param a VertexData of first vertex
	 * @param b VertexData of second vertex
	 * @param c VertexData of third vertex
	 */
	public Face(VertexData a, VertexData b, VertexData c) {
		faceData = new ArrayList<VertexData>();
		faceData.add(a);
		faceData.add(b);
		faceData.add(c);
	}
	
	/**
	 * Adds a vertex to this face.
	 * @param vertex The VertexData describing the vertex to add.
	 */
	public void add (VertexData vertex) {
		faceData.add(vertex);
	}
	
	/**
	 * Get a vertex from the face
	 * @param index The index of the vertex to get
	 * @return the VertexData 
	 */
	
	public VertexData getVertex(int index){
		if( index > faceData.size() ){
			return null; //TODO: Add exception handling
		}
		
		return faceData.get(index);
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

