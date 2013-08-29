package algorithms;

import java.util.concurrent.Callable;

/**
 * Interface for shortest path algorithm (extends Callable)
 * @author Max
 *
 */
public interface ShortestPathAlg extends Callable<Object>{
	/**
	 * getShortestPath(Object, Object) gets the shortest path between source Node and dest Node in the current map
	 * 
	 * @param src = source node
	 * @param dest = dest node
	 * @return shortestPath
	 */
	public Object getShortestPath(Object src, Object dest) throws ArrayIndexOutOfBoundsException;

	/**
	 * setMap(Object map) sets the map to the map passed in
	 * 
	 * @param map
	 */
	public void setMap(Object map);
	/**
	 * setSource(Object map) sets the src to the src passed in
	 * 
	 * @param map
	 */
	public void setSource(Object src);
	/**
	 * setDest(Object dest) sets the dest to the dest passed in
	 * 
	 * @param map
	 */
	public void setDest(Object dest);
}
