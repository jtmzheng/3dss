package graphs;

/**
 * Directed weighted edge class
 * 
 * */
public class Edge {
	
	private Node to = new Node();
	private Node from = new Node();
	public int weight;
	
	public Edge(Node src, Node dest, int weight){
		to = dest;
		from = src;
	}
	
	public Node fromNode(){
		return from;
	}
	
	public Node toNode(){
		return to;
	}
	
	public int weight(){
		return weight;
	}
	
}
