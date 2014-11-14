/*
 * created on Nov 27, 2007
 * modified by Guenter Jaeger on April 22, 2010
 */

package mayday.genemining2.cng;

import java.util.ArrayList;
import java.util.List;

import mayday.core.ClassSelectionModel;
import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.Node;

/**
 * @author Thomas Loeffler
 */
public class Bipartition {
	List<String> leafList1 = new ArrayList<String>();
	List<String> leafList2 = new ArrayList<String>();
	Node node;

	/**
	 * Default constructor
	 */
	public Bipartition() {
	}

	/**
	 * @param bipartition
	 */
	public Bipartition(Bipartition bipartition) {
		this.clear();
		this.leafList1 = bipartition.leafList1;
		this.leafList2 = bipartition.leafList2;
		this.node = bipartition.node;
	}

	/**
	 * @param tree
	 */
	public void set(Node tree) {
		this.clear();
		if (tree == null) {
			return;
		}

		for (Node n : tree.getLeaves(null))
			leafList1.add(n.getLabel());
	}

	/**
	 * @param tree
	 * @param partitioner
	 */
	public void set(Node tree, Node partitioner) {
		this.clear();
		if (partitioner == null || partitioner.numberOfDescendants(null) < 2) {
			return;
		}

		// assume binary trees, this is the edge going INTO partitioner
		Edge splitEdge = partitioner.getEdges().iterator().next();

		for (Node n : partitioner.getLeaves(splitEdge))
			leafList1.add(n.getLabel());

		for (Node n : splitEdge.getOtherNode(partitioner).getLeaves(splitEdge))
			leafList2.add(n.getLabel());

		node = partitioner;
	}

	/**
	 * @return incoming branch length
	 */
	public double getIncomingBranchLength() {
		return node.getEdges().iterator().next().getLength();
	}

	/**
	 * @param model
	 */
	public void set(ClassSelectionModel model) {
		this.clear();
		int size = model.getNumObjects();
		String class1 = model.getClassesLabels().get(0);
		for (int i = 0; i != size; ++i) {
			String partition = model.getPartition().get(i);
			String name = model.getObjectName(i);
			if (partition.equals(class1)) {
				this.leafList1.add(name);
			} else {
				this.leafList2.add(name);
			}
		}

	}

	/**
	 * clear leaf lists and node
	 */
	public void clear() {
		this.leafList1.clear();
		this.leafList2.clear();
		this.node = null;
	}

	/**
	 * @return true is node == null, else false
	 */
	public boolean isCleared() {
		return this.node == null;
	}

	/**
	 * @return first leaf list
	 */
	public List<String> getLeafList1() {
		return this.leafList1;
	}

	/**
	 * @return second leaf list
	 */
	public List<String> getLeafList2() {
		return this.leafList2;
	}

	/**
	 * @return node
	 */
	public Node getNode() {
		return this.node;
	}

	/**
	 * 
	 * @param nodeName
	 *            , the name of the node
	 * @return the number (1 or 2) of the list containing node. 0 if node is not
	 *         contained in any list.
	 */
	public int containsInList(String nodeName) {
		for (int i = 0; i != this.leafList1.size(); ++i) {
			if (this.leafList1.get(i).equals(nodeName)) {
				return 1;
			}
		}
		for (int i = 0; i != this.leafList2.size(); ++i) {
			if (this.leafList2.get(i).equals(nodeName)) {
				return 2;
			}
		}
		return 0;
	}

	/**
	 * @param tree
	 * @return bipartition from node
	 */
	public Bipartition searchInTree(Node tree) {

		Bipartition bipartition = new Bipartition();

		for (Node n : tree.postorderNodeList(null)) {
			if (!n.isLeaf()) {
				bipartition.set(tree, n);
				if (this.equals(bipartition)) {
					return bipartition;
				}
			}
		}

		for (Node n : tree.postorderNodeList(null)) {
			if (!n.isLeaf()) {
				bipartition.set(tree, n);
				if (this.equals1(bipartition)) {
					return bipartition;
				}
			}
		}

		return null;
	}

	/**
	 * @param bipartition
	 * @return true , if this equals bipartition, else false
	 */
	public boolean equals(Bipartition bipartition) {
		return (this.leafList1.size() == bipartition.leafList1.size()
				&& this.leafList2.size() == bipartition.leafList2.size()
				&& this.leafList1.containsAll(bipartition.leafList1) && this.leafList2
				.containsAll(bipartition.leafList2))
				|| (this.leafList1.size() == bipartition.leafList2.size()
						&& this.leafList2.size() == bipartition.leafList1
								.size()
						&& this.leafList1.containsAll(bipartition.leafList2) && this.leafList2
						.containsAll(bipartition.leafList1));
	}

	/**
	 * @param bipartition
	 * @return true , if this equals bipartition except for one element, else false
	 */
	public boolean equals1(Bipartition bipartition) {
		return (this.leafList1.size() == bipartition.leafList1.size() + 1
				&& this.leafList2.size() == bipartition.leafList2.size() - 1
				&& this.leafList1.containsAll(bipartition.leafList1) && bipartition.leafList2
				.containsAll(this.leafList2))
				||

				(this.leafList1.size() == bipartition.leafList1.size() - 1
						&& this.leafList2.size() == bipartition.leafList2
								.size() + 1
						&& bipartition.leafList1.containsAll(this.leafList1) && this.leafList2
						.containsAll(bipartition.leafList2))
				||

				(this.leafList2.size() == bipartition.leafList1.size() + 1
						&& this.leafList1.size() == bipartition.leafList2
								.size() - 1
						&& this.leafList2.containsAll(bipartition.leafList1) && bipartition.leafList2
						.containsAll(this.leafList1))
				||

				(this.leafList2.size() == bipartition.leafList1.size() - 1
						&& this.leafList1.size() == bipartition.leafList2
								.size() + 1
						&& bipartition.leafList1.containsAll(this.leafList2) && this.leafList1
						.containsAll(bipartition.leafList2));
	}
	
	@Override
	public String toString() {
		String str = "List 1: ";
		for (int i = 0; i != this.leafList1.size(); ++i) {
			str = str + this.leafList1.get(i);
			if (i + 1 != leafList1.size()) {
				str = str + ", ";
			}
		}
		str = str + "\nList 2: ";
		for (int i = 0; i != this.leafList2.size(); ++i) {
			str = str + this.leafList2.get(i);
			if (i + 1 != leafList2.size()) {
				str = str + ", ";
			}
		}
		str = str + "\nNode: " + this.node.toString();
		return str;
	}
}
