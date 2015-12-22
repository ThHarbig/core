package mayday.core.structures.graph.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;

/**
 * Utility class for finding strongly connected components. 
 * @author Stephan Symons
 * @version 0.9
 */
public class StronglyConnectedComponents 
{
	private static int index = 0;
	private static  List<Node> stack = new ArrayList<Node>();
	private static  List<List<Node>> SCC = new ArrayList<List<Node>>();

	private static  Map<Node,Integer> indexMap=new HashMap<Node, Integer>();
	private static  Map<Node,Integer> lowLink=new HashMap<Node, Integer>();

	/**
	 * Finds strongly connected components. 
	 * @param g Digraph in which strongly connected components are to be found
	 * @return A list of strongly connected components.
	 */
	public static List<List<Node>> findComponents(Graph g)
	{
		index=0;
		stack = new ArrayList<Node>();
		SCC = new ArrayList<List<Node>>();
		indexMap=new HashMap<Node, Integer>();
		lowLink=new HashMap<Node, Integer>();
		Set<Node> nodes=new HashSet<Node>(g.getNodes());
		while(true)
		{
			 findComponents(g, nodes.iterator().next());
			 for(List<Node> l:SCC)
			 {
				nodes.removeAll(l); 
			 }
			 if(nodes.isEmpty()) break;			 
		}		
		return SCC;
	}
	
	/**
	 * Finds strongly connected components. 
	 * @param g Digraph in which strongly connected components are to be found
	 * @param v The node to start at 
	 * @return A list of strongly connected components.
	 */
	private static List<List<Node>> findComponents(Graph g, Node v)
	{
		indexMap.put(v, index);
		lowLink.put(v, index);

		index++;
		stack.add(0, v);
		for(Node n: g.getOutNeighbors(v))
		{			
			if(indexMap.containsKey(n) == false) // n.index is undefined
			{
				findComponents(g, n);
				lowLink.put(v,Math.min(lowLink.get(n), lowLink.get(v)));
			}else if(stack.contains(n))
			{
				lowLink.put(v,Math.min(lowLink.get(v), indexMap.get(n)));				
			}
		}
		if(lowLink.get(v) == indexMap.get(v))
		{
			Node n;
			ArrayList<Node> component = new ArrayList<Node>();
			do{
				n = stack.remove(0);
				component.add(n);
			}while(n != v);
			SCC.add(component);
		}
		return SCC;
	}
	
	/**
	 * Approximately calculates one longest simple cycle from the strongly connected component of the graph
	 * @param component A list of nodes that form a strongly connected component in g
	 * @param g A graph
	 * @return A list of node that form one of the  longest simple cycles in g. 
	 */
	public static Set<Node> getLongestSimpleCycle(List<Node> component, Graph g)
	{
		List<Set<Node>> res=new ArrayList<Set<Node>>();
		traverseComponent(component.get(0), component, g, new HashSet<Node>(),res);
		Set<Node> maxList=null;
		int max=0;
		for(Set<Node> l:res)
		{
			if(l.size()>=max)
			{
				max=l.size();
				maxList=l;
			}
		}
		return maxList;
	}
	
	
	
	/**
	 * Traverse graph searching for longest cycle
	 * @param v
	 * @param component
	 * @param g
	 * @param seen
	 * @param foundCycles
	 */
	private static void traverseComponent(Node v, List<Node> component, Graph g, Set<Node> seen, List<Set<Node>> foundCycles)
	{
		seen.add(v);
		Stack<Node> stack=new Stack<Node>();
		for(Node n: g.getOutNeighbors(v))
		{
			if(component.contains(n))
			{
				if(seen.contains(n)) // we have met some cycle
				{
					Set<Node> cyc=new HashSet<Node>(seen);
					cyc.add(n);
					foundCycles.add(cyc);
					return;
				}	
				stack.push(n);								
			}
		}
		for(Node n:stack)
			traverseComponent(n, component, g, seen, foundCycles);
	}
	
}
