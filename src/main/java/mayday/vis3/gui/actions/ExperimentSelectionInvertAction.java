package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;

import mayday.core.Experiment;
import mayday.vis3.model.ViewModel;

@SuppressWarnings("serial")
public class ExperimentSelectionInvertAction extends AbstractAction {

	private ViewModel viewModel;
	
	public ExperimentSelectionInvertAction(ViewModel viewModel) {
		super("Invert");
		this.viewModel = viewModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		Set<Experiment> initial = viewModel.getSelectedExperiments();
		Set<Experiment> invert = new HashSet<Experiment>(viewModel.getDataSet().getMasterTable().getExperiments());
		invert.removeAll(initial);
		viewModel.setExperimentSelection(invert);
	}

}
