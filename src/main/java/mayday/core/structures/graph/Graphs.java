package mayday.core.structures.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import mayday.core.structures.graph.algorithm.CycleDetection;
import mayday.core.structures.linalg.matrix.DoubleMatrix;

/**
 * This is a utilities class for Graphs.
 * @author Stephan Symons
 * @see Graph
 */
public final class Graphs 
{
	/**
	 * Identifies the root of the given graph. The root is defined as 
	 * the node with the smallest inDegree. If the root has an inDegree > 0,
	 * the graph is not a tree. 
	 * @param g The graph in which the root is to be found. 
	 * @return One node with the smallest inDegree
	 * @see Node
	 */
	public static Node findRoot(Graph g)
	{
		int inDeg=Integer.MAX_VALUE;
		Node root=null;
		for(Node n:g.getNodes())
		{
			int deg=g.getInDegree(n);
			if(deg < inDeg)
			{
				inDeg=deg;
				root=n;
			}
		}
		return root;
	}

	/**
	 * Finds all nodes of the graph that have outDegree = 0. 
	 * @param g The graph whose leaves are to be found. 
	 * @return ArrayList of Nodes with outDegree=0.
	 */
	public static List<Node> findLeaves(Graph g)
	{
		List<Node> leaves=new ArrayList<Node>();
		for(Node n:g.getNodes())
		{
			if(g.getOutDegree(n)==0)
				leaves.add(n);
		}		
		return leaves;
	}

	/**
	 * Calculates all connected components of the graph. This function considers 
	 * the graph to be undirected. Therefore, on a directed graph, it calculates
	 * the <b>weakly</b> connected components. 
	 * 
	 * @param graph A graph with one or more connected components.
	 * @return A list of a components, defined as list of nodes which belong to the same component
	 */
	public static List<List<Node> > calculateComponents(Graph graph)
	{
		//fetch first node, whatever this is:		
		Stack<Node> queue=new Stack<Node>();
		HashMap<Node,Boolean> seen=new HashMap<Node, Boolean>();
		Node current=null;
		for(Node n:graph.getNodes())
		{
			current=n;
			break;
		}
		queue.add(current);		
		List<Node> currentComponent=new ArrayList<Node>();
		List<List<Node>> res=new ArrayList<List<Node>>();
		while(true)
		{
			//fetch next node:
			current=queue.pop();
			for(Node n: graph.getNeighbors(current))
			{
				if(seen.get(n)==null) 
				{
					queue.push(n);
					seen.put(n,true);
				}
			}
			currentComponent.add(current);
			seen.put(current,true);

			// are we finished with a component?
			if(queue.isEmpty())
			{
				res.add(currentComponent);
				currentComponent=new ArrayList<Node>();
				boolean found=false;
				for(Node n:graph.getNodes())
				{	

					if(seen.get(n)==null)
					{
						//a new connectivity component
						queue.add(n);
						found=true;
						break;
					}
				}
				if(!found) break;
			}			
		}		
		return res;
	}

	/**
	 * Checks if the graph has any cycle
	 * @param graph The Graph to be tested
	 * @return true if the graph has at least one cycle, false otherwise
	 */
	public static boolean hasCycle(Graph graph)
	{
		HashMap<Node,Integer> inDegree=new HashMap<Node,Integer>();
		Queue<Node> noInDegrees=new LinkedList<Node>();
		for(Node n: graph.getNodes())
		{
			int in=graph.getInDegree(n);
			if(in ==0 ) noInDegrees.add(n);
			inDegree.put(n, in);
		}
		if(noInDegrees.isEmpty())
		{
			// no nodes with zero inDegree --> graph has a cycle!
			return true;
		}
		while(!noInDegrees.isEmpty())
		{
			// remove first node from stack, remove it from inDegree map, and decrease in degree of all adjacent nodes. 
			Node n=noInDegrees.poll();			
			inDegree.remove(n);
			for(Edge e:graph.getEdges(n))
			{
				int deg=inDegree.get(e.getTarget());
				deg--;
				if(deg==0) noInDegrees.add(e.getTarget());
				inDegree.put(e.getTarget(),deg);
			}
		}
		// are any nodes left with inDegree?
		return !inDegree.isEmpty();
	}

