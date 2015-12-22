package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.probelist.ProbeListSelectionDialog;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.wrapped.WrappedProbe;

@SuppressWarnings("serial")
public class ProbeSelectionSendToProbeList extends AbstractAction {

	private ViewModel viewModel;
	
	public ProbeSelectionSendToProbeList(ViewModel viewModel) {
		super("Send selection to ProbeList...");
		this.viewModel = viewModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		Collection<Probe> sourceSelection = viewModel.getSelectedProbes();
			
		ProbeListSelectionDialog vsd = new ProbeListSelectionDialog(viewModel.getDataSet().getProbeListManager());
		vsd.setDialogDescription("Select ProbeLists to copy the selected probes into");
		vsd.setModal(true);
		vsd.setVisible(true);		
		List<ProbeList> vss = vsd.getSelection();
		for (ProbeList tgtPL : vss) {
			for (Probe pb : sourceSelection) {
				if (!tgtPL.contains(pb)) {
					if (pb instanceof WrappedProbe)
						pb = ((WrappedProbe)pb).getWrappedProbe();
					tgtPL.addProbe(pb);
				}
			}
		}
	}

}
