package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;
import java.util.HashSet;

import javax.swing.AbstractAction;

import mayday.core.Experiment;
import mayday.vis3.model.ViewModel;

@SuppressWarnings("serial")
public class ExperimentSelectionClearAction extends AbstractAction {

	private ViewModel viewModel;
	
	public ExperimentSelectionClearAction(ViewModel viewModel) {
		super("Clear");
		this.viewModel = viewModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		viewModel.setExperimentSelection(new HashSet<Experiment>());
	}

}
