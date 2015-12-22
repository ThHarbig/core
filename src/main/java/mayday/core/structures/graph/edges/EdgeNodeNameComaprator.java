package mayday.core.structures.graph.edges;

import java.util.Comparator;

import mayday.core.structures.graph.Edge;

/**
 * This comparator ensures that edges can be sorted according to their adjacent nodes. 
 * Comparison is done by node name.
 * @see Node
 * @author Stephan Symons
 */
public class EdgeNodeNameComaprator implements Comparator<Edge> 
{
	@Override
	public int compare(Edge o1, Edge o2) 
	{
		int i=o1.getSource().getName().compareTo(o2.getSource().getName());
		if(i==0)
			return o1.getTarget().getName().compareTo(o2.getTarget().getName());
		return i;
	}
}
