package mayday.core.gui.probelist;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JList;
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

import mayday.core.ProbeList;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.probelistmanager.ProbeListManagerEvent;
import mayday.core.probelistmanager.ProbeListManagerListener;
import mayday.core.probelistmanager.gui.cellrenderer.ProbeListCellRenderer;

@SuppressWarnings("serial")
public class ProbeListSelectionPanel extends JTabbedPane implements ChangeListener {

	private HashMap<SelectionComponent, ProbeListManager> selComps = new HashMap<SelectionComponent, ProbeListManager>();
	private LinkedList<SelectionComponent> byTabIndex = new LinkedList<SelectionComponent>();
	private int lastSelectedTab=0;
	
	private ProbeListSelectionPanel() {
		this.addChangeListener(this);
	}
	
	public ProbeListSelectionPanel(ProbeListManager plm) {
		this();
		SelectionComponent sc = new SelectionByList();
		selComps.put(sc, plm);
		add(plm.getDataSet().getName(),sc.getComponent());
		byTabIndex.add(sc);
		populate();
	}
	
	public ProbeListSelectionPanel(Collection<ProbeListManager> plms) {
		this();
		for (ProbeListManager plm : plms) {
			SelectionComponent sc = new SelectionByList();
			add(plm.getDataSet().getName(),sc.getComponent());
			selComps.put(sc, plm);
			byTabIndex.add(sc);
		}
		populate();
	}

	
	public void setFilter(ProbeListSelectionFilter plf) {
		for (SelectionComponent sc : byTabIndex)
			sc.setFilter(plf);
		populate();
	}
	
	private void populate() {
		for(SelectionComponent sc : byTabIndex) {
			sc.populate(selComps.get(sc));
		}
	}
	
	// copy current selection on tab change
	public void stateChanged(ChangeEvent arg0) {
		if (this.getSelectedIndex()!=lastSelectedTab) {
			List<ProbeList> sel = byTabIndex.get(lastSelectedTab).getSelectedPLs();
			for (SelectionComponent sc : byTabIndex)
				sc.setSelectedPLs(sel);
			lastSelectedTab = this.getSelectedIndex();
		}
	}
	
	public List<ProbeList> getSelection() {
		return byTabIndex.get(lastSelectedTab).getSelectedPLs(); 
	}
	
	public int getSelectableCount() {
		return byTabIndex.get(lastSelectedTab).getSelectableCount();
	}
	
	public void removeNotify() {
		for (SelectionComponent sc : byTabIndex) {
			sc.dispose();
		}
	    super.removeNotify();
	  }
	
	public void addInternalMouseListener(MouseListener ml) {
		for (SelectionComponent sc : byTabIndex) {
			sc.getComponent().addMouseListener(ml);
		}		
	}

	
	
	// the parent class for different kind of probelist lists (tree, by type, by class...)
	private abstract static class SelectionComponent implements ProbeListManagerListener {
		
		protected ProbeListManager plManager;
		protected ProbeListSelectionFilter filter;
		
		public abstract String toString();

		public void setFilter(ProbeListSelectionFilter plf) {
			filter = plf;
		}
		
		public void populate(ProbeListManager plm) {
			if (plManager!=null) 
				dispose(); //unregister first
			plManager = plm;
			plManager.addProbeListManagerListener(this);
			rebuild();
		}
		
		protected abstract void rebuild();
		
		public abstract List<ProbeList> getSelectedPLs();

		public abstract void setSelectedPLs(List<ProbeList> pls);
		
		public abstract Component getComponent();
		
		public abstract int getSelectableCount();
		
		public void dispose() {
			plManager.removeProbeListManagerListener(this);
		}
		
		public void probeListManagerChanged( ProbeListManagerEvent event ) {
			List<ProbeList>  oldSel = getSelectedPLs();
			rebuild();
			setSelectedPLs(oldSel);
		}
		
	}
	
	


	
	
	public abstract static class SelectionTree extends SelectionComponent {
		
		protected JTree plTree;
		protected DefaultTreeModel treeModel;
		protected DefaultMutableTreeNode rootNode;
		
