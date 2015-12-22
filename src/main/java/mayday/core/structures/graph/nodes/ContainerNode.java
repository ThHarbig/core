package mayday.core.structures.graph.nodes;

import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;


/**
 * This is container node that can keep any comparable object as a payload.  
 * @author Stephan Symons
 *
 * @param <T> Any comparable class 
 */
@SuppressWarnings("unchecked")
public class ContainerNode<T extends Comparable> extends Node
{
	protected T payload;
	
	/**
	 * Creates an empty container node.
	 */
	public ContainerNode(Graph graph)
	{
		super(graph);
	}
	
	/**
	 * Creates an empty container node with name <code>name</name>
	 * @param name
	 */
	public ContainerNode(Graph graph, String name)
	{
		super(graph, name);
	}
	
	/**
	 * Creates an empty container node with name <code>name</name>
	 * @param name
	 */
	public ContainerNode(Graph graph, T payload)
	{
		super(graph);
		this.payload=payload;
	}
	
	/**
	 * @return the payload
	 */
	public T getPayload() {
		return payload;
	}

	/**
	 * @param payload the payload to set
	 */
	public void setPayload(T payload) {
		this.payload = payload;
	}



	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Node o) 
	{
		if(o instanceof ContainerNode)
		{
			return payload.compareTo(((ContainerNode) o).getPayload());
		}
		return super.compareTo(o);		
	}
	
	@Override
	public String toString()
	{
		return payload.toString();
	}
	
	
}
