package mayday.dynamicpl.gui;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

import mayday.dynamicpl.Rule;

@SuppressWarnings("serial")
public class RuleTreeTransferHandler extends TransferHandler {

	private DefaultMutableTreeNode SelectedNode;
	private RuleTreeEditorPane rtep;
	private JTree tree;
	
	public RuleTreeTransferHandler(RuleTreeEditorPane t) {
		rtep = t;
		tree = t.ruleTree;
	}
	
	
	public boolean canImport(TransferHandler.TransferSupport info) {
		// Check if the import comes from ourself
		return (SelectedNode!=null);
	}

	protected Transferable createTransferable(JComponent c) {
		return new StringSelection(exportString(c));
	}

	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY_OR_MOVE;
	}

	public boolean importData(TransferHandler.TransferSupport info) {
		if (!info.isDrop()) 
			return false;		
		if (SelectedNode==null)
			return false;
				
		JTree.DropLocation dl = (JTree.DropLocation)info.getDropLocation();
		
		DefaultMutableTreeNode newParent = ((DefaultMutableTreeNode)dl.getPath().getLastPathComponent());		
		Object selO = newParent.getUserObject();
		if (selO instanceof Rule)
			newParent = (DefaultMutableTreeNode)newParent.getParent();
		
		// sanity check: newParent may not be a child of SelectedNode, obviously
		boolean insane = false;
		DefaultMutableTreeNode node = newParent;
		do {
			if (node==SelectedNode) {
				insane=true;
				break;
			}
			node = (DefaultMutableTreeNode)node.getParent();			
		} while (node!=null);
		
		if (insane) {
			System.err.println("This drag&drop operation is insane.");
			return false;
		}
		
		rtep.removeNode(SelectedNode);
		rtep.insertNode(SelectedNode, newParent, 0);		
		return true;
	}

	protected void exportDone(JComponent c, Transferable data, int action) {
		SelectedNode=null;
	}

	//Bundle up the selected items in the list
	//as a single string, for export.
	protected String exportString(JComponent c) {
		//JTree tree = (JTree)c;
		//assume that c == tree
		
		SelectedNode = ((DefaultMutableTreeNode)tree.getLastSelectedPathComponent());
		
		return SelectedNode.getUserObject().toString();
	}



}
