package mayday.core.structures.graph.algorithm;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;

/**
 * Helper class for cycle detection
 * @author Stephan Symons
 *
 */
public class CycleDetection 
{
	private static final int NOT_VISITED = 0;
	private static final int BEING_VISITED = 1;
	private static final int DONE_VISITED = 2;
	
	private Map<Node, Integer> visited=new HashMap<Node, Integer>();
	
	private Set<Node> cycle;
	
	private boolean finished;
	
	/**
	 * Finds cycles containing starting from node n in g. Does not necessarily find the
	 * largest cycle.  
	 * @param g A graph
	 * @param n a node to start cycle detection at
	 * @return A set of nodes forming a cycle. If the set is empty, no cycle was found.
	 */
	public Set<Node> detectCycle(Graph g, Node n)
	{
		for(Node node: g.getNodes())
			visited.put(node, NOT_VISITED);
		cycle=new TreeSet<Node>();
		finished=false;
		visit(g,n);	
		
		return cycle;
		
	}
	
	
	
	private Node visit(Graph g,Node n)
	{
		if(finished) return null;
		
		if(visited.get(n)==BEING_VISITED)
		{
			cycle.add(n);
			return n;
		}
		if(visited.get(n)==DONE_VISITED)
		{
			return null;
		}
		visited.put(n,BEING_VISITED);
		
		for(Node nNext:g.getOutNeighbors(n))
		{
			Node v2=visit(g,nNext);
			if(v2!=null)
			{
				cycle.add(n);				
				if(v2==n)
				{					
					finished=true;
				}
				return v2;
			}
		}
		visited.put(n,DONE_VISITED);
		return null;
			
			
	}
	

}
