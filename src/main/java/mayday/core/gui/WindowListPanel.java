package mayday.core.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import mayday.core.DataSet;
import mayday.core.Mayday;
import mayday.core.structures.EnumerationIterable;
import mayday.vis3.model.Visualizer;
import mayday.vis3.model.VisualizerMember;

@SuppressWarnings("serial")
public class WindowListPanel extends JPanel implements ActionListener {
	
	protected JTree windowList;
	protected TreeModel tm;
	
	public WindowListPanel() {
		setLayout(new BorderLayout());
		add(new JScrollPane(
				windowList = new JTree(tm = new DefaultTreeModel(new WindowTreeNode(Mayday.sharedInstance)))));
		MaydayWindowManager.addListener(this);
		
		windowList.setCellRenderer(new WindowNodeRenderer());
		windowList.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==2) {
					selectionToFront();
				}
				if (e.getButton()==MouseEvent.BUTTON3) {
					JPopupMenu windowMenu = new JPopupMenu("Window Actions");
					windowMenu.add(new AbstractAction("Bring to front") {

						public void actionPerformed(ActionEvent e) {
							selectionToFront();
						}
						
					});
					windowMenu.add(new AbstractAction("Close selected") {

						public void actionPerformed(ActionEvent e) {
							selectionClose();
						}
						
					});
					windowMenu.show(windowList, e.getX(), e.getY());
				}
			}
		});
		
		updateWindowList();
		
	}
	
	protected void toFrontNode(TreeNode n) {
		if (n.getChildCount()>0)
			toFrontChildren(n);
		else {
			if (n instanceof WindowTreeNode && n!=tm.getRoot())
				((WindowTreeNode)n).getUserObject().toFront();
			else if (n instanceof PlotWindowTreeNode)
				((PlotWindowTreeNode)n).getUserObject().toFront();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	protected void toFrontChildren(TreeNode n) {
		HashSet<TreeNode> htn = new HashSet<TreeNode>();
		new EnumerationIterable(n.children()).addToCollection(htn);
		for (TreeNode cn : htn) {
			toFrontNode(cn);
		}
	}
	
	protected void selectionToFront() {
		if (windowList.getSelectionModel().getSelectionCount()==0)
			return;
		for (TreePath tp : windowList.getSelectionModel().getSelectionPaths()) {
			TreeNode n = (TreeNode)tp.getLastPathComponent();
			toFrontNode(n);
			windowList.expandPath(tp);
		}
	}
	
	protected void closeNode(TreeNode n) {
		if (n.getChildCount()>0)
			closeChildren(n);
		else {
			if (n instanceof WindowTreeNode && n!=tm.getRoot())
				((WindowTreeNode)n).getUserObject().dispose();
			else if (n instanceof PlotWindowTreeNode)
				((PlotWindowTreeNode)n).getUserObject().closePlot();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void closeChildren(TreeNode n) {
		HashSet<TreeNode> htn = new HashSet<TreeNode>();
		new EnumerationIterable(n.children()).addToCollection(htn);
		for (TreeNode cn : htn) {
			closeNode(cn);
		}
	}

	protected void selectionClose() {
		if (windowList.getSelectionModel().getSelectionCount()==0)
			return;
		for (TreePath tp : windowList.getSelectionModel().getSelectionPaths()) {
			TreeNode n = (TreeNode)tp.getLastPathComponent();
			closeNode(n);			
		}
	}
	
	public void dispose() {
		MaydayWindowManager.removeListener(this);
	}

	protected void updateWindowList() {
		// first add all visualizers
		
		DefaultMutableTreeNode root = ((DefaultMutableTreeNode)tm.getRoot());
		
		HashSet<Object> expandedRows = new HashSet<Object>();
		for (int i=0; i!=windowList.getRowCount(); ++i) {
			if (windowList.isExpanded(i)) {
				TreeNode tn = (TreeNode)windowList.getPathForRow(i).getLastPathComponent();
				if (tn instanceof DefaultMutableTreeNode) {
					expandedRows.add(((DefaultMutableTreeNode)tn).getUserObject());
				}
			}
		}

		root.removeAllChildren();
		
		HashSet<VisualizerMember> activeVis = new HashSet<VisualizerMember>();
		
		// then remaining windows
		for (Window w: MaydayWindowManager.getWindows())
			if (!(w instanceof VisualizerMember)) {
				root.add(new WindowTreeNode(w));
			} else
				activeVis.add((VisualizerMember)w);
				
		
		for (DataSet ds : Visualizer.openVisualizers.keySet()) {
			DefaultMutableTreeNode dsNode = new DefaultMutableTreeNode(ds.getName());
			for (Visualizer vis : Visualizer.openVisualizers.get(ds)) {
				DefaultMutableTreeNode visNode = new DefaultMutableTreeNode("Visualizer <"+vis.getID()+">: "+vis.getMembers().size()+" open windows");
				for (VisualizerMember vm : vis.getMembers()) {
					if (activeVis.contains(vm))
						visNode.add(new PlotWindowTreeNode(vm));
				}
				if (visNode.getChildCount()>0)
					dsNode.add(visNode);
			}
			if (dsNode.getChildCount()>0)
				root.add(dsNode);
		}
		
		windowList.setModel(null);
		windowList.setModel(tm);

		if (windowList.getRowCount()>0)
			windowList.expandRow(0);
		
		for (int i=0; i!=windowList.getRowCount(); ++i) {
			TreeNode tn = (TreeNode)windowList.getPathForRow(i).getLastPathComponent();
			if (tn instanceof DefaultMutableTreeNode) {
				Object o = ((DefaultMutableTreeNode)tn).getUserObject();
				if (expandedRows.contains(o)) {
					windowList.expandPath(windowList.getPathForRow(i));
				}
				else
					o.hashCode();
			}
		}
	}
	
	protected class WindowTreeNode extends DefaultMutableTreeNode {
		
		public WindowTreeNode(Window w) {
			super(w);
		}
		
		public Window getUserObject() {
			return ((Window)super.getUserObject());
		}
	
		public String toString() {
				return MaydayWindowManager.getTitle(getUserObject());
		}
	}
	
	
	protected class PlotWindowTreeNode extends DefaultMutableTreeNode {
		
		public PlotWindowTreeNode(VisualizerMember w) {
			super(w);
		}
		
		public VisualizerMember getUserObject() {
			return ((VisualizerMember)super.getUserObject());
		}
		
		public String toString() {
			return getUserObject().getTitle();
		}
	}
	
	protected class WindowNodeRenderer extends DefaultTreeCellRenderer {
		 public Component getTreeCellRendererComponent(JTree tree, Object value,
				  boolean sel,
				  boolean expanded,
				  boolean leaf, int row,
				  boolean hasFocus) {
			 Component c = super.getTreeCellRendererComponent(tree, ((DefaultMutableTreeNode)value), sel, expanded, leaf, row, hasFocus);
			 c.setSize(c.getPreferredSize());
			 return c;
			 
		 }
	}

	public void actionPerformed(ActionEvent e) {
		updateWindowList();
	}
	
}
