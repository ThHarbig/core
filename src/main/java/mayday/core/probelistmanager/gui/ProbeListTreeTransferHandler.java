package mayday.core.probelistmanager.gui;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.dragndrop.MaydayTransferHandler;
import mayday.core.probelistmanager.UnionProbeList;
import mayday.core.probelistmanager.models.ProbeListTreeListModel;
import mayday.core.structures.maps.MultiHashMap;

@SuppressWarnings("serial")
public class ProbeListTreeTransferHandler extends MaydayTransferHandler<ProbeList> {

	ProbeListManagerViewTree plmvt;
	
	public ProbeListTreeTransferHandler(ProbeListManagerViewTree t) {
		super(ProbeList.class);
		plmvt = t;
	}	

	@Override
	public ProbeList[] getDragObject(JComponent c) {
		//ignore component
		ProbeListNode[] pln = plmvt.getTree().getSelectedNodes();
		ProbeList[] result = new ProbeList[pln.length];
		int i=0;
		for (ProbeListNode n:pln)
			result[i++] = n.getProbeList();
		return result;
	}


	@Override
	protected void processDrop(Component c, ProbeList[] droppedObjects,TransferHandler.TransferSupport info) {
		// ignore component
		
		// get insertion point
		JTree.DropLocation dl = (JTree.DropLocation)info.getDropLocation();
		
		ProbeListNode newParentNode = ((ProbeListNode)dl.getPath().getLastPathComponent());		
		ProbeList newParentList = newParentNode.getProbeList();
		int insertionIndex = 0;
		
		if (newParentList instanceof UnionProbeList) {
			// if the parent is a unionprobelist, insert directly, as first element
			// all parameters are already correct
		} else {
			// if parent is a normal probelist, find the parent and set the correct index
			ProbeListNode tmpParentNode = newParentNode.getParent();
			insertionIndex = tmpParentNode.getIndex(newParentNode)+1;
			newParentNode = tmpParentNode;
			newParentList = newParentNode.getProbeList();
		}

		// check if ProbeLists come from same dataset
		boolean sameDataSet = droppedObjects[0].getDataSet() == newParentList.getDataSet();
		
		if (sameDataSet) {
			// Source is the same dataset
			// move each ProbeList individually in case some actions are insane and fail
			// iterate backwards to keep correct order
			ProbeListTreeListModel pltlm = ((ProbeListTreeListModel)plmvt.getTree().getModel());
			
			for (int i=droppedObjects.length-1; i>=0; i--) {
				try {
					// if the moved list is moved WITHIN it's previous parent AND the insertion index
					// is below its previous index, we have to account for the fact that removing the
					// list will change that insertion index by -1
					int delta=0;
					ProbeList oldParentList = droppedObjects[i].getParent();
					if (newParentList==oldParentList) {
						// in this case newParentNode==oldParentNode
						ProbeListNode oldNode = pltlm.nodeOf(droppedObjects[i], newParentNode);
						int oldIndex = newParentNode.getIndex(oldNode);
						if (insertionIndex>=oldIndex)
							delta=-1;
					}
					
					pltlm.moveProbeList(droppedObjects[i], newParentList, insertionIndex+delta);
				} catch (RuntimeException rte) {
					System.err.println(rte.getMessage());
					//rte.printStackTrace();
				}
			}
			
			plmvt.getProbeListManager().orderChangedExternally();
		}
		else
		{			
			DataSet targetDataSet = newParentList.getDataSet();
			DataSet sourceDataSet = droppedObjects[0].getDataSet();
			
			String sourceText = "Recieved from "+sourceDataSet.getName();
				
			MultiHashMap<String, Probe> displayCache = new MultiHashMap<String, Probe>();
			for (Probe pb : targetDataSet.getMasterTable().getProbes().values())
				displayCache.put(pb.getDisplayName(), pb);
			
			for (ProbeList sourcePL : droppedObjects) {
				ProbeList targetPL = new ProbeList(targetDataSet, true);
				targetPL.setName(sourcePL.getName());
				targetPL.getAnnotation().setQuickInfo(sourceText);
				targetPL.setColor(sourcePL.getColor());

				for (Probe sourcepb : sourcePL.getAllProbes()) {
					String nameCandidate = sourcepb.getName();				
					Probe targetpb = targetDataSet.getMasterTable().getProbe(nameCandidate);
					if (targetpb!=null) {
						targetPL.addProbe(targetpb);
					} else {
						for (Probe pb : displayCache.get(nameCandidate))
							if (!targetPL.contains(pb)) 
								targetPL.addProbe(pb);
					}
				}

				// now insert into the correct node
				targetPL.setParent((UnionProbeList)newParentList);
				plmvt.getProbeListManager().addObject(targetPL);

			}
							
		}

		
	}

	@Override
	protected Object getContextObject() {
		// no context info needed for this drag handler
		return null;
	}

}
