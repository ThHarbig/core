package mayday.core.probelistmanager.models;

import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.swing.tree.TreePath;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.probelistmanager.MasterTableProbeList;
import mayday.core.probelistmanager.UnionProbeList;
import mayday.core.probelistmanager.gui.ProbeListNode;

@SuppressWarnings("serial")
public class ProbeListTreeListModel extends TreeModelWithListModel {

	public ProbeListTreeListModel(DataSet ds) {
		super(new ProbeListNode(new MasterTableProbeList(ds)));
		((MasterTableProbeList)getRoot().getProbeList()).setNode(getRoot());
	}
	
	public ProbeListNode getRoot() {
		return (ProbeListNode)super.getRoot();
	}
	
	public ProbeListNode nodeOf(Object pl, ProbeListNode startNode) {
		return (ProbeListNode)super.nodeOf(pl, startNode);
	}

	protected void checkSanity(ProbeListNode parentNode, ProbeListNode probeListNode) {
		// Sanity check: Parent may not be a child of the intended child
		TreePath parentPath = new TreePath(parentNode.getPath());
		TreePath probeListPath = new TreePath(probeListNode.getPath());
		if (probeListPath.isDescendant(parentPath)) {
			throw new RuntimeException("Insanity requested: Circular path in a tree is not allowed.");
		}
	}
	
	protected void insertProbeList(ProbeList probeList, ProbeList parent, 
			ProbeListNode probeListNode, ProbeListNode parentNode, int index) {

		checkSanity(parentNode, probeListNode);
		
		// INSERTING has to be done via the methods provided by the treemodel		
		insertNodeInto(probeListNode, parentNode, index);
		
		probeList.setParent((UnionProbeList)parent);
		
		if (probeList instanceof UnionProbeList)
			((UnionProbeList)probeList).setNode(probeListNode);
		
		UnionProbeList parentUPL = (UnionProbeList)parent;
		parentUPL.childrenChanged();
	}
	
	
	public void insertProbeList(ProbeListNode probeListNode, ProbeListNode parentNode, int index) {
		ProbeList probeList, parent;
		if (parentNode==null) {
			parentNode = (ProbeListNode)getRoot();
		}
		parent = (ProbeList)parentNode.getUserObject();
		probeList = (ProbeList)probeListNode.getUserObject();
		
		insertProbeList(probeList, parent, probeListNode, parentNode, index);
	}
	
	
	
	public ProbeListNode insertProbeList(ProbeList probeList, ProbeList parent, int index) {
		ProbeListNode parentNode, probeListNode;
		
		if (parent==null) {
			parentNode = getRoot();
			parent = parentNode.getProbeList();
		} else {
			parentNode = nodeOf(parent, getRoot());
		}
		
		probeListNode = new ProbeListNode(probeList);
		
		insertProbeList(probeList, parent, probeListNode, parentNode, index);
		
		return probeListNode;
	}

	
	public void moveProbeList(ProbeList probeList, ProbeList newParent, int newIndex) {
		
		ProbeListNode newParentNode;
		ProbeListNode probeListNode;
		
		probeListNode = nodeOf(probeList, getRoot());
		
		if (newParent==null) {
			newParentNode = getRoot();
			newParent = newParentNode.getProbeList();
		} else {
			newParentNode = nodeOf(newParent, getRoot());
		}

		moveNode(probeListNode, newParentNode, newIndex);
	}
	
	
	public void moveNode(ProbeListNode childNode, ProbeListNode newParentNode, int newIndex) {
		checkSanity(newParentNode, childNode);		
		removeProbeListNodeFromParent(childNode);
		insertProbeList(childNode, newParentNode, newIndex);
	}
	
	/** remove a probelist from the model
	 * @param probeList probelist to remove
	 * @return collection of all probelists that were removed as a consequence of this action
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProbeList> removeProbeList(ProbeList probeList) {
		LinkedList<ProbeList> victims = new LinkedList<ProbeList>();
		victims.add(probeList);
		
		ProbeListNode probeListNode = nodeOf(probeList, getRoot());
		if (probeListNode!=null) {
			Enumeration<ProbeListNode> theChildren = probeListNode.children();
			while (theChildren.hasMoreElements()) {
				ProbeListNode child = theChildren.nextElement();
				if (child.getProbeList()!=null)
					victims.add(child.getProbeList());
			}
			// if leaf, remove
			removeProbeListNodeFromParent(probeListNode);
		}

		return victims;
	}
	
	public void replaceProbeList(ProbeList old, ProbeList replacement) {
		ProbeListNode plNode = nodeOf(old, getRoot());
		ProbeListNode parentNode = plNode.getParent();
		// can only replace leaves !
		if (!plNode.isLeaf())
			throw new RuntimeException("Can only replace leaves in the tree");
		plNode.setProbeList(replacement);
		replacement.setParent((UnionProbeList)parentNode.getProbeList());
		((UnionProbeList)parentNode.getProbeList()).childrenChanged();
		// update the projection
		int index = projectedModel.indexOf(old);
		projectedModel.set(index, replacement);
	}
	
	public void clear() {
		getRoot().removeAllChildren();
	}

	public void removeProbeListNodeFromParent(ProbeListNode n) {
		ProbeListNode pnode = n.getParent();
		UnionProbeList upl = ((UnionProbeList)pnode.getProbeList());
		removeNodeFromParent(n);
		upl.childrenChanged();
	}	
}