	/**
	 * Topologically sorts the graph. <br>
	 * This is not guaranteed to work if the graph contains cycles
	 * @param graph The graph to be sorted
	 * @return An ordered list of nodes
	 */
	public static List<Node> topologicalSort(Graph graph)
	{
		HashMap<Node,Integer> inDegree=new HashMap<Node,Integer>();
		List<Node> res=new ArrayList<Node>();
		Queue<Node> noInDegrees=new LinkedList<Node>();
		// calculate & store inDegrees 
		for(Node n: graph.getNodes())
		{
			int in=graph.getInDegree(n);
			if(in ==0 ) noInDegrees.add(n);
			inDegree.put(n, in);
		}	
		if(noInDegrees.isEmpty())
		{
			// no nodes with zero inDegree --> graph has a cycle!
			return null;
		}
		while(!noInDegrees.isEmpty())
		{
			// remove first node from stack, remove it from inDegree map, and decrease in degree of all adjacent nodes. 
			Node n=noInDegrees.poll();
			res.add(n);
			inDegree.remove(n);
			for(Edge e:graph.getEdges(n))
			{
				int deg=inDegree.get(e.getTarget());
				deg--;
				if(deg==0) noInDegrees.add(e.getTarget());
				inDegree.put(e.getTarget(),deg);
			}
		}
		// are any nodes left with inDegree?
		return inDegree.isEmpty()?res:null;		
	}
	/**
	 * Topologically sorts the graph. If the graph contains cycles, this algorithm does not work. 
	 * @param graph A acyclic graph
	 * @param component A component to be sorted.
	 * @return A topologically ordered list of nodes. 
	 */
	public static List<Node> topologicalSort(Graph graph, List<Node> component)
	{
		HashMap<Node,Integer> inDegree=new HashMap<Node,Integer>();
		List<Node> res=new ArrayList<Node>();
		Queue<Node> noInDegrees=new LinkedList<Node>();
		// calculate & store inDegrees 
		for(Node n: component)
		{
			int in=graph.getInDegree(n);
			if(in ==0 ) noInDegrees.add(n);
			inDegree.put(n, in);
		}	
		// no nodes with zero inDegree --> graph has a cycle!
		if(noInDegrees.isEmpty()) return null;
		while(!noInDegrees.isEmpty())
		{
			// remove first node from stack, remove it from inDegree map, and decrease in degree of all adjacent nodes. 
			Node n=noInDegrees.poll();
			res.add(n);
			inDegree.remove(n);
			for(Edge e:graph.getEdges(n))
			{
				if(e.getTarget()==null) continue;
				int deg=inDegree.get(e.getTarget());
				deg--;
				if(deg==0) noInDegrees.add(e.getTarget());
				inDegree.put(e.getTarget(),deg);
			}
		}

		return inDegree.isEmpty()?res:null;
	}


	/**
	 * 
	 * Calculates the first node in the graph. This is the node with the lowest inDegree,
	 * or any node with the lowest inDegree, if several nodes have the minimal inDegree.	 *
	 * @param A graph
	 * @return A node with the minimum inDegree in the graph.
	 */
	public static Node firstNode(Graph g)
	{
		Node n0=null;
		int minDeg=Integer.MAX_VALUE;
		for(Node n: g.getNodes())
		{
			if(g.getInDegree(n) < minDeg)
			{
				minDeg=g.getInDegree(n);
				n0=n;				
			}
		}
		return n0;
	}

	/**
	 * Decides if the graph is linear, ie. each node has at most one outgoing and incoming edge. 
	 * @param g A graph 
	 * @return true if the graph is linear, else false.
	 */
	public static boolean isLinear(Graph g)
	{
		if(hasCycle(g)) return false;
		for(Node n:g.getNodes())
		{
			if(g.getOutDegree(n)>1 && g.getInDegree(n)>1)
				return false;			
		}
		return true; 
	}

	/**
	 * Decides if the graph is branched, ie. at least node has more than one incoming or more than one outgoing edge.
	 * @param g A graph 
	 * @return true if the graph is branched, else false.
	 */
	public static boolean isBranched(Graph g)
	{
		if(hasCycle(g)) return false;
		for(Node n:g.getNodes())
		{
			if(g.getOutDegree(n)>1 || g.getInDegree(n)>1)
				return true;			
		}
		return false; 
	}

	/**
	 * Decides if the graph is circular, ie. it consists of a single cycle and nothing more. 
	 * @param g A graph 
	 * @return true if the graph is branched, else false.
	 */
	public static boolean isCircular(Graph g)
	{
		if(!hasCycle(g)) return false;
		return new CycleDetection().detectCycle(g, g.getNodesIterator().next()).size()==g.nodeCount();
	}

