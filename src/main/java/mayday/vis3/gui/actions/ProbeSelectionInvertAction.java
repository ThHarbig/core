package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;

import mayday.core.Probe;
import mayday.vis3.model.ViewModel;

@SuppressWarnings("serial")
public class ProbeSelectionInvertAction extends AbstractAction {

	private ViewModel viewModel;
	
	public ProbeSelectionInvertAction(ViewModel viewModel) {
		super("Invert");
		this.viewModel = viewModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		Set<Probe> initial = viewModel.getSelectedProbes();
		Set<Probe> invert = new HashSet<Probe>(viewModel.getProbes());
		invert.removeAll(initial);
		viewModel.setProbeSelection(invert);
	}

}
