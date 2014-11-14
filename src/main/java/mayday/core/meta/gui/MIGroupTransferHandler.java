package mayday.core.meta.gui;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

import mayday.core.gui.dragndrop.MaydayTransferHandler;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.MIGroupSelectionPanel.SelectionTree;
import mayday.core.meta.miotree.Directory;

@SuppressWarnings("serial")
public class MIGroupTransferHandler extends MaydayTransferHandler<MIGroup> {

	SelectionTree miot;
	MIManager context;
	
	public MIGroupTransferHandler(SelectionTree t, MIManager mim) {
		super(MIGroup.class);
		miot = t;
		context = mim;
	}	

	@Override
	public MIGroup[] getDragObject(JComponent component) {
		MIGroupSelection<MIType> mgs = miot.getSelectedGroups();
		return mgs.toArray(new MIGroup[mgs.size()]);
	}


	@Override
	protected void processDrop(Component c, MIGroup[] droppedObjects, TransferHandler.TransferSupport info) {
		// ignore component
		
		// get insertion point
		JTree.DropLocation dl = (JTree.DropLocation)info.getDropLocation();
		
		DefaultMutableTreeNode newParentNode = ((DefaultMutableTreeNode)dl.getPath().getLastPathComponent());
		String newParentPath="";
		
		if (newParentNode.getUserObject() instanceof Directory) {
			Directory newParentDir = (Directory)newParentNode.getUserObject();		
			newParentPath = newParentDir.getName();
			while ((newParentDir=newParentDir.getParent())!=null)
				newParentPath = newParentDir.getName()+"/"+newParentPath;
		}

		for (MIGroup mg : droppedObjects)
			if (!mg.getPath().equals(newParentPath) &&!(mg.getPath()+"/"+mg.getName()).equals(newParentPath))
				context.moveGroupInTree(mg, newParentPath);
	}

	@Override
	protected Object getContextObject() {
		return context;
	}

}