	/**
	 * Removes all nodes from the graph, except the nodes specified in <code>nodes</code>
	 * @param g A graph
	 * @param nodes The nodes to be retained in <code>g</graph>
	 * @return A copy of <code>g</code> restriced to <code>nodes</code>
	 */
	public static Graph restrict(Graph g, Collection<Node> nodes)
	{
		Graph res=new Graph();
		res.setName(g.getName());
		for(Node n:nodes)
		{
			res.addNode(n);			
		}
		for(Node n:nodes)
		{
			for(Node m:g.getOutNeighbors(n))
			{
				if(res.contains(m))
				{
					// get the edge and clone it 
					Edge template= g.getEdge(n, m);
					Edge cloneEdge= template.clone();
					res.connect(cloneEdge);
//					res.connect(n,m);
				}
			}
		}		
		return res;
	}

	//	/**
	//	 * Removes all nodes from the graph, except the nodes specified in <code>nodes</code>
	//	 * @param g A graph
	//	 * @param nodes The nodes to be retained in g
	//	 * @return A copy of graph restriced to nodes
	//	 */
	//	public static void restrictGraph(Graph g, Set<Node> nodes)
	//	{
	//		Iterator<Node> iter=g.getNodesIterator();
	//		while(iter.hasNext())
	//		{
	//			Node n=iter.next();
	//			if(!nodes.contains(n))
	//			{
	//				g.removeNode(n);
	//			}
	//		}
	//	}

	/**
	 * Removes the nodes from g, and replaces them with a new node. Reroutes 
	 * all edges to the specified node to the new nodes, and drops all edges
	 * that are no longer necessary.
	 * 
	 * @param g A graph
	 * @param nodes The nodes to be collapsed to one node
	 * @return g with the nodes collapsed.
	 */
	public static Graph collapseNodes(Graph g, Collection<Node> nodes)
	{
		Graph res=g.clone();
		Node resultNode=new Node(res);
		res.addNode(resultNode);
		for(Node n:nodes)
		{
			for(Edge e:res.getInEdges(n))
			{
				e.setTarget(resultNode);
			}
			for(Edge e:res.getOutEdges(n))
			{
				e.setSource(resultNode);
			}
			res.removeNode(n);
		}	
		Iterator<Edge> edges=res.getEdgesIterator();
		while(edges.hasNext())
		{
			Edge e=edges.next();
			if(!res.contains(e.getSource()) || !res.contains(e.getTarget()) || (e.getSource()==e.getTarget() && e.getSource()==resultNode))
				edges.remove();

		}
		return res;
	}

	/**
	 * Removes the nodes from g, and replaces them with a new node. Reroutes 
	 * all edges to the specified node to the new nodes, and drops all edges
	 * that are no longer necessary.
	 * 
	 * @param g A graph
	 * @param nodes The nodes to be collapsed to one node
	 * @return g with the nodes collapsed.
	 */
	public static Node collapseGraph(Graph g, Collection<Node> nodes)
	{
		Node resultNode=new Node(g);
		resultNode.setName("new");
		g.addNode(resultNode);
		for(Node n:nodes)
		{
			for(Edge e:g.getInEdges(n))
			{
				e.setTarget(resultNode);
				g.connect(e.source,resultNode);
			}
			for(Edge e:g.getOutEdges(n))
			{
				e.setSource(resultNode);	
				g.connect(resultNode,e.getTarget());
			}
			g.removeNode(n);			
		}

		List<Edge> del=new ArrayList<Edge>();


		for(Edge e: g.getEdges())
		{
			if(!g.contains(e.getSource()) || !g.contains(e.getTarget()) || (e.getSource()==e.getTarget() && e.getSource()==resultNode)  )
			{
				del.add(e);
			}			
		}
		for(Edge e:del)
			g.removeEdge(e);
		return resultNode;
	}

	/**
	 * Makes a flat copy of the original graph. This means, all edges and all nodes of <code>original</code> will be
	 * added to the resulting graph. Node that the nodes and edges will be shared between graphs and the node operations will be performed on
	 * the original graph 
	 * @param original The graph to be copied
	 * @return a flat copy of the <code>original</code> graph. 
	 */
	public static Graph flatCopy(Graph original){
		Graph copy=new Graph();
		copy.setName(original.getName());

		for(Node n:original.getNodes())
			copy.addNode(n);

		for(Edge e:original.getEdges())
			copy.connect(e.clone());

		return copy;
	}

