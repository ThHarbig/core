package mayday.core.structures.graph.algorithm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;

/**
 * Iterator that iterates over the nodes of a graph in a depth-first manner. 
 * If the graph is not connected, each component is searched successively. 
 * @author Stephan Symons
 *
 */
public class DepthFirstIterator implements Iterator<Node>
{
	/** The search agenda	 */
	private Stack<Node> queue;
	/** The graph in question	 */
	private Graph graph;	
	/** The current node */
	private Node current;	
	/** Keeps track of already visited nodes. */
	private Map<Node,Boolean> seen;	
	/** Only iterate over one single component */
	private boolean singleComponent;
	
	/**
	 * Create a new iterator over a graph and start search at a defined node.
	 * @param graph The graph to be iterated over
	 */
	public DepthFirstIterator(Graph graph)
	{
		this.graph=graph;
		queue=new Stack<Node>();
		seen=new HashMap<Node, Boolean>();
		singleComponent=false;
		for(Node n:this.graph.getNodes())
		{
			current=n;
			break;
		}
		queue.add(current);		
	}
	
	/**
	 * Create a new iterator over a graph and start search at a defined node.
	 * @param graph The graph to be iterated over
	 * @param start The start node for iteration
	 */
	public DepthFirstIterator(Graph graph, Node start)
	{
		singleComponent=false;
		this.graph=graph;
		queue=new Stack<Node>();
		seen=new HashMap<Node, Boolean>();
		queue.add(start);
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() 
	{
		if(! queue.isEmpty()) return true;
		// stop if we should visit only one component
		if(queue.isEmpty() && singleComponent) return false;
		//we are finished if there are no more nodes to be visited
		//queue is empty and all nodes are seen
		// are all nodes seen?
		for(Node n:graph.getNodes())
		{
			if(seen.get(n)==null)
			{
				//a new connectivity component
				queue.add(n);
				return true;
			}
		}
		return false;
		
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public Node next() 
	{
		current=queue.pop();
		expand(current);
		return current;		
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove() 
	{
		graph.removeNode(current);		
	}
	
	/**
	 * Expand the node and store child nodes in search agenda as per rules of DFS. 
	 * @param node
	 */
	private void expand(Node node)
	{
		seen.put(node,true);
		for(Node n: graph.getNeighbors(node))
		{
			if(seen.get(n)==null) 
			{
				queue.push(n);
				seen.put(n, true);
				}
		}
	}

	/**
	 * @return the singleComponent
	 */
	public boolean isSingleComponent() {
		return singleComponent;
	}

	/**
	 * @param singleComponent the singleComponent to set
	 */
	public void setSingleComponent(boolean singleComponent) {
		this.singleComponent = singleComponent;
	}
	
	
	
}
