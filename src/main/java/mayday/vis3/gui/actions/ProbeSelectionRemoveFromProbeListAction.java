package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;
import java.util.LinkedList;

import javax.swing.AbstractAction;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.vis3.model.ViewModel;

@SuppressWarnings("serial")
public class ProbeSelectionRemoveFromProbeListAction extends AbstractAction {

	private ViewModel viewModel;
	
	public ProbeSelectionRemoveFromProbeListAction(ViewModel viewModel) {
		super("Remove from ProbeList(s)");
		this.viewModel = viewModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		LinkedList<Probe> lpl = new LinkedList<Probe>(viewModel.getSelectedProbes());
		for (Probe pb : lpl) {
			for (ProbeList pl : viewModel.getProbeLists(false))
				if (pl.contains(pb))
					pl.removeProbe(pb);
		}
	}

}
