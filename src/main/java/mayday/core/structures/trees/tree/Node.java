package mayday.core.structures.trees.tree;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import mayday.core.structures.trees.io.PlainNewick;

/**
 * Represents a Node of a tree with label, parent-Edge
 * and left and right Edge
 * @see Edge
 * @author Andreas Friedrich, Michael Borner
 */
public class Node implements ITreePart, Comparable<Node> {
	
	protected String label;
	protected ArrayList<Edge> edges = new ArrayList<Edge>();
	
	/**
	 * Creates a new Node object
	 * @param label the name of the Node
	 * @param leftedge the left Edge of this Node leading to its left hild Node
	 * @param rightedge the right Edge of this Node leading to its right hild Node
	 * @param parentedge the parent Edge of this Node leading to its parent Node
	 */
	public Node(String label, Edge parentEdge, Edge... childEdges){
		this.label = label;
		if (parentEdge!=null)
			edges.add(parentEdge);
		for (Edge e : childEdges)
			edges.add(e);
	}
	
	public Collection<Edge> getEdges() {
		return Collections.unmodifiableList(edges);
	}
	
//	/**
//	 * Returns the number of Edges on the longest path from Node to leaf
//	 * @return largest depth from Node to leaf
//	 */
//	public int depth() {
//		if(this.leftedge == null)
//			return 0;
//		else {
//			int r = 1+this.leftedge.getChild().depth();
//			int l = 1+this.rightedge.getChild().depth();
//			return Math.max(r, l);
//		}
//	}
	/**
	 * Returns the sum of Edge lengths on the longest path from Node to leaf
	 * @return sum of largest Edge lengths from Node to leaf
	 */
	public double distanceToGround(Edge incomingEdge) {
		if (isLeaf())
			return 0;
		double dtg = Double.NEGATIVE_INFINITY;
		for (Edge e : edges)
			if (e!=incomingEdge)
				dtg = Math.max(dtg, e.getLength()+e.getOtherNode(this).distanceToGround(e));
		return dtg;
	}
	
	/**
	 * Returns the sum of Edge lengths on the longest path from Node to leaf
	 * @return sum of largest Edge lengths from Node to leaf
	 */
	public int edgesToGround(Edge incomingEdge) {
		if (isLeaf())
			return 0;
		int dtg = Integer.MIN_VALUE;
		for (Edge e : edges)
			if (e!=incomingEdge)
				dtg = (int)Math.max(dtg, 1+e.getOtherNode(this).edgesToGround(e));
		return dtg;
	}
	
//	/**
//	 * Returns the length of the longest Edge which is a descendant of this Node
//	 * @return length of the longest Edge
//	 */
//	public double longestEdgeLength() {
//		if(this.leftedge == null)
//			return 0;
//		else {
//			double r = Math.max(this.rightedge.getLength(),
//								this.rightedge.getChild().longestEdgeLength());
//			double l = Math.max(this.leftedge.getLength(),
//					this.leftedge.getChild().longestEdgeLength());
//			return Math.max(r, l);
//		}
//	}
	
	public void addEdge(Edge e) {
		edges.add(e);
	}
	
	/**
	 * Fills ArrayList with Nodes of the tree in postorder traversal
	 * @return ArrayList of Nodes in postorder
	 */
	public ArrayList<Node> postorderNodeList(Edge incomingEdge) {
		ArrayList<Node> n = new ArrayList<Node>();
		postorderNodeListRec(incomingEdge, n);
		return n;
	}
	
	//recursive helper
	private void postorderNodeListRec(Edge incomingEdge, ArrayList<Node> nodes) {
		for (Edge e : edges)
			if (e!=incomingEdge)
				e.getOtherNode(this).postorderNodeListRec(e, nodes);
		nodes.add(this);
	}
	
	
	
	public ArrayList<Edge> postorderEdgeList() {
		ArrayList<Edge> ee = new ArrayList<Edge>();
		for (Edge e : edges)
			e.postorderEdgeListRec(this, ee);
		return ee;
	}
	/**
	 * Fills ArrayList with descending Edges of this Node in postorder traversal
	 * @return ArrayList of Edges in postorder
	 */

		
	public void setLabel(String l){
		this.label = l;
	}
	
	/** @Deprecated Use ScreenLayout.getLabel(Node) whenever possible */
	public String getLabel(){
		return this.label;
	}
	
	public String toString() {
		if (label.length()==0)
			return ""+hashCode();
		return this.label;
	}
	
	public int numberOfDescendants(Edge incomingEdge) {
		int nod = 0;
		for (Edge e : edges) {
			if (e!=incomingEdge)
				nod += e.numberOfDescendants(this);
		}
		return nod+1; // account for myself
	}
	
	public int numberOfDescendantLeaves(Edge incomingEdge) {
		if (isLeaf())
			return 1;
		int nod = 0;
		for (Edge e : edges) {
			if (e!=incomingEdge)
				nod += e.getOtherNode(this).numberOfDescendantLeaves(e);
		}
		return nod;
	}


	protected void collectLeaves(Edge incomingEdge, Collection<Node> leaves) {
		if (isLeaf())
			leaves.add(this);
		else
			for (Edge e : edges)
				if (e!=incomingEdge) 
					e.getOtherNode(this).collectLeaves(e, leaves);
	}
	
	/** returns all leaves ordered by tree structur */ 
	public Collection<Node> getLeaves(Edge incomingEdge) {
		Collection<Node> result = new LinkedList<Node>();
		collectLeaves(incomingEdge, result);
		return result;
	}
	
	public boolean isLeaf() {
		return edges.size()<=1;
	}
	
	public boolean isRoot() {
		for (Edge e: edges)
			if (e.getNode(0)!=this)
				return false;
		return true;
	}

	
	protected void turnEdges(Edge incomingEdge) {
		for (Edge e : edges) {
			if (e!=incomingEdge) {
				if (e.getNode(0)!=this) 
					e.turn();
				e.getOtherNode(this).turnEdges(e);
			}
		}
		// make incoming edge the root edge
		if (edges.get(0).getNode(1)!=this) {
			for (int i=1; i<edges.size(); ++i) { 
				if (edges.get(i).getNode(1)==this) {
					swapChildren(0, i);
					break;
				}
			}
		}
	}
	
	public void makeRoot() {
		turnEdges(null);
	}

	public int compareTo(Node o) {
		if (this==o)
			return 0;
//		int c = label.compareTo(o.getLabel());
//		if (c==0)
//			c = (int)Math.signum(o.edges.size()-edges.size());
//		if (c==0)
//			
		return new Integer(hashCode()).compareTo(o.hashCode());  
	}
	
	public void addParentEdge(Edge e) {
		edges.add(0, e);
	}
	
	public String toNewick() {
		return new PlainNewick().serialize(this);
	}

	public void swapChildren(int i1, int i2) {
		Edge e = edges.get(i1);
		edges.set(i1, edges.get(i2));
		edges.set(i2, e);
	}

	public void replaceEdge(Edge oldEdge, Edge newEdge) {
		int i= edges.indexOf(oldEdge);
		if (i>=0) {
			edges.remove(i);
			edges.add(i,newEdge);
		} 
		else 
			edges.add(newEdge);
		
	}

}