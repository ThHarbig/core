package mayday.core.structures.graph.algorithm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;

/**
 * This Iterator class allows to move through a graph in a Breadth First strategy. 
 * @author Stephan Symons
 * @see Iterator
 */
public class BreadthFirstIterator implements Iterator<Node>
{
	/** The queue containing the next nodes */
	Queue<Node> queue;
	/** The graph to operate on */
	private Graph graph;
	/** The current node */
	private Node current;
	/** The list of seen nodes */
	private Map<Node,Boolean> seen;
	
	

	/**
	 * Creates a new iterator to iterate over the graph. 
	 * @param graph The graph to operate on
	 */
	public BreadthFirstIterator(Graph graph)
	{
		this.graph=graph;
		queue=new LinkedList<Node>();
		seen=new HashMap<Node, Boolean>();
		
		for(Node n:this.graph.getNodes())
		{
			current=n;
			break;
		}
		queue.add(current);
	}
	
	/**
	 * Creates a new iterator to iterate over the graph, 
	 * starting at a specified node. 
	 * @param graph The graph to operate on
	 * @param start The node to begin iteration at.
	 */
	public BreadthFirstIterator(Graph graph, Node start)
	{
		this.graph=graph;
		queue=new LinkedList<Node>();
		seen=new HashMap<Node, Boolean>();
		queue.add(start);
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() 
	{
		if(! queue.isEmpty()) return true;
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
		current=queue.poll();
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
	 * Expand the current node
	 * @param node
	 */
	private void expand(Node node)
	{
//		System.out.print("Expanding "+node+":");
		for(Node n: graph.getNeighbors(node))
		{
//			System.out.print(n+"\t");
			if(seen.get(n)==null) queue.add(n);
		}
//		System.out.println(queue);
//		System.out.println("---------------------");
		seen.put(node,true);
	}

}
