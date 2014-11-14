package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractAction;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.vis3.gui.VisualizerSelectionDialog;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.Visualizer;

@SuppressWarnings("serial")
public class ProbeSelectionSendToVisualizerAction extends AbstractAction {

	private ViewModel viewModel;
	
	public ProbeSelectionSendToVisualizerAction(ViewModel viewModel) {
		super("Send selection to Visualizer...");
		this.viewModel = viewModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		DataSet targetDS = null;
		Collection<Probe> sourceSelection = viewModel.getSelectedProbes();
		Collection<Probe> targetSelection = sourceSelection;
			
		VisualizerSelectionDialog vsd = new VisualizerSelectionDialog(targetDS);
		vsd.setModal(true);
		vsd.setVisible(true);		
		List<Visualizer> vss = vsd.getSelection();		
		if (vss.size()>0) {
			Visualizer vizz = vss.get(0);
			targetDS = vizz.getViewModel().getDataSet();
			if (targetDS != viewModel.getDataSet()) {
				targetSelection = new HashSet<Probe>();
				for (Probe pb : sourceSelection) {
					Probe pb2 = targetDS.getMasterTable().getProbe(pb.getName());
					if (pb2!=null)
						targetSelection.add(pb2);
				}
			}
			vizz.getViewModel().setProbeSelection(targetSelection);
		}
	}

}
