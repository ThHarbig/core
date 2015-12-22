package mayday.core.structures.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamWriter;

import mayday.core.structures.graph.io.GraphMLExport;

/**
 * Graph data structure. Contains nodes of type {@link mayday.graph.Node},
 * connected by edges of type{@link mayday.graph.Edge}. Each node may exist
 * multiple times in the graph. 
 * The graph is directed. 
 * @author Stephan Symons
 *
 */
public class Graph implements Cloneable
{
	/** adjacency map */
	protected HashMap<Node,LinkedList<Edge>> adjacencyMap;
	
	/** map for faster inDegree calculation */
	protected HashMap<Node,LinkedList<Edge>> inDegreeMap;

	/** The name of the graph */
	protected String name;	
	
	/**
	 * Default constructor. Create a new, empty graph. 
	 */
	public Graph()
	{
		adjacencyMap=new HashMap<Node, LinkedList<Edge>>();
		inDegreeMap=new HashMap<Node,LinkedList<Edge>>();
//		edges=new HashSet<Edge>();

	}

	/**
	 * Removes all nodes and edges from the graph
	 */
	public void clear()
	{
		adjacencyMap.clear();
		inDegreeMap.clear();
//		edges=new HashSet<Edge>();

	}

	/**
	 * Checks if the graph is empty, i.e. contains no nodes. 
	 * 
	 * @return true, if the graph contains no nodes, or false if the graph contains 1 or more nodes. 
	 */
	public boolean isEmpty()
	{
		return adjacencyMap.isEmpty();
	}

	/**
	 * Returns the number of nodes. 
	 * 
	 * @return the number of nodes
	 */
	public int nodeCount()
	{
		return adjacencyMap.size();
	}

	/**
	 * Returns the number of edges in this Graph object.
	 *
	 * @return the number of edges.
	 */
	public int edgeCount()
	{
		int n=0;
		for(List<Edge> l: adjacencyMap.values())
			n+=l.size();
		return n;
	}

	/**
	 * Adds a node to the graph
	 * @param node
	 */
	public void addNode(Node node)
	{
		adjacencyMap.put(node,new LinkedList<Edge>());
		inDegreeMap.put(node, new LinkedList<Edge>());
	}

	/**
	 * Connect two nodes. 
	 * @param source the first node 
	 * @param target the node to be connected to the first node
	 */
	public void connect(Node source, Node target)
	{
		if(source==null) throw new IllegalArgumentException("Source node must not be null");
		if(target==null) throw new IllegalArgumentException("Target node must not be null");
		Edge e=new Edge(source,target);
		adjacencyMap.get(source).add(e);
		inDegreeMap.get(target).add(e);
//		edges.add(e);
	}

	/**
	 * Connect two nodes. 
	 * @param source the first node 
	 * @param target the node to be connected to the first node
	 */
	public void connect(Edge edge)
	{
		if(edge==null) throw new IllegalArgumentException("Edge must not be null");
		if(edge.getSource()==null) throw new IllegalArgumentException("Source node must not be null");
		if(edge.getTarget()==null) throw new IllegalArgumentException("Target node must not be null");
		adjacencyMap.get(edge.getSource()).add(edge);
		inDegreeMap.get(edge.getTarget()).add(edge);
	}

	/**
	 * Remove edge connecting nodes source and target.
	 * @param source 
	 * @param target
	 */
	public void removeEdge(Node source, Node target)
	{
		//adjacencyMap.get(source).remove(new Edge(source,target));
		Iterator<Edge> iter=adjacencyMap.get(source).iterator();
		while(iter.hasNext())
		{
			Edge e=iter.next();
			if(e.getTarget().equals(target)) 
			{
				adjacencyMap.get(source).remove(e); 
				inDegreeMap.get(target).remove(e); 
//				edges.remove(e);
				return;
			}
		}
	}
		
	/**
	 * Remove an edge 
	 * @param source 
	 * @param target
	 */
	public void removeEdge(Edge e)
	{
 		adjacencyMap.get(e.source).remove(e);
		if(e.getTarget()!=null) 
		{
			adjacencyMap.get(e.source).remove(e);
			inDegreeMap.get(e.getTarget()).remove(e);
		}
	}

