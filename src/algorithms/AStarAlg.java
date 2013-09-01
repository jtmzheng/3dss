package algorithms;

import graphs.Node;

public class AStarAlg implements ShortestPathAlg{
	
	private Object src;
	private Object dest;

	@Override
	public Object getShortestPath(Object src, Object dest)
			throws ArrayIndexOutOfBoundsException {

		// Error check for illegal arguments
		if (!(src instanceof Node) || !(dest instanceof Node))
			throw new IllegalArgumentException("AStarAlg expects Node objects");
		
		return null;
	}

	@Override
	public void setMap(Object map) {
		// TODO Auto-generated method stub
		
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
