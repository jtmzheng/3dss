package graphs;

import java.util.List;

/**
 * Graphs will be built in graph classes depending on what is desired.
 * In constructor data needed to generate graphs sent in and the graphs
 * are made using the appropriate Node and Edge objects as needed. For 
 * dense graphs the Edge and Node objects should not be used.
 * @author Max
 *
 */

public interface DirectedGraph {
	
	public List<Edge> adj(Node n);
	public int V();
	public int E();
	public DirectedGraph graphType();
	
}
