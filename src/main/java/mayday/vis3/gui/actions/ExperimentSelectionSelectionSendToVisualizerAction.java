package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractAction;

import mayday.core.DataSet;
import mayday.core.Experiment;
import mayday.vis3.gui.VisualizerSelectionDialog;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.Visualizer;

@SuppressWarnings("serial")
public class ExperimentSelectionSelectionSendToVisualizerAction extends AbstractAction {

	private ViewModel viewModel;
	
	public ExperimentSelectionSelectionSendToVisualizerAction(ViewModel viewModel) {
		super("Send selection to Visualizer...");
		this.viewModel = viewModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		DataSet targetDS = null;
		Collection<Experiment> sourceSelection = viewModel.getSelectedExperiments();
		Collection<Experiment> targetSelection = sourceSelection;
			
		VisualizerSelectionDialog vsd = new VisualizerSelectionDialog(targetDS);
		vsd.setModal(true);
		vsd.setVisible(true);		
		List<Visualizer> vss = vsd.getSelection();		
		if (vss.size()>0) {
			Visualizer vizz = vss.get(0);
			targetDS = vizz.getViewModel().getDataSet();
			if (targetDS != viewModel.getDataSet()) {
				targetSelection = new HashSet<Experiment>();
				for (Experiment ex : sourceSelection) {
					Experiment ex2 = targetDS.getMasterTable().getExperiment(ex.getName());
					if (ex2!=null)
						targetSelection.add(ex2);
				}
			}
			vizz.getViewModel().setExperimentSelection(targetSelection);
		}
	}

}
