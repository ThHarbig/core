package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;
import java.util.HashSet;

import javax.swing.AbstractAction;

import mayday.core.Probe;
import mayday.vis3.model.ViewModel;

@SuppressWarnings("serial")
public class ProbeSelectionClearAction extends AbstractAction {

	private ViewModel viewModel;
	
	public ProbeSelectionClearAction(ViewModel viewModel) {
		super("Clear");
		this.viewModel = viewModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		viewModel.setProbeSelection(new HashSet<Probe>());
	}

}
