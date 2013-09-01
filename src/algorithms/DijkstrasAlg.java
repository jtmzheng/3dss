package algorithms;

import graphs.Node;

/**
 * Class for Dijkstra's Algorithm
 * @author Max
 *
 */

public class DijkstrasAlg implements ShortestPathAlg{

	private Object src;
	private Object dest;
	
	public DijkstrasAlg(){
		
	}
	
	/**
	 * getShortestPath(Object, Object) gets the shortest path between source Node and dest Node in the current map
	 * 
	 * @param src = source node
	 * @param dest = dest node
	 * @return shortestPath
	 */
	public Object getShortestPath(Object src, Object dest) throws ArrayIndexOutOfBoundsException {
		
		// Error check for illegal arguments
		if (!(src instanceof Node) || !(dest instanceof Node))
			throw new IllegalArgumentException("DijkstrasAlg expects Node objects");
		
		return null;
	}


	@Override
	public void setMap(Object map) {
	}
	
	

	@Override
	public Object call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSource(Object src) {
		this.src = src;
		
	}

	@Override
	public void setDest(Object dest) {
		this.dest = dest;		
	}
	
	
}
