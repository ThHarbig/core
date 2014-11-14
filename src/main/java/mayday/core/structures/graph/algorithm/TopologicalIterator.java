package mayday.core.structures.graph.algorithm;

import java.util.Iterator;

import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;

/**
 * Iterator for moving over a graph in topological order.
 * @author Stephan Symons
 */
public class TopologicalIterator implements Iterator<Node>
{
	
	/** An iterator over a collection of nodes */
	private Iterator<Node> iter;
	
	/**
	 * Creates a new iterator. This works by calling Graph.topologicalSort
	 * on the graph and iterating over the sorted set of nodes. 
	 * @param graph
	 */
	public TopologicalIterator(Graph graph)
	{
		iter=Graphs.topologicalSort(graph).iterator(); 
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() 
	{
		return iter.hasNext();
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public Node next() 
	{
		return iter.next();

	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove() 
	{
		iter.remove();
	}

}
