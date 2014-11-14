package mayday.core.structures.graph.edges;

import java.util.Comparator;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Node;

/**
 * This comparator ensures that edges can be sorted according to their adjacent nodes. 
 * Comparison is done by node id.
 * @see Node
 * @see Node.getID
 * @author Stephan Symons
 */
public class EdgeNodeIDComparator implements Comparator<Edge> 
{

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Edge o1, Edge o2) 
	{
		int r= o1.getSource().getID() - o2.getSource().getID();
		if(r==0)
			return o1.getTarget().getID() - o2.getTarget().getID();
		else
			return r; 
	}

}
