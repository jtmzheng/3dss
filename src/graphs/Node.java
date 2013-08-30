package graphs;

import java.util.ArrayList;
import java.util.List;

public class Node {
	
	private List<Node> adj = new ArrayList<Node>();
	private int E = 0; //number of outgoing edges
	private float[] position = {0, 0, 0};
	
	/**
	 * Unfinished constructor
	 */
	public Node(/*Associated data*/){
		
	}
	
	public Node(float x, float y, float z){
		position[0] = x;
		position[1] = y;
		position[2] = z;
	}
	
	/**
	 * 
	 * @param n - node to be added to the adjacency list 
	 * @return - true if successfully added, false if not
	 */
	public boolean addAdj(Node n){
		if(adj.add(n)){
			++E;
			return true;
		}
		else
			return false;
	}
	
	public List<Node> getAdjNodes () {
		return adj;
	}
	
	public float[] getPosition() {
		return position;
	}

}
