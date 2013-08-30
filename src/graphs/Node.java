package graphs;

import java.util.ArrayList;
import java.util.List;

public class Node {
	
	private List<Node> adj = new ArrayList<Node>();
	private int E = 0; //number of outgoing edges
	/**
	 * Unfinished constructor
	 */
	public Node(/*Associated data*/){
		
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
	

}
