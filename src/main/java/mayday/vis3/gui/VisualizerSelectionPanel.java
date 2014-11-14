package mayday.vis3.gui;

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
import mayday.vis3.model.Visualizer;

@SuppressWarnings("serial")
public class VisualizerSelectionPanel extends JTabbedPane implements ChangeListener {

	private int lastSelectedTab=0;
	
	private LinkedList<SelectionComponent> selComps = new LinkedList<SelectionComponent>();
	
	public VisualizerSelectionPanel() {
		super();
		selComps.add(new SelectionByTree());
		for(SelectionComponent sc : selComps) {
			this.add(sc.toString(), new JScrollPane(sc.getComponent()));
		}
		this.addChangeListener(this);
		populate();
	}
	
	public void setFilter(VisualizerSelectionFilter plf) {
		for (SelectionComponent sc : selComps)
			sc.setFilter(plf);
		populate();
	}
	
	private void populate() {
		for(SelectionComponent sc : selComps) {
			sc.populate();
		}
	}
	
	// copy current selection on tab change
	public void stateChanged(ChangeEvent arg0) {
		if (this.getSelectedIndex()!=lastSelectedTab) {
			List<Visualizer> sel = selComps.get(lastSelectedTab).getSelectedVs();
			for (SelectionComponent sc : selComps)
				sc.setSelectedVs(sel);
			lastSelectedTab = this.getSelectedIndex();
		}
	}
	
	public List<Visualizer> getSelection() {
		return selComps.get(lastSelectedTab).getSelectedVs(); 
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
	private abstract static class SelectionComponent {
		
		protected VisualizerSelectionFilter filter;
		
		public abstract String toString();

		public void setFilter(VisualizerSelectionFilter plf) {
			filter = plf;
		}
		
		public void populate() {
			rebuild();
		}
		
		protected abstract void rebuild();
		
		public abstract List<Visualizer> getSelectedVs();

		public abstract void setSelectedVs(List<Visualizer> pls);
		
		public abstract Component getComponent();
		
		public abstract int getSelectableCount();
		
		public void dispose() {
		}
		
	}
	
	


	
	
	public abstract static class SelectionTree extends SelectionComponent {
		
		protected JTree plTree;
		protected DefaultTreeModel treeModel;
		protected DefaultMutableTreeNode rootNode;
		
		public SelectionTree() {
			rootNode = new DefaultMutableTreeNode("Visualizers");
			treeModel = new DefaultTreeModel(rootNode);
			plTree = new JTree(rootNode);
			plTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
			plTree.addMouseListener(new ObjectListMouseListener());
		}
		
		@Override
		public List<Visualizer> getSelectedVs() {
			List<Visualizer> ret = new LinkedList<Visualizer>();
			TreePath[] selectedRows = plTree.getSelectionModel().getSelectionPaths();
			if (selectedRows!=null)
				for (TreePath tp : selectedRows) {
					Visualizer vis = nodeToVis(((DefaultMutableTreeNode)tp.getLastPathComponent()));
					if (vis!=null)
							ret.add(vis);				
				}
			return ret;
		}

		@Override
		public void setSelectedVs(List<Visualizer> groups) {
			TreePath[] paths = new TreePath[groups.size()];
			int pathi=0;
			for (Visualizer vis : groups) {				
				paths[pathi] = findInTree(new TreePath(rootNode), vis);
				pathi++;
			}
			plTree.getSelectionModel().setSelectionPaths(paths);
		}

		@Override
		public Component getComponent() {
			return plTree;
		}
		
		public void populate() {
			super.populate();
		}
		
		protected abstract Visualizer nodeToVis(DefaultMutableTreeNode node);
		
		@SuppressWarnings("unchecked")
		protected TreePath findInTree(TreePath parent, Visualizer object) {
	        DefaultMutableTreeNode startnode = (DefaultMutableTreeNode)parent.getLastPathComponent();
	        	    
	        if (nodeToVis(startnode)==object)
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
			}
		}  
		
		
	}
	
	private static class SelectionByTree extends SelectionTree {

		private int selectableCount;
		
		public SelectionByTree() {
			plTree.setCellRenderer(new VisualizerTreeCellRenderer());
		}
		
		@Override
		protected void rebuild() {
			selectableCount=0;
			rootNode.removeAllChildren();
			for (DataSet ds : Visualizer.openVisualizers.keySet()) {
				DefaultMutableTreeNode dsNode = new DefaultMutableTreeNode(ds.getName());
				for (Visualizer vis : Visualizer.openVisualizers.get(ds)) {
					if (filter==null || filter.pass(vis)) {
						dsNode.add(new DefaultMutableTreeNode(vis));
						++selectableCount;					
					}
				}
				if (dsNode.getChildCount()>0)
					rootNode.add(dsNode);
			}
			treeModel = new DefaultTreeModel(rootNode);
			plTree.setModel(treeModel);
			for (int i = 0; i < plTree.getRowCount(); i++) {
				plTree.expandRow(i);
			}

		}
		
		protected Visualizer nodeToVis(DefaultMutableTreeNode node) {
			Object o = node.getUserObject();
			if (o instanceof Visualizer)
				return (Visualizer)node.getUserObject();
			return null;
		}
		
		public String toString() {
			return "by DataSet";
		}
		
		public int getSelectableCount() {
			return selectableCount;
		}
		
	}
	
	
	public static class VisualizerTreeCellRenderer extends DefaultTreeCellRenderer {
		
		public Component getTreeCellRendererComponent(JTree tree,
                Object value,
                boolean selected,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
			
			Object val = ((DefaultMutableTreeNode)value).getUserObject();
			
			Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			((JLabel)c).setOpaque(false);
			
			if (val instanceof Visualizer) {
				Visualizer vis = (Visualizer)val;
				((JLabel)c).setText("Visualizer <"+vis.getID()+">: "+vis.getMembers().size()+" open windows");
			} 
			return c;			
		}
	}

	
}
