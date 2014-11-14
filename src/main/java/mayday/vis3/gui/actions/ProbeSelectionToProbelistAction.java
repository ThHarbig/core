package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.core.meta.MIGroup;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.wrapped.WrappedProbe;

@SuppressWarnings("serial")
public class ProbeSelectionToProbelistAction extends AbstractAction {

	private ViewModel viewModel;
	
	public ProbeSelectionToProbelistAction(ViewModel viewModel) {
		super("Create ProbeList...");
		this.viewModel = viewModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		ProbeList pl = new ProbeList(viewModel.getDataSet(), true);
		for (Probe pb : viewModel.getSelectedProbes()) {
			if (pb instanceof WrappedProbe)
				pb = ((WrappedProbe)pb).getWrappedProbe();
			pl.addProbe(pb);
		}
		pl.setName("Selected Probes");
		AbstractPropertiesDialog apd = PropertiesDialogFactory.createDialog(pl);
		apd.setModal(true);
		apd.setVisible(true);
		if (apd.isCancelled()) {
			pl.clearProbes();
			for (MIGroup mg : viewModel.getDataSet().getMIManager().getGroupsForObject(pl)) {
				mg.remove(pl);
			}
		} else {
			viewModel.getDataSet().getProbeListManager().addObjectAtTop(pl);
		}
	}

}
