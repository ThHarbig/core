package mayday.core.structures.graph.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;

public class CycleRemoval 
{
	/**
	 * Caclulate the feedback arcs using Eades' GC algorithm (Eades et al 1993)
	 * @param g
	 * @return
	 */
	public Set<Edge> greedyFeedbackArcs(Graph g)
	{
		List<Node> sl=new LinkedList<Node>();
		List<Node> sr=new LinkedList<Node>();
		// flat clone the graph:
		Graph g2=Graphs.flatCopy(g);
		while(true)
		{
			while(true)
			{
				Node sink=null;
				for(Node n: g2.getNodes())
				{
					if(g2.getOutDegree(n)==0) // a sink?
					{
						sink=n;
						break;
					}					
				}
				if(sink!=null)
				{
//					System.out.println("Sink: "+sink);
					g2.removeNode(sink);
					sr.add(0, sink);
				}else
				{
					break;
				}
			}
			
			while(true)
			{
				Node source=null;
				for(Node n: g2.getNodes())
				{
					if(g2.getOutDegree(n)==0) // a sink?
					{
						source=n;
						break;					
					}					
				}
				if(source!=null)
				{
//					System.out.println("Source: "+source);
					g2.removeNode(source);
					sl.add(source);				
				}else
				{
					break;
				}
			}
			// choose some node
			if(g2.nodeCount()!=0){
				int num=Integer.MIN_VALUE;
				Node maxNode=null;
				for(Node n: g2.getNodes())
				{
					int nr=g2.getOutDegree(n)-g2.getInDegree(n);
					if(nr > num)
					{
						num=nr;
						maxNode=n;
					}								
				}
				sl.add(maxNode);
//				System.out.println("w: "+maxNode);
				g2.removeNode(maxNode);				
			}
			else{
				break;
			}
		}
		List<Node> slsr=new ArrayList<Node>(sl);
		slsr.addAll(sr);
		
		Set<Edge> edges=new HashSet<Edge>();
		for(int i=0; i!= slsr.size(); ++i)
		{
			Node n=slsr.get(i);
			for(Edge e:g.getOutEdges(n))
			{
				if(slsr.indexOf(e.getTarget()) < i)
					edges.add(e);
			}
		}		
		return edges;
	}
	
	/**
	 * Removes cycles from a graph, if there are any. To calculate the set of edges that
	 * induce a cycle, the greedy feedback arc algorithm (Eades et al 1993) is used. 
	 * <b>Note: </b>Modifies the graph. Use Graphs.flatCopy() to produce a working copy of the graph. 
	 * @param g The input graph (will be modified)
	 * @param feedbackArcs The set if feedback arcs. <b>Note</b> If the set of
	 *  feedback arcs is not sufficient to remove all cycles in g, all cycles
	 *  will be removed that are induced by the edges in feedbackArcs. 
	 * 
	 */
	public void removeCycles(Graph g, Set<Edge> feedbackArcs)
	{
		for(Edge e: feedbackArcs)
		{
			g.reverseEdge(e);
		}
	}

	/**
	 * Removes cycles from a graph, if there are any. To calculate the set of edges that
	 * induce a cycle, the greedy feedback arc algorithm (Eades et al 1993) is used. 
	 * <b>Note</b>Modifies the graph. Use Graphs.flatCopy() to produce a working copy of the graph. 
	 * @param g The input graph (will be modified) 
	 * @return The input graph, with feedback edges removed. This is the same object as <code>g</code> 
	 */
	public void removeCycles(Graph g)
	{
		removeCycles(g, greedyFeedbackArcs(g));
	}
}