	/**
	 * Remove edge connecting nodes source and target.
	 * @param source 
	 * @param edge
	 */
	@Deprecated
	public void removeEdge(Node source, Edge edge)
	{
		adjacencyMap.get(source).remove(edge);				
		if(edge.getTarget()!=null) 
		{
			inDegreeMap.get(edge.getTarget()).remove(edge);
		}
	}

	/**
	 * Removes a node from the graph.
	 * @param node The node to be removed.
	 */
	public void removeNode(Node node)
	{
		for(LinkedList<Edge> list:adjacencyMap.values())
		{
			Iterator<Edge> iter=list.iterator();
			while(iter.hasNext())
			{
				Edge e=iter.next();
				if(e.getTarget().equals(node)) 
				{
					iter.remove();
				}
			}
		}
		for(LinkedList<Edge> list:inDegreeMap.values())
		{
			Iterator<Edge> iter=list.iterator();
			while(iter.hasNext())
			{
				Edge e=iter.next();
				if(e.getSource().equals(node)) 
				{
					iter.remove();
				}
			}
		}		
//		for(Edge e: adjacencyMap.get(node)) 
//		{
//			Node target = e.getTarget();
//			if(target!=null) 
//			{
//				int newInDegree=inDegreeMap.get(target)-1;
//				inDegreeMap.put(target, newInDegree);
//			}
//		}		
		adjacencyMap.remove(node);
		inDegreeMap.remove(node);
	}
	
	/**
	 * Reverse, i.e. exchange the source and target of the edge. 
	 * @param e The edge to be reversed. 
	 * @return The new, i.e. reverted edge. 
	 */
	public Edge reverseEdge(Edge e)
	{
		removeEdge(e);
		Node t=e.getTarget();
		e.setTarget(e.getSource());
		e.setSource(t);
		connect(e);
		return e; 
	}

	/**
	 * Finds the first occurrence of a node with a given name  in a graph
	 * @param name The node name to search for
	 * @return The node, if such a node exists, or null
	 */
	public Node findNode(String name)
	{
		for(Node n:adjacencyMap.keySet())
		{
			if(n.getName().equals(name)) return n;
		}		
		return null;
	}

	/**
	 * Finds the first occurrence of an edge with a given name in a graph
	 * @param name The edge name to search for
	 * @return The node, if such a node exists, or null
	 */
	public Edge findEdge(String name)
	{
		for(LinkedList<Edge> list:adjacencyMap.values())
		{
			Iterator<Edge> iter=list.iterator();
			while(iter.hasNext())
			{
				Edge e=iter.next();
				if(e.getName().equals(name)) 
				{
					return e;
				}
			}
		}
		return null;
	}


	/**
	 * Get the set of the nodes in the graph.
	 * @return Set of nodes in the graph
	 */
	public Set<Node> getNodes()
	{
		return adjacencyMap.keySet();
	}

	/**
	 * For the set of the nodes in the graph, return an iterator.
	 * @return
	 */
	public Iterator<Node> getNodesIterator()
	{
		return adjacencyMap.keySet().iterator();
	}

	/**
	 * Get all edges in the graph
	 * @return A set of all edges.
	 */
	public Set<Edge> getEdges()
	{
		Set<Edge> res=new HashSet<Edge>();
		for(List<Edge> e:adjacencyMap.values())
		{
			res.addAll(e);
		}
		return res;
	}
	
	/**
	 * Get a certain Edge defined by source and target node. 
	 * @return The edge in question, or null if no such edge exists. 
	 */
	public Edge getEdge(Node source, Node target)
	{
		for(Edge e:adjacencyMap.get(source))
		{
			if(e.getTarget()==target)
				return e;
		}
		return null;
	}
	
	/**
	 * Get all edges emerging from a specific node
	 * @return A set of all edges emerging from node.
	 */
	public LinkedList<Edge> getEdges(Node n)
	{
		return adjacencyMap.get(n);
	}
	
