/*
 * Created on Feb 7, 2005
 *
 */
package mayday.core.probelistmanager.gui;

import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.gui.ProbeListImage;
import mayday.core.probelistmanager.ProbeListManagerTree;
import mayday.core.probelistmanager.gui.cellrenderer.GraphicProbeListTreeCellRenderer;
import mayday.core.probelistmanager.gui.cellrenderer.ProbeListTreeCellRenderer;

@SuppressWarnings("serial")
public class ProbeListJTree
extends JTree
implements ProbeListListener
{
	
	ProbeListManagerTree plmt;
	
	public ProbeListJTree(ProbeListManagerTree probeListManager) {
		super(probeListManager.getTreeModel());		
		
		plmt = probeListManager;
		addTreeSelectionListener(new TreeSelectionListener() {

			public void valueChanged(TreeSelectionEvent e) {
								
				try {
				
					DefaultMutableTreeNode ond = (DefaultMutableTreeNode)e.getOldLeadSelectionPath().getLastPathComponent();
					DefaultMutableTreeNode nnd = (DefaultMutableTreeNode)e.getOldLeadSelectionPath().getLastPathComponent();

					// update nodes in case they have changed in the meantime
					((DefaultTreeModel)treeModel).nodeChanged(ond);
					((DefaultTreeModel)treeModel).nodeChanged(nnd);
				} catch (Exception ex) {
					// never mind.
				}			}
			
		});
		init();
	}

	
	// A very dirty way to force the JTree to correctly display compute the node sizes
	@SuppressWarnings("unchecked")
	public void validate() {
		super.validate();
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				Enumeration<ProbeListNode> c = ((ProbeListNode)getModel().getRoot()).postorderEnumeration();
				while (c.hasMoreElements()) {
					ProbeListNode pln = c.nextElement();
					((DefaultTreeModel)treeModel).nodeChanged(pln);
				}
			}
			
		});
	}

	private void init() {
		updateCellRenderer();
		addProbeListListeners();
	}

	protected void removeProbeListListeners() {
		for (ProbeList pl : plmt.getProbeLists())
			pl.removeProbeListListener( this );
	}

	protected void addProbeListListeners() {
		for (ProbeList pl : plmt.getProbeLists())
			pl.addProbeListListener( this );
	}

	public void removeNotify()	{       
		removeProbeListListeners();
		super.removeNotify();
	}
	
	public void addNotify()	{       
		addProbeListListeners();
		super.addNotify();
	}

	public void probeListChanged( ProbeListEvent event ) {
		if ( ( event.getChange() & ProbeListEvent.OVERALL_CHANGE ) != 0 ) {
			this.repaint();
		}
	}     

	public void updateCellRenderer() {
		if (ProbeListImage.useGraphics.getBooleanValue())
			setCellRenderer( new GraphicProbeListTreeCellRenderer() );
		else
			setCellRenderer( new ProbeListTreeCellRenderer() );
		repaint();
	}
	
	public ProbeListNode getSelectedNode() {
		return (ProbeListNode)getLastSelectedPathComponent();
	}
	
	public ProbeListNode[] getSelectedNodes() {
		TreePath[] tps = getSelectionPaths();
		ProbeListNode[] ret = new ProbeListNode[tps==null?0:tps.length];
		if (tps!=null)
			for (int i=0; i!=ret.length; ++i)
				ret[i] = (ProbeListNode)tps[i].getLastPathComponent();
		return ret;
	}	
	
	public void expandPathTo(TreeNode node) {
		TreePath tp = new TreePath(((DefaultTreeModel)getModel()).getPathToRoot(node));
		expandPath(tp);
	}
	
	public void selectNode(TreeNode node) {
		TreePath tp = new TreePath(((DefaultTreeModel)getModel()).getPathToRoot(node));
		getSelectionModel().setSelectionPath(tp);	
	}
	
	
}
