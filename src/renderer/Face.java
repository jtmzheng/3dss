package renderer;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

/**
 * May rename Triangle in the future
 * @author Max
 * @author Adi
 */
public class Face {

	public List<VertexData> faceData;
	public Vector3f normalVector = null;
	
	public Face() {}
	
	public Face(List<VertexData> faceData) {
		this.faceData = faceData;
	}
	
	public void add (VertexData vertex) {
		faceData.add(vertex);
	}
	
	public void setNormal (Vector3f normal) {
		this.normalVector = normal;
	}
	
    /**
     * Computes the normal vector to this face.
     */
    public void calculateNormal() {
        float[] edge1 = new float[3];
        float[] edge2 = new float[3];
        float[] normal = new float[3];
        
        float[] v1 = faceData.get(0).getGeometric();
        float[] v2 = faceData.get(1).getGeometric();
        float[] v3 = faceData.get(2).getGeometric();
        
        float[] p1 = {v1[0], v1[1], v1[2]};
        float[] p2 = {v2[0], v2[1], v2[2]};
        float[] p3 = {v3[0], v3[1], v3[2]};

        edge1[0] = p2[0] - p1[0];
        edge1[1] = p2[1] - p1[1];
        edge1[2] = p2[2] - p1[2];

        edge2[0] = p3[0] - p2[0];
        edge2[1] = p3[1] - p2[1];
        edge2[2] = p3[2] - p2[2];

        // Cross product of the two edges.
        normal[0] = edge1[1] * edge2[2] - edge1[2] * edge2[1];
        normal[1] = edge1[2] * edge2[0] - edge1[0] * edge2[2];
        normal[2] = edge1[0] * edge2[1] - edge1[1] * edge2[0];

        normalVector = new Vector3f(normal[0], normal[1], normal[2]);
    }
    
    public String toString() { 
        String result = "\nVertices: " + faceData.size() + "\n-----------\n";
        for(VertexData f : faceData) {
            result += f.toString() + "\n";
        }
        return result;
    }
    
    public Vector3f getComputedNormal() {
    	if(normalVector == null)
    		calculateNormal();
    	
    	return this.normalVector;
    }
	
}

