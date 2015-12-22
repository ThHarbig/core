package mayday.core.datasetmanager.gui;

import java.awt.Component;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import mayday.core.DataSet;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.gui.dragndrop.MaydayTransferHandler;

@SuppressWarnings("serial")
public class DataSetTransferHandler extends MaydayTransferHandler<DataSet> {

	public DataSetTransferHandler() {
		super(DataSet.class);
	}	

	@Override
	public DataSet[] getDragObject(JComponent c) {
		return DataSetManagerView.getInstance().getSelectedDataSets().toArray(new DataSet[0]);		
	}


	@Override
	protected void processDrop(Component c, DataSet[] droppedObjects, TransferHandler.TransferSupport info) {
		for (DataSet ds : droppedObjects) {
			if (DataSetManager.singleInstance.contains(ds)) {
				// move the ds
				JList.DropLocation dl = (JList.DropLocation)info.getDropLocation();
				
				int draggedIndex = DataSetManager.singleInstance.indexOf(ds);
				int target = dl.getIndex();
				
				boolean sourceBeforeTarget = (draggedIndex < target);
    			int addIndex = (sourceBeforeTarget ? target-1 : target);
    			List<DataSet> os = DataSetManagerView.getInstance().getSelectedDataSets();
    			DataSetManager.singleInstance.removeObject(ds);
    			if (addIndex<0)
    				addIndex=DataSetManager.singleInstance.getNumberOfObjects();
    			DataSetManager.singleInstance.add(addIndex, ds);
    			DataSetManagerView.getInstance().setSelectedDataSets(os);
			} else {
				DataSetManager.singleInstance.addObjectAtBottom(ds);
			}
		}
						
	}

	@Override
	protected Object getContextObject() {
		// no context info needed for this drag handler
		return null;
	}

}