		public SelectionTree() {
			rootNode = new DefaultMutableTreeNode("Probe Lists");
			treeModel = new DefaultTreeModel(rootNode);
			plTree = new JTree(rootNode);
			plTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
			plTree.addMouseListener(new ObjectListMouseListener());
		}
		
		@Override
		public List<ProbeList> getSelectedPLs() {
			List<ProbeList> ret = new LinkedList<ProbeList>();
			TreePath[] selectedRows = plTree.getSelectionModel().getSelectionPaths();
			if (selectedRows!=null)
				for (TreePath tp : selectedRows) {
					ProbeList pl = nodeToPL(((DefaultMutableTreeNode)tp.getLastPathComponent()));
					if (pl!=null)
							ret.add(pl);				
				}
			return ret;
		}

		@Override
		public void setSelectedPLs(List<ProbeList> groups) {
			TreePath[] paths = new TreePath[groups.size()];
			int pathi=0;
			for (ProbeList pl : groups) {				
				paths[pathi] = findInTree(new TreePath(rootNode), pl);
				pathi++;
			}
			plTree.getSelectionModel().setSelectionPaths(paths);
		}

		@Override
		public Component getComponent() {
			return new JScrollPane(plTree);
		}
		
		public void populate(ProbeListManager plm) {
			super.populate(plm);
		}
		
		protected abstract ProbeList nodeToPL(DefaultMutableTreeNode node);
		
		@SuppressWarnings("unchecked")
		protected TreePath findInTree(TreePath parent, ProbeList object) {
	        DefaultMutableTreeNode startnode = (DefaultMutableTreeNode)parent.getLastPathComponent();
	        	    
	        if (nodeToPL(startnode)==object)
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
						List<ProbeList> mgs = getSelectedPLs();
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
			plTree.setCellRenderer(new ProbeListTreeCellRenderer());
		}
		
		@Override
		protected void rebuild() {
			selectableCount=0;
			rootNode.removeAllChildren();
			
			HashMap<ProbeList, DefaultMutableTreeNode> listsWeHaveSeen = new HashMap<ProbeList, DefaultMutableTreeNode>();
			listsWeHaveSeen.put(null, rootNode);
			
			for (ProbeList pl : plManager.getProbeLists()) {				
				if (filter==null || filter.pass(pl)) {
					addProbeListNode(pl,listsWeHaveSeen);
					++selectableCount;					
				}
			}
			treeModel = new DefaultTreeModel(rootNode);
			plTree.setModel(treeModel);
			for (int i = 0; i < plTree.getRowCount(); i++) {
				plTree.expandRow(i);
			}

		}
		
		protected void addProbeListNode(ProbeList pl, HashMap<ProbeList, DefaultMutableTreeNode> lwhs) {
			ProbeList parent = pl.getParent();
			if (!lwhs.containsKey(parent)) {
				// add a parent node 
				addProbeListNode(parent, lwhs);
			}
			DefaultMutableTreeNode plNode;
			if (filter==null || filter.pass(pl))
				plNode = new DefaultMutableTreeNode(pl);
			else
				plNode = new DefaultMutableTreeNode(pl.getName());			
			lwhs.get(parent).add(plNode);			
			lwhs.put(pl, plNode);
		}
		
		protected ProbeList nodeToPL(DefaultMutableTreeNode node) {
			Object o = node.getUserObject();
			if (o instanceof ProbeList)
				return (ProbeList)node.getUserObject();
			return null;
		}
		
		public String toString() {
			return "All Probe Lists";
		}
		
		public int getSelectableCount() {
			return selectableCount;
		}
		
	}
	
	
	public static class ProbeListTreeCellRenderer extends DefaultTreeCellRenderer {
		
		private ProbeListCellRenderer plcr = new ProbeListCellRenderer();
		private JList list = new JList();
		
		public Component getTreeCellRendererComponent(JTree tree,
                Object value,
                boolean selected,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
			
			Object val = ((DefaultMutableTreeNode)value).getUserObject();
			
			if (val instanceof ProbeList) {
				return plcr.getListCellRendererComponent(list,val,0,selected,hasFocus);
			} 
			return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);			
		}
	}

	
}
