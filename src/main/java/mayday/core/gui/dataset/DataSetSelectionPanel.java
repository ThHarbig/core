package mayday.core.gui.dataset;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import mayday.core.DataSet;
import mayday.core.StoreEvent;
import mayday.core.StoreListener;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;

@SuppressWarnings("serial")
public class DataSetSelectionPanel extends JTabbedPane implements ChangeListener {

	private DataSetManager dsManager;
	private int lastSelectedTab=0;
	
	private LinkedList<SelectionComponent> selComps = new LinkedList<SelectionComponent>();
	
	public DataSetSelectionPanel() {
		dsManager = DataSetManager.singleInstance;
		selComps.add(new SelectionByList());
		for(SelectionComponent sc : selComps) {
			this.add(sc.toString(), new JScrollPane(sc.getComponent()));
		}
		this.addChangeListener(this);
		populate();
	}
	
	public void setFilter(DataSetSelectionFilter plf) {
		for (SelectionComponent sc : selComps)
			sc.setFilter(plf);
		populate();
	}
	
	private void populate() {
		for(SelectionComponent sc : selComps) {
			sc.populate(dsManager);
		}
	}
	
	// copy current selection on tab change
	public void stateChanged(ChangeEvent arg0) {
		if (this.getSelectedIndex()!=lastSelectedTab) {
			List<DataSet> sel = selComps.get(lastSelectedTab).getSelectedDSs();
			for (SelectionComponent sc : selComps)
				sc.setSelectedDSs(sel);
			lastSelectedTab = this.getSelectedIndex();
		}
	}
	
	public List<DataSet> getSelection() {
		return selComps.get(lastSelectedTab).getSelectedDSs(); 
	}
	
	public int getSelectableCount() {
		return selComps.get(lastSelectedTab).getSelectableCount();
	}
	
	public void removeNotify() {
		for (SelectionComponent sc : selComps) {
			sc.dispose();
		}
	    super.removeNotify();
	  }
	
	public void addInternalMouseListener(MouseListener ml) {
		for (SelectionComponent sc : selComps) {
			sc.getComponent().addMouseListener(ml);
		}		
	}

	
	
	// the parent class for different kind of mio group lists (tree, by type, by class...)
	private abstract static class SelectionComponent implements StoreListener {
		
		protected DataSetManager dsManager;
		protected DataSetSelectionFilter filter;
		
		public abstract String toString();

		public void setFilter(DataSetSelectionFilter plf) {
			filter = plf;
		}
		
		public void populate(DataSetManager plm) {
			if (dsManager!=null) 
				dispose(); //unregister first
			dsManager = plm;
			dsManager.addStoreListener(this);
			rebuild();
		}
		
		protected abstract void rebuild();
		
		public abstract List<DataSet> getSelectedDSs();

		public abstract void setSelectedDSs(List<DataSet> pls);
		
		public abstract Component getComponent();
		
		public abstract int getSelectableCount();
		
		public void dispose() {
			dsManager.removeStoreListener(this);
		}
		
		public void objectAdded(StoreEvent evt) {
			changed(evt);
		}
		
		public void objectRemoved(StoreEvent evt) {
			changed(evt);
		}
		
		public void changed( StoreEvent event ) {			
			List<DataSet>  oldSel = getSelectedDSs();
			rebuild();
			setSelectedDSs(oldSel);
		}
		
	}
	
	


	
	
	public abstract static class SelectionTree extends SelectionComponent {
		
		protected JTree plTree;
		protected DefaultTreeModel treeModel;
		protected DefaultMutableTreeNode rootNode;
		
		public SelectionTree() {
			rootNode = new DefaultMutableTreeNode("DataSets");
			treeModel = new DefaultTreeModel(rootNode);
			plTree = new JTree(rootNode);
			plTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
			plTree.addMouseListener(new ObjectListMouseListener());
		}
		