	/**
	 * Returns all edges that are leaving  the current node (<code>Node n</code> is source of the edge)
	 * @param n A node contained in the graph
	 * @return All outgoing edges from n
	 */
	public Set<Edge> getOutEdges(Node n)
	{
		if(n==null)
			return Collections.emptySet();
		Set<Edge> res=new HashSet<Edge>();
		for(Edge e:adjacencyMap.get(n))
		{
			res.add(e);
		}
		return res;
	}
	
	public Set<Edge> getAllEdges(Node n)
	{
		Set<Edge> res=new HashSet<Edge>();
		res.addAll(getInEdges(n));
		res.addAll(getOutEdges(n));
		
		return res;
	}
	
	/**
	 * Returns all edges that arrive at the current node (<code>Node n</code> is target of the edge)
	 * @param n A node contained in the graph
	 * @return All incoming edges from n
	 */
	public Set<Edge> getInEdges(Node n)
	{
		Set<Edge> res=new HashSet<Edge>();
		for(LinkedList<Edge> l:adjacencyMap.values())
		{
			for(Edge e: l)
			{
				if(e.target==n)
				{
					res.add(e);
				}
			}			
		}
		return res;
	}

	/**
	 * Get an iterator for all edges in the graph
	 * @return An iterator for the set of all edges.
	 */
	public Iterator<Edge> getEdgesIterator()
	{
		return getEdges().iterator();
	}
	
	/**
	 * The number of edges leaving the node
	 * @param node
	 * @return
	 */
	public int getOutDegree(Node node)
	{
		return adjacencyMap.get(node).size();
	}
	
	/**
	 * The number of edges arriving the node
	 * @param node
	 * @return
	 */
	public int getInDegree(Node node)
	{	/*
		int res=0;
		for(Node n:adjacencyMap.keySet())
		{
			for(Edge e:adjacencyMap.get(n))
			{
				if(e.getTarget().equals(node)) 
				{
					res++;				
				}
			}
		}
		return res;
		*/
		if(inDegreeMap.containsKey(node)) {
			return inDegreeMap.get(node).size();
		}
		else {
			throw new IllegalArgumentException("Node not in graph");
		}
	}
	
	/**
	 * The number of all edges either starting or ending at a node.
	 * @param node
	 * @return
	 */
	public int getDegree(Node node)
	{
		return getInDegree(node)+getOutDegree(node);
	}
	
	/**
	 * Returns  the set of nodes a node is connected with.
	 * @param n A node in the graph.
	 * @return the set of nodes a node is connected with or null if the node has no neighbors or is not present in the graph.
	 */
	public Set<Node> getNeighbors(Node n)
	{
		if(!adjacencyMap.containsKey(n))
		{
			return null;
		}
		Set<Node> res=new HashSet<Node>();
		for(Edge e:adjacencyMap.get(n))
		{
			res.add(e.getTarget());
		}
		for(LinkedList<Edge> l:adjacencyMap.values())
		{
			for(Edge e: l)
			{
				if(e.target==n)
				{
					res.add(e.getSource());
				}
			}			
		}
		return res;
	}
	
	/**
	 * Returns all nodes that are connected to the current node by an outgoing edge (<code>Node n</code> is source of the edge)
	 * @param n A node contained in the graph
	 * @return All nodes connected to n by an outgoing edge.
	 */
	public Set<Node> getOutNeighbors(Node n)
	{
		Set<Node> res=new HashSet<Node>();
		for(Edge e:adjacencyMap.get(n))
		{
			res.add(e.getTarget());
		}
		return res;
	}
	
	/**
	 * Returns all nodes that are connected to the current node by an incoming edge (<code>Node n</code> is target of the edge)
	 * @param n
	 * @return
	 */
	public Set<Node> getInNeighbors(Node n)
	{
		Set<Node> res=new HashSet<Node>();
		for(Edge e: inDegreeMap.get(n))
		{
			res.add(e.getSource());
		}
		
//		for(LinkedList<Edge> l:adjacencyMap.values())
//		{
//			for(Edge e: l)
//			{
//				if(e.target==n)
//				{
//					res.add(e.getSource());
//				}
//			}			
//		}
		return res;
	}
	
