package mayday.core.probelistmanager.models;

import java.util.Enumeration;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;


@SuppressWarnings("serial")
public class TreeModelWithListModel 
extends DefaultTreeModel
implements ListModel, TreeModel
{
	
	protected DefaultListModel projectedModel;	

	public TreeModelWithListModel(DefaultMutableTreeNode root) {
		super(root, false);
		projectedModel = new DefaultListModel();
		projectedModel.add(0, root.getUserObject());
	}	

	// listmodel
	public void addListDataListener(ListDataListener arg0) {
		projectedModel.addListDataListener(arg0);		
	}

	public void removeListDataListener(ListDataListener arg0) {
		projectedModel.removeListDataListener(arg0);
	}

	@SuppressWarnings("unchecked")
	protected void checkModelSynchronicity() {
		Enumeration<DefaultMutableTreeNode> nodes = ((DefaultMutableTreeNode)getRoot()).preorderEnumeration();
		int lmIndex=0;
		boolean bug=false;
		while(nodes.hasMoreElements()) {
			Object treeo = nodes.nextElement().getUserObject();
			Object listo = lmIndex<projectedModel.size()?projectedModel.get(lmIndex):"--missing--";
			bug|=(treeo!=listo);
			++lmIndex;
		}
		if (bug) {
			System.err.println("Models mismatch");
			System.err.println(this);
			JOptionPane.showMessageDialog(null, "Mayday's internal data structure has become corrupted. Please file a bug!");
		}			
	}
	
	@SuppressWarnings("unchecked")
	public String toString() {
		Enumeration<DefaultMutableTreeNode> nodes = ((DefaultMutableTreeNode)getRoot()).preorderEnumeration();
		int lmIndex=0;
		String s="";
		while(nodes.hasMoreElements()) {
			Object treeo = nodes.nextElement().getUserObject();
			Object listo = lmIndex<projectedModel.size()?projectedModel.get(lmIndex):"--missing--";
			s+=treeo+" = "+listo+"\n";
			++lmIndex;
		}
		return s;
	}
	
	// listmodel
	public Object getElementAt(int arg0) {
		return projectedModel.getElementAt(arg0);
	}

	// listmodel
	public int getSize() {		
		return projectedModel.getSize();
	}
	
	public Object[] toArray() {
		return projectedModel.toArray();
	}
	

	@SuppressWarnings("unchecked")
	public DefaultMutableTreeNode nodeOf(Object pl, DefaultMutableTreeNode startNode) {
		Enumeration<DefaultMutableTreeNode> nodes = startNode.preorderEnumeration();
		while(nodes.hasMoreElements()) {
			DefaultMutableTreeNode node = nodes.nextElement();
			if (node.getUserObject()==pl)
				return node;
		}
		return null;
	}
			
	
	@SuppressWarnings("unchecked")
	protected int internal_index_of(Object pl) {
		Enumeration<DefaultMutableTreeNode> nodes = ((DefaultMutableTreeNode)getRoot()).preorderEnumeration();
		int index=0;		
		while(nodes.hasMoreElements()) {
			DefaultMutableTreeNode node = nodes.nextElement();
			if (node.getUserObject()==pl)
				return index;
			index++;			
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	protected int internal_index_of(DefaultMutableTreeNode n) {
		Enumeration<DefaultMutableTreeNode> nodes = ((DefaultMutableTreeNode)getRoot()).preorderEnumeration();
		int index=0;		
		while(nodes.hasMoreElements()) {
			DefaultMutableTreeNode node = nodes.nextElement();
			if (node==n)
				return index;
			index++;			
		}
		return -1;
	}


		
	// treemodel
	public void insertNodeInto(MutableTreeNode newChild, MutableTreeNode parent, int index) {
		if (index>parent.getChildCount())
			index = parent.getChildCount();
		if (index<0)
			index = 0;

		super.insertNodeInto(newChild, parent, index);		
		
		insertIntoProjection( (DefaultMutableTreeNode)newChild );
		
		checkModelSynchronicity();
	}

	// treemodel
	public void removeNodeFromParent(MutableTreeNode node) {
		removeNodeFromProjection((DefaultMutableTreeNode)node);
		super.removeNodeFromParent(node);
		checkModelSynchronicity();
	}
	
	@SuppressWarnings("unchecked")
	protected void insertIntoProjection(DefaultMutableTreeNode node) {
		int listIndex = internal_index_of(node);
		projectedModel.insertElementAt(node.getUserObject(), listIndex);
		Enumeration<DefaultMutableTreeNode> c = node.children();		
		while (c.hasMoreElements()) {
			insertIntoProjection(c.nextElement());
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void removeNodeFromProjection(DefaultMutableTreeNode n) {
		Object o = n.getUserObject();

		projectedModel.removeElement(o);

		Enumeration<DefaultMutableTreeNode> c = n.children();
		while (c.hasMoreElements())
			removeNodeFromProjection(c.nextElement());
	}

}