	/**
	 * Class for simple adjacency matrix
	 * @author symons
	 *
	 */
	public static class AdjacencyMatrix extends DoubleMatrix
	{
		private Map<Node, Integer> nodeMap; 

		/**
		 * Create new adjacency matrix
		 * @param nrow
		 * @param ncol
		 */
		public AdjacencyMatrix(int nrow, int ncol) 
		{
			super(nrow, ncol);			
		}



		/**
		 * Copy constructor; creates a deep copy
		 * @param m
		 */
		public AdjacencyMatrix(AdjacencyMatrix m) {
			super(m);
			this.nodeMap=m.nodeMap;
		}

		/**
		 * Get the node mapping
		 * @return
		 */
		public Map<Node, Integer> getNodeMap() {
			return nodeMap;
		}

		/**
		 * Set the node mapping
		 * @param nodeMap
		 */
		public void setNodeMap(Map<Node, Integer> nodeMap) {
			this.nodeMap = nodeMap;
		}
	}

	/**
	 * Create an adjacency matrix (currently, binaray) 
	 * @param g
	 * @return
	 */
	public static AdjacencyMatrix adjacencyMatrix(Graph g)
	{
		AdjacencyMatrix m=new AdjacencyMatrix(g.nodeCount(), g.nodeCount());
		int i=0; 
		List<Node> nodes=new ArrayList<Node>(g.getNodes());
		Collections.sort(nodes);
		Map<Node, Integer> map=new HashMap<Node, Integer>();

		for(Node n: nodes)
		{
			m.setColumnName(i, n.getName());
			m.setRowName(i, n.getName());
			map.put(n, i);
			++i;
		}
		for(i=0; i!=nodes.size(); ++i)
		{
			for(Node target: g.getOutNeighbors(nodes.get(i)))
			{
				m.setValue(i, map.get(target), 1.0);
			}			
		}
		m.setNodeMap(map);
		return m;		
	}
	
	/**
	 * Calculate, using DFS, the transitive closure of Graph g.
	 * @param g
	 * @return
	 */
	public static Graph transitiveClosure(Graph g)
	{
		Graph res=flatCopy(g);
		for(Node n:g.getNodes())
		{
			List<Node> reachable=new LinkedList<Node>();
			traverse(g, n, reachable);
			for(Node nr: reachable)
				res.connect(n,nr);
		}
		return res;		
	}
	
	/**
	 * For calculating the transitive closure, traverse the graph and keep track of every node encountered. 
	 * @param g
	 * @param n
	 * @param passed
	 */
	private static void traverse(Graph g, Node n, List<Node> passed)
	{
		for(Node nnext: g.getOutNeighbors(n))
		{
			passed.add(nnext);
			traverse(g,nnext,passed);
		}
	}
	
	/**
	 * Calculate the transitive reduction red, as defined by red = adj - (adj o closure) 
	 * @param adj
	 * @param closure
	 * @return the transitive reduction red. 
	 */
	public static AdjacencyMatrix transitiveReduction(AdjacencyMatrix adj, AdjacencyMatrix closure)
	{
		// calculate adj o closure
		AdjacencyMatrix res=new AdjacencyMatrix(adj);
		for(int i=0; i!=adj.nrow(); ++i){
			for(int j=0; j!=adj.nrow(); ++j){
				if(adj.getValue(i, j)==1){
					for(int k=0; k!= closure.ncol(); ++k){
						if(closure.getValue(j,k)==1){
							res.setValue(i, k, res.getValue(i, k)-1 <= 0 ? 0:1 );
						}
					}
				}
			}
		}
		return res;
	}
	
	/**
	 * Calculate the transitive reduction red of graph g, as defined by red = adj - (adj o closure) 
	 * @param adj
	 * @param closure
	 * @return the transitive reduction red, i.e. a flat  copy of the input graph with all transitive edges removed.
	 */
	public static Graph transitiveReduction(Graph g)
	{
		Graph res=flatCopy(g);
		AdjacencyMatrix r=adjacencyMatrix(res);
		System.out.println(r);
		
		AdjacencyMatrix t=adjacencyMatrix(transitiveClosure(g));
		System.out.println(t);
		AdjacencyMatrix rm=transitiveReduction(r, t);
		System.out.println(rm);
		
		for(Edge e:g.getEdges())
		{
			if(rm.getValue(rm.getNodeMap().get(e.getSource()), 
					rm.getNodeMap().get(e.getTarget()))<=0)
					res.removeEdge(e);
		}
		return res; 
	}

}
