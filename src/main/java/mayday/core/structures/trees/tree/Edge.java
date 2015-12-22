package mayday.core.structures.trees.tree;

import java.util.ArrayList;

/**
 * Represents a branch of a tree with length, parent-Node
 * and child-Node
 * @see Node
 * @author Andreas Friedrich, Michael Borner
 * @author Florian Battke: Edges are undirected
 */
public class Edge implements ITreePart{
	
	protected double length;
	protected Node[] nodes = new Node[2];
	protected String label;

	public Edge(double length, Node one, Node two) {
		this.length = length;
		nodes[0] = one;
		nodes[1] = two;
	}
	
	public boolean equals(Edge e) {
		if(e==null)
			return false;
		else 
			return length==e.length && ((label==null && e.label==null) || label.equals(e.label))
			&& ((nodes[0]==e.nodes[0] && nodes[1]==e.nodes[1]) || ((nodes[0]==e.nodes[1] && nodes[1]==e.nodes[0])));
	}
	
//	/**
//	 * Checks if this Edge is a left or right Edge
//	 * @return True if this Edge is the left Edge of its parent Node; false otherwise
//	 */
//	public boolean isLeftEdge() {
//		return this.equals(this.getParent().getLeftedge());
//	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String w) {
		label = w;
	}
	
	/**
	 * Computes the number of Nodes which are under this Edge
	 * @return the number of descending Nodes
	 */
	public int numberOfDescendants(Node incomingNode) {
		return getOtherNode(incomingNode).numberOfDescendants(this);
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public Node getOtherNode(Node n) {
		if (nodes[0]==n)
			return nodes[1];
		else return nodes[0];
	}

	public ArrayList<Edge> postorderEdgeList(Node incomingNode) {
		ArrayList<Edge> e = new ArrayList<Edge>();
		postorderEdgeListRec(incomingNode, e);
		return e;
	}
	

	//recursive helper
	protected void postorderEdgeListRec(Node incomingNode, ArrayList<Edge> edges) {
		Node n = getOtherNode(incomingNode);
		for (Edge e : n.edges)
			if (e!=this)
				e.postorderEdgeListRec(n, edges);
		edges.add(this);
	}
	
	public Node getNode(int i) {
		return nodes[i];
	}
	
	public void turn() {
		Node n = nodes[0];
		nodes[0]=nodes[1];
		nodes[1]=n;
	}
	
	public String toString() {
		return nodes[0]+" -- "+nodes[1]+" ("+length+")"+((label!=null)?(":"+label):(""));
	}

}