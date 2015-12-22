package mayday.core.probelistmanager.models;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


@SuppressWarnings("serial")
public class ListSelectionModelFromTreeSelectionModel
extends DefaultListSelectionModel
implements ListSelectionModel, TreeSelectionListener
{

	protected TreeSelectionModel selectionModel;
	protected TreeModelWithListModel model;
	protected boolean silent = false;
	
	public ListSelectionModelFromTreeSelectionModel(TreeSelectionModel selectionModel, TreeModelWithListModel model) {
		this.model = model;
		this.selectionModel = selectionModel;
		setSelectionMode(MULTIPLE_INTERVAL_SELECTION);
		sync_back();
	}

	public void clearSelection() {
		super.clearSelection();
		sync();
	}

	protected void sync() {
		if (silent)
			return;
		selectionModel.clearSelection();
		for (int index=getMinSelectionIndex(); index<=getMaxSelectionIndex(); ++index) {
				if (index > -1 && super.isSelectedIndex(index))
				selectionModel.addSelectionPath(indexToPath(index));
		}
	}

	protected void sync_back() {
		silent = true;
		super.clearSelection();
		TreePath[] tps = selectionModel.getSelectionPaths();
		for(TreePath tp : tps) {
			int index = pathToIndex(tp);
			this.addSelectionInterval(index, index);
		}			
		silent = false;
	}

	public int pathToIndex(TreePath path) {
		int index = model.internal_index_of(((DefaultMutableTreeNode)path.getLastPathComponent()));
		return index;
	}

	public void insertIndexInterval(int index, int length, boolean before) {
		super.insertIndexInterval(index, length, before);
		sync();
	}

	protected DefaultMutableTreeNode indexToNode(int index) {
		Object o = model.getElementAt(index);
		return model.nodeOf(o, (DefaultMutableTreeNode)model.getRoot());
	}

	protected TreePath indexToPath(int index) {
		return new TreePath(indexToNode(index).getPath());	
	}

	public boolean isSelectedIndex(int index) {		
		return selectionModel.isPathSelected(indexToPath(index));
	}

	public boolean isSelectionEmpty() {
		return selectionModel.isSelectionEmpty();
	}

	public void removeIndexInterval(int index0, int index1) {
		super.removeIndexInterval(index0, index1);
		sync();
	}

	public void removeSelectionInterval(int index0, int index1) {
		super.removeSelectionInterval(index0, index1);
		sync();
	}

	public void setSelectionInterval(int index0, int index1) {
		super.setSelectionInterval(index0, index1);
		sync();		
	}


	public void valueChanged(TreeSelectionEvent e) {
		sync_back();
	}

	public void addSelectionInterval(int index0, int index1) {
		super.addSelectionInterval(index0, index1);
		sync();
	}

	public void setAnchorSelectionIndex(int index) {
		super.setAnchorSelectionIndex(index);
		sync();	
	}

	public void setLeadSelectionIndex(int index) {
		super.setLeadSelectionIndex(index);
		sync();	
	}

}