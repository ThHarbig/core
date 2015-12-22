package mayday.core.meta.gui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.LinkedList;

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
import mayday.core.Experiment;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIManagerEvent;
import mayday.core.meta.MIManagerListener;
import mayday.core.meta.MIType;
import mayday.core.meta.miotree.Directory;

@SuppressWarnings("serial")
public class MIGroupSelectionPanel extends JTabbedPane implements ChangeListener {

	private MIManager miManager;
	private int lastSelectedTab=0;
	
	private LinkedList<SelectionComponent> selComps = new LinkedList<SelectionComponent>();
	
	private MIGroupSelectionPanel() {
		super();
		selComps.add(new SelectionByTree(this));
		selComps.add(new SelectionByType(this));
		selComps.add(new SelectionByContainedType(this));
		for(SelectionComponent sc : selComps) {
			this.add(sc.toString(), new JScrollPane(sc.getComponent()));
		}
		this.addChangeListener(this);
	}
	
	public MIGroupSelectionPanel(MIManager mim) {
		this();
		miManager=mim;
		populate();
	}
	
	public MIGroupSelectionPanel(MIManager mim, String mioType) {
		this();		
		miManager=mim;
		FilterCriteria[] filters = new FilterCriteria[]{ 
				new TypeFilterCriteria(mioType)
		};
		addFilterCriteria(filters);
	}
	
	public MIGroupSelectionPanel(MIManager mim, Class<?>... clazz) {
		this();		
		miManager=mim;
		FilterCriteria[] filters = new FilterCriteria[]{ 
				new ClassFilterCriteria(clazz)
		};
		addFilterCriteria(filters);
	}
	
	public void addFilterCriteria(FilterCriteria... filters) {
		for (SelectionComponent sc : selComps)
			for (FilterCriteria fc : filters)
				sc.addFilterCriteria(fc);
		populate();
	}
	
	private void populate() {
		for(SelectionComponent sc : selComps) {
			sc.populate(miManager);
		}
	}
	
	// copy current selection on tab change
	public void stateChanged(ChangeEvent arg0) {
		if (this.getSelectedIndex()!=lastSelectedTab) {
			MIGroupSelection<MIType> sel = selComps.get(lastSelectedTab).getSelectedGroups();
			for (SelectionComponent sc : selComps)
				sc.setSelectedGroups(sel);
			lastSelectedTab = this.getSelectedIndex();
		}
	}
	