	/**
	 * Remove nodes with no edges from the graph. 
	 */
	public void removeOrphans()
	{
		Iterator<Node> iter=adjacencyMap.keySet().iterator();
		List<Node> remove=new LinkedList<Node>();
		while(iter.hasNext())
		{
			Node n=iter.next();
			if(getDegree(n)==0)
			{
				remove.add(n);
			}
		}
		for(Node n: remove)
			removeNode(n);
		
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Graph clone()
	{
		Graph clone=new Graph();
//		if you think that something like: 
//		clone.adjacencyMap=(HashMap<Node,LinkedList<Edge>>)adjacencyMap.clone();
//		would replicate all nodes, forget it. 
//		we have to do the following:
		Map<Node, Node> oldToNewNode =new HashMap<Node, Node>();
		for(Node n: getNodes())
		{
			Node nnew=(Node)n.clone();
			nnew.setGraph(clone);
			clone.addNode(nnew);
			oldToNewNode.put(n, nnew);
		}
		for(Edge e:getEdges())
		{
			Edge eNew=(Edge)e.clone();
			eNew.setSource(oldToNewNode.get(e.getSource()));
			eNew.setTarget(oldToNewNode.get(e.getTarget()));
			clone.connect(eNew);
		}
		return clone;
	}
	
	
	/**
	 * Returns a Set of Nodes that match the requested role.
	 * @param role
	 * @return
	 */
	public Set<Node> getNodes(String role)
	{
		Set<Node> res=new HashSet<Node>();
		for(Node node:getNodes())
		{
			if(node.getRole().equals(role))
				res.add(node);
		}
		return res;
	}
	
	/**
	 * Adds an entire graph to the graph, including all nodes and edges. The 
	 * additional graph is not connected with any component of the existing graph.  
	 * @param g
	 */
	public void add(Graph g)
	{
		for(Node n:g.getNodes())
			addNode(n);
		for(Edge e:g.getEdges())
			connect(e);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Tests whether the graph contains the node
	 * @param A node
	 * @return true, if the graph contains the node, false otherwise. 
	 */
	public boolean contains(Node n)
	{
		return adjacencyMap.keySet().contains(n);
	}

	
	public boolean isConnected(Node n, Node m)
	{
		for(Edge e:adjacencyMap.get(n))
		{
			if(e.getTarget().equals(m))
				return true;
		}
		return false;		
	}
	
	/**
	 * Exports the graph to GraphML, including all components. 
	 * @param writer
	 * @throws Exception
	 */
	public void export(XMLStreamWriter writer) throws Exception
	{
		writer.writeStartElement("graph");
		writer.writeAttribute("id", "graph"+hashCode());		
		writer.writeAttribute("edgedefault", "directed");
		
		GraphMLExport.writeDataElement(writer, GraphMLExport.CLASS_KEY, getClass().getCanonicalName());
		if(name!=null)
		{
			GraphMLExport.writeDataElement(writer, GraphMLExport.NAME_KEY, getName());
		}
		for(Node n:getNodes())
		{
			n.export(writer);			
		}
		writer.flush();
		for(Edge ed:getEdges())
		{
			ed.export(writer);
		}
		writer.writeEndElement();
	}
	
	/**
	 * Just write the head of the graph into the graphML file. Omit the nodes and edges. 
	 * @param writer
	 * @throws Exception
	 */
	public void exportGraphHead(XMLStreamWriter writer) throws Exception
	{
		writer.writeStartElement("graph");
		writer.writeAttribute("id", "graph"+hashCode());		
		writer.writeAttribute("edgedefault", "directed");
		
		GraphMLExport.writeDataElement(writer, GraphMLExport.CLASS_KEY, getClass().getCanonicalName());
		if(name!=null)
		{
			GraphMLExport.writeDataElement(writer, GraphMLExport.NAME_KEY, getName());
		}

	}
	
}
