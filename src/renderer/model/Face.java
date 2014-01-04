package renderer.model;

import java.util.ArrayList;
import java.util.List;

import texture.Material;

/**
 * Represents a face in a model.
 *
 * @author Max
 * @author Adi
 */
public class Face {
	public List<VertexData> faceData;
	
	// Each face has a material, which has its own lighting properties.
	private Material material = null;
	
	/**
	 * Constructs a face given vertex data.
	 * @param faceData List of vertex data.
	 */
	public Face(List<VertexData> faceData, Material m) {
		for (VertexData v : faceData) {
			if(m.Ka != null) {
				v.setAmbient(m.Ka[0], m.Ka[1], m.Ka[2]);
			}
			if(m.Kd != null) {
				v.setDiffuse(m.Kd[0], m.Kd[1], m.Kd[2], m.Kd[3]);
			}
			if(m.Ks != null) {
				v.setSpecular(m.Ks[0], m.Ks[1], m.Ks[2]);
			}
			
			v.setSpecPower(m.Ns);
		}
		
		this.faceData = faceData;
		this.material = m;
	}
	
	/**
	 * Constructs a triangular face from given VertexData
	 * @param a VertexData of first vertex
	 * @param b VertexData of second vertex
	 * @param c VertexData of third vertex
	 */
	public Face(VertexData a, VertexData b, VertexData c, Material m) {
		faceData = new ArrayList<VertexData>();
		
		a.setAmbient(m.Ka[0], m.Ka[1], m.Ka[2]);
		a.setDiffuse(m.Kd[0], m.Kd[1], m.Kd[2], m.Kd[3]);
		a.setSpecular(m.Ks[0], m.Ks[1], m.Ks[2]);
		a.setSpecPower(m.Ns);
		faceData.add(a);
		
		b.setAmbient(m.Ka[0], m.Ka[1], m.Ka[2]);
		b.setDiffuse(m.Kd[0], m.Kd[1], m.Kd[2], m.Kd[3]);
		b.setSpecular(m.Ks[0], m.Ks[1], m.Ks[2]);
		b.setSpecPower(m.Ns);
		faceData.add(b);
		
		c.setAmbient(m.Ka[0], m.Ka[1], m.Ka[2]);
		c.setDiffuse(m.Kd[0], m.Kd[1], m.Kd[2], m.Kd[3]);
		c.setSpecular(m.Ks[0], m.Ks[1], m.Ks[2]);
		c.setSpecPower(m.Ns);
		faceData.add(c);
		
		this.material = m;
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
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + faceData.size());
		}
		
		return faceData.get(index);
	}
	
	/**
	 * Get the list of vertices that make up this face.
	 * @return the list of vertices
	 */
	public List<VertexData> getVertices(){
		return faceData;
	}

	/**
	 * Get the material from the face (if applicable)
	 * @return the material of the face
	 */
	public Material getMaterial() {
		return material;
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