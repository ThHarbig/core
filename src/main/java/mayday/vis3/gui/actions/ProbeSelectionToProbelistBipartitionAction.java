package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;
import java.util.HashSet;

import javax.swing.AbstractAction;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.wrapped.WrappedProbe;

@SuppressWarnings("serial")
public class ProbeSelectionToProbelistBipartitionAction extends AbstractAction {

	private ViewModel viewModel;
	
	public ProbeSelectionToProbelistBipartitionAction(ViewModel viewModel) {
		super("Create ProbeList Bipartition...");
		this.viewModel = viewModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		ProbeList pl1 = new ProbeList(viewModel.getDataSet(), true);		
		for (Probe pb : viewModel.getSelectedProbes()) {
			if (pb instanceof WrappedProbe)
				pb = ((WrappedProbe)pb).getWrappedProbe();
			pl1.addProbe(pb);
		}
		pl1.setName("Bipartition A");
		
		ProbeList pl2 = new ProbeList(viewModel.getDataSet(), true);				
		HashSet<Probe> hp = new HashSet<Probe>(viewModel.getProbes());
		hp.removeAll(viewModel.getSelectedProbes());		
		for (Probe pb : hp) {
			if (pb instanceof WrappedProbe)
				pb = ((WrappedProbe)pb).getWrappedProbe();
			pl2.addProbe(pb);
		}
		pl2.setName("Bipartition B");

		viewModel.getDataSet().getProbeListManager().addObjectAtTop(pl2);
		viewModel.getDataSet().getProbeListManager().addObjectAtTop(pl1);

		AbstractPropertiesDialog apd1 = PropertiesDialogFactory.createDialog(pl1);
		apd1.setModal(false);
		apd1.setVisible(true);
		
		AbstractPropertiesDialog apd2 = PropertiesDialogFactory.createDialog(pl2);
		apd2.setModal(false);
		apd2.setVisible(true);
	}

}