	public MIGroupSelection<MIType> getSelection() {
		return selComps.get(lastSelectedTab).getSelectedGroups(); 
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

	
	
	
	public interface FilterCriteria {
		public boolean pass(MIGroup mg);
	}
	
	public static class TypeFilterCriteria implements FilterCriteria {
		private String theType;
		public TypeFilterCriteria(String mioType) {
			theType = mioType;
		}
		public boolean pass(MIGroup mg) {
			return (mg.getMIOType().equals(theType));
		}
	}
	
	@SuppressWarnings("unchecked")
	public static class ClassFilterCriteria implements FilterCriteria {
		private Class[] clazz;
		public ClassFilterCriteria(Class[] c) {
			clazz = c;
		}
		public boolean pass(MIGroup mg) {
			for (Class c : clazz)
				if (c.isAssignableFrom(mg.getMIOClass()))
					return true;
			return false;
		}
	}
	
	// the parent class for different kind of mio group lists (tree, by type, by class...)
	private abstract static class SelectionComponent implements MIManagerListener {
		
		protected MIManager miManager;
		protected MIGroupSelectionPanel parentPanel;
		// TRIGGERING: put this back in when delaying events works reliably, i.e. when MIGroups are thread-safe
		// protected DelayedUpdateTask updateTask = new DelayedUpdateTask(this.toString()) {
//
//			protected boolean needsUpdating() {
//				return true;
//			}
//
//			protected synchronized void performUpdate() {
//				MIGroupSelection<MIType> oldSel = getSelectedGroups();
//				rebuild();
//				setSelectedGroups(oldSel);
//			}
//			
//		};
		
		public SelectionComponent(MIGroupSelectionPanel parent) {
			parentPanel=parent;
		}
		
		public abstract String toString();
		
		protected LinkedList<FilterCriteria> filters = new LinkedList<FilterCriteria>();		
		
		public void populate(MIManager mim) {
			if (miManager!=null) 
				dispose(); //unregister first
			miManager = mim;
			miManager.addMIManagerListener(this);
			// miManager.addListenerForObject(this);  // TRIGGERING: put this back in when event delaying works reliably
			rebuild();
		}
		
		protected abstract void rebuild();
		
		protected boolean passFilters(MIGroup mg) {
			for (FilterCriteria fc : filters)
				if (!fc.pass(mg))
					return false;
			return true;
		}
		
		public void addFilterCriteria(FilterCriteria fc) {
			if (!filters.contains(fc))
				filters.add(fc);
			if (miManager!=null)
				rebuild();
		}
		
		public void removeFilterCriteria(FilterCriteria fc) {
			filters.remove(fc);
			if (miManager!=null)
				rebuild();
		}
		
		public void clearFilterCriteria() {
			filters.clear();
			if (miManager!=null)
				rebuild();
		}

		public abstract MIGroupSelection<MIType> getSelectedGroups();

		public abstract void setSelectedGroups(MIGroupSelection<MIType> groups);
		
		public abstract Component getComponent();
		
		public abstract int getSelectableCount();
		
		public void dispose() {
			miManager.removeMIManagerListener(this);
//			miManager.removeListenerForObject(this); // TRIGGERING: put this back in when event delaying works reliably
		}
		
		public void miManagerChanged( MIManagerEvent event ) {
//			TRIGGERING: put this back in when event delaying works reliably			
//			updateTask.trigger();
			MIGroupSelection<MIType> oldSel = getSelectedGroups();
			rebuild();
			setSelectedGroups(oldSel);
		}

		public Object getWatchedObject() {
			return null;
		}

// TRIGGERING: put this back in when event delaying works reliably		
//		public void miGroupChanged(MIGroupEvent event) {
//			updateTask.trigger();
//		}
		
	}
	
	


	
	
	public abstract static class SelectionTree extends SelectionComponent  {
		
		protected JTree mioTree;
		protected DefaultTreeModel treeModel;
		protected DefaultMutableTreeNode rootNode;
		
		public SelectionTree(MIGroupSelectionPanel parent) {
			super(parent);
			rootNode = new DefaultMutableTreeNode("Meta information groups");
			treeModel = new DefaultTreeModel(rootNode);
			mioTree = new JTree(rootNode);
			mioTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
			mioTree.addMouseListener(new ObjectListMouseListener());
			mioTree.setDragEnabled(true);
			mioTree.setTransferHandler(new MIGroupTransferHandler(this, miManager));
		}
		
		@Override
		public MIGroupSelection<MIType> getSelectedGroups() {
			MIGroupSelection<MIType> mgs = new MIGroupSelection<MIType>();
			TreePath[] selectedRows = mioTree.getSelectionModel().getSelectionPaths();
			if (selectedRows!=null)
				for (TreePath tp : selectedRows) {
					MIGroup mg = nodeToGroup(((DefaultMutableTreeNode)tp.getLastPathComponent()));
					if (mg!=null)
						if (passFilters(mg))
							mgs.add(mg);				
				}
			return mgs;
		}

		@Override
		public void setSelectedGroups(MIGroupSelection<MIType> groups) {
			TreePath[] paths = new TreePath[groups.size()];
			int pathi=0;
			for (MIGroup mg : groups) {				
				paths[pathi] = findInTree(new TreePath(rootNode), mg);
				pathi++;
			}
			mioTree.getSelectionModel().setSelectionPaths(paths);
		}

		@Override
		public Component getComponent() {
			return mioTree;
		}
		
		public void populate(MIManager mim) {
			super.populate(mim);
			mioTree.setTransferHandler(new MIGroupTransferHandler(this, mim));
		}
		
		protected void rebuild() {
			//((DefaultTreeModel)mioTree.getModel()).nodesChanged(rootNode, null); // force jtree to update node widths
		}
		
		protected abstract MIGroup nodeToGroup(DefaultMutableTreeNode node);
		
		@SuppressWarnings("unchecked")
		protected TreePath findInTree(TreePath parent, MIGroup object) {
	        DefaultMutableTreeNode startnode = (DefaultMutableTreeNode)parent.getLastPathComponent();
	        	    
	        if (nodeToGroup(startnode)==object)
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
		
		public void dispose() {
			super.dispose();
		}
		
		protected class ObjectListMouseListener extends MouseInputAdapter {      
			public void mouseClicked( MouseEvent e ) {   
				MIGroupSelection<MIType> mgs = getSelectedGroups();
				// update nodes here
				for (MIGroup mg : mgs) {
					TreePath path = findInTree(new TreePath(rootNode), mg);
					if (path!=null)
						((DefaultTreeModel)mioTree.getModel()).nodeChanged((TreeNode)path.getLastPathComponent());
				}
				if ( e.getButton() == MouseEvent.BUTTON1 ) {
					if ( e.getClickCount() == 2 ) { 						
						if (mgs.size()>0) {
							AbstractPropertiesDialog dlg;
							//Object selected = mgs.get(0);
							dlg = PropertiesDialogFactory.createDialog(mgs.toArray());
							Component c = mioTree;
							while (c!=null && !(c instanceof java.awt.Dialog))
								c = c.getParent();
							if (c!=null)
								dlg.setModal(((java.awt.Dialog)c).isModal());
							//dlg.setModal(isModal()); 
							dlg.setVisible(true);
						}
					}
				} else { if ( e.getButton() == MouseEvent.BUTTON3 ) 
					if ( e.getClickCount() == 1 ) { 
						MIMenu mima = new MIMenu(parentPanel, miManager, null);
						mima.getPopupMenu().show(mioTree,  e.getX(), e.getY() );
					}
				}
			}
		}  			
	}
	
	private static class SelectionByType extends SelectionTree {

		private int selectableCount;
		
		public SelectionByType(MIGroupSelectionPanel parent) {
			super(parent);
			mioTree.setCellRenderer(new MIGroupTreeCellRenderer(this));
		}
		
		@Override
		protected void rebuild() {			
			selectableCount=0;
			rootNode.removeAllChildren();
			for (String type : miManager.getTypes()) {
				DefaultMutableTreeNode typeNode = new DefaultMutableTreeNode(MIManager.getMIOPluginInfo(type).getName());
				// add all children
				for (MIGroup mg : miManager.getGroupsForType(type)) {
					if (passFilters(mg)) {
						typeNode.add(new DefaultMutableTreeNode(mg));
						++selectableCount;
					}
				}
				if (typeNode.getChildCount()>0)
					rootNode.add(typeNode);
			}
			treeModel = new DefaultTreeModel(rootNode);
			mioTree.setModel(treeModel);
//			for (int i = 0; i < mioTree.getRowCount(); i++) {
//				mioTree.expandRow(i);
//			}
			super.rebuild();
		}
		
		protected MIGroup nodeToGroup(DefaultMutableTreeNode node) {
			Object o = node.getUserObject();
			if (o instanceof MIGroup)
				return (MIGroup)node.getUserObject();
			return null;
		}
		
		public String toString() {
			return "by Type";
		}
		
		public int getSelectableCount() {
			return selectableCount;
		}
		
	}
	
	private static class SelectionByTree extends SelectionTree {

		private int selectableCount;
		
		public SelectionByTree(MIGroupSelectionPanel parent) {
			super(parent);
			mioTree.setCellRenderer(new MIDirectoryTreeCellRenderer(this));
		}
		
		private DefaultMutableTreeNode buildSubTree(Directory d) {
			DefaultMutableTreeNode startNode = new DefaultMutableTreeNode(d);
			for (Directory subd : d.getSubDirs()) {
				DefaultMutableTreeNode subDirNode = buildSubTree(subd);
				if (subDirNode.getChildCount()>0) 
					startNode.add(subDirNode);
				if (subd.getGroup()!=null && passFilters(subd.getGroup())) {
					startNode.add(subDirNode);
					selectableCount++;
				}
				
			}
			return startNode;
		}
		
		@Override
		protected void rebuild() {
			rootNode.removeAllChildren();
			selectableCount=0;
			for (Directory d : miManager.getTreeRoot().getSubDirs()) {
				DefaultMutableTreeNode dirNode = buildSubTree(d);
				if (dirNode.getChildCount()>0)
					rootNode.add(dirNode);
				if (d.getGroup()!=null && passFilters(d.getGroup())) {
					rootNode.add(dirNode);
					selectableCount++;
				}
			}
			treeModel = new DefaultTreeModel(rootNode);
			mioTree.setModel(treeModel);
//			for (int i = 0; i < mioTree.getRowCount(); i++) {
//				mioTree.expandRow(i);
//			}
			super.rebuild();
		}
		
		protected MIGroup nodeToGroup(DefaultMutableTreeNode node) {
			Object o = node.getUserObject();
			if (o!=null && o instanceof Directory) 
				return ((Directory)o).getGroup();
			return null;
		}
		
		public String toString() {
			return "by Path";
		}
		
		
		public int getSelectableCount() {
			return selectableCount;
		}

		
	}
	
	
	
	private static class SelectionByContainedType extends SelectionTree {

		private int selectableCount;
		
		public SelectionByContainedType(MIGroupSelectionPanel parent) {
			super(parent);
			mioTree.setCellRenderer(new MIGroupTreeCellRenderer(this));
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void rebuild() {
			selectableCount=0;
			rootNode.removeAllChildren();
			
			Class[] availabletypes = new Class[]{Probe.class, ProbeList.class, Experiment.class, DataSet.class, MIType.class};
			DefaultMutableTreeNode[] typeNodes = new DefaultMutableTreeNode[availabletypes.length];
			LinkedList<Integer> indices = new LinkedList<Integer>();
			
			for (int i=0; i!=availabletypes.length; ++i) {
				String typeName = availabletypes[i].toString();
				typeName = typeName.substring(typeName.lastIndexOf(".")+1);
				typeNodes[i]= new DefaultMutableTreeNode(typeName);
				rootNode.add(typeNodes[i]);
				indices.add(i);
			}
			
			for (MIGroup mg : miManager.getGroups()) {
				if (passFilters(mg)) {
					LinkedList<Integer> remaining_indices = new LinkedList<Integer>(indices);
					for (Object o : mg.getObjects()) {
						for (int sindex=0; sindex!=remaining_indices.size(); ) {
							int index = remaining_indices.get(sindex);
							if (o!=null && availabletypes[index].isAssignableFrom(o.getClass())) {
								typeNodes[index].add(new DefaultMutableTreeNode(mg));
								if (remaining_indices.size()==indices.size())
									++selectableCount;
								remaining_indices.remove(sindex);
								break;
							} else {
								++sindex;
							}
						}
						if (remaining_indices.size()==0)
							break;
					}
				}
			}
			treeModel = new DefaultTreeModel(rootNode);
			mioTree.setModel(treeModel);
//			for (int i = 0; i < mioTree.getRowCount(); i++) {
//				mioTree.expandRow(i);
//			}
			super.rebuild();

		}
		
		protected MIGroup nodeToGroup(DefaultMutableTreeNode node) {
			Object o = node.getUserObject();
			if (o instanceof MIGroup)
				return (MIGroup)node.getUserObject();
			return null;
		}
		
		public String toString() {
			return "by annotated Objects";
		}
		
		public int getSelectableCount() {
			return selectableCount;
		}
		
	}
	
	
	
	
	
	public static class MIDirectoryTreeCellRenderer extends DefaultTreeCellRenderer {
		
		private SelectionComponent parent;
		
		public MIDirectoryTreeCellRenderer(SelectionComponent p) {
			parent=p;
		}
		
		public Component getTreeCellRendererComponent(JTree tree,
                Object value,
                boolean selected,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
			
			Object val = ((DefaultMutableTreeNode)value).getUserObject();
			
			if (val instanceof Directory) {
				Directory d = (Directory)val;
				MIGroup mg = d.getGroup();
				String name = ((Directory)val).getName();
				if (mg==null) {
					name = "("+name+")";
				} else {		
					if (!parent.passFilters(mg))
						name = "<font color=#777777>"+name;
					name+="&nbsp;&nbsp;<font size=-2>["+mg.size()+"]";
				}
				name = "<html><nobr>"+name;
				return super.getTreeCellRendererComponent(tree, name, selected, expanded, leaf, row, hasFocus);
			} 
			return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);			
		}
	}
	
	public static class MIGroupTreeCellRenderer extends DefaultTreeCellRenderer {
		
		private SelectionComponent parent;
		
		public MIGroupTreeCellRenderer(SelectionComponent p) {
			parent=p;
		}
		
		public Component getTreeCellRendererComponent(JTree tree,
                Object value,
                boolean selected,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
			
			
			Object val = ((DefaultMutableTreeNode)value).getUserObject();
			
			Component ret; 
			
			if (val instanceof MIGroup) {
				String name = ((MIGroup)val).getName();
				if (!parent.passFilters(((MIGroup)val)))
					name = "<font color=#777777>"+name;
				name+="&nbsp;&nbsp;<font size=-2>["+((MIGroup)val).size()+"]";				
				name = "<html><nobr>"+name+"</nobr></html>";
				ret = super.getTreeCellRendererComponent(tree, name, selected, expanded, leaf, row, hasFocus);
			} else {
				ret = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			}
			
			return ret;
		}
	}


	
	
}