		@Override
		public List<DataSet> getSelectedDSs() {
			List<DataSet> ret = new LinkedList<DataSet>();
			TreePath[] selectedRows = plTree.getSelectionModel().getSelectionPaths();
			if (selectedRows!=null)
				for (TreePath tp : selectedRows) {
					DataSet ds = nodeToDS(((DefaultMutableTreeNode)tp.getLastPathComponent()));
					if (ds!=null)
							ret.add(ds);				
				}
			return ret;
		}

		@Override
		public void setSelectedDSs(List<DataSet> groups) {
			TreePath[] paths = new TreePath[groups.size()];
			int pathi=0;
			for (DataSet ds : groups) {				
				paths[pathi] = findInTree(new TreePath(rootNode), ds);
				pathi++;
			}
			plTree.getSelectionModel().setSelectionPaths(paths);
		}

		@Override
		public Component getComponent() {
			return new JScrollPane(plTree);
		}
		
		public void populate(DataSetManager plm) {
			super.populate(plm);
		}
		
		protected abstract DataSet nodeToDS(DefaultMutableTreeNode node);
		
		@SuppressWarnings("unchecked")
		protected TreePath findInTree(TreePath parent, DataSet object) {
	        DefaultMutableTreeNode startnode = (DefaultMutableTreeNode)parent.getLastPathComponent();
	        	    
	        if (nodeToDS(startnode)==object)
	            return parent;
	    
	        // Traverse children
	        if (startnode.getChildCount() >= 0) {
	        	for (Enumeration e=startnode.children(); e.hasMoreElements(); ) {
	        		TreeNode n = (TreeNode)e.nextElement();
	        		TreePath path = parent.pathByAddingChild(n);
	        		TreePath result = findInTree(path, object);
	        		// Found a match
	        		if (result != null) {
	        			return result;
	        		}
	        	}
	        }
	        return null;
		}
		
		protected class ObjectListMouseListener extends MouseInputAdapter {      
			public void mouseClicked( MouseEvent e ) {       
				if ( e.getButton() == MouseEvent.BUTTON1 ) 
					if ( e.getClickCount() == 2 ) { 
						List<DataSet> mgs = getSelectedDSs();
						if (mgs.size()>0) {
							AbstractPropertiesDialog dlg;
							//Object selected = mgs.get(0);
							dlg = PropertiesDialogFactory.createDialog(mgs.toArray());
							Component c = plTree;
							while (c!=null && !(c instanceof java.awt.Dialog))
								c = c.getParent();
							if (c!=null)
								dlg.setModal(((java.awt.Dialog)c).isModal());
							//dlg.setModal(isModal()); 
							dlg.setVisible(true);
						}
					}
			}
		}  
		
		
	}
	
	private static class SelectionByList extends SelectionTree {

		private int selectableCount;
		
		public SelectionByList() {
			super();
			plTree.setCellRenderer(new DataSetTreeCellRenderer());
		}
		
		@Override
		protected void rebuild() {
			selectableCount=0;
			rootNode.removeAllChildren();
			for (DataSet pl : dsManager.getDataSets()) {
				if (filter==null || filter.pass(pl)) {
					rootNode.add(new DefaultMutableTreeNode(pl));
					++selectableCount;					
				}
			}
			treeModel = new DefaultTreeModel(rootNode);
			plTree.setModel(treeModel);
			for (int i = 0; i < plTree.getRowCount(); i++) {
				plTree.expandRow(i);
			}

		}
		
		protected DataSet nodeToDS(DefaultMutableTreeNode node) {
			Object o = node.getUserObject();
			if (o instanceof DataSet)
				return (DataSet)node.getUserObject();
			return null;
		}
		
		public String toString() {
			return "All DataSets";
		}
		
		public int getSelectableCount() {
			return selectableCount;
		}
		
	}
	
	
	public static class DataSetTreeCellRenderer extends DefaultTreeCellRenderer {
		
		public Component getTreeCellRendererComponent(JTree tree,
                Object value,
                boolean selected,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
			
			Object val = ((DefaultMutableTreeNode)value).getUserObject();
			
			Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

			if (val instanceof DataSet) {
				((JLabel)c).setText(((DataSet)val).getName());
			} 
			return c;							
		}
	}

	
}
