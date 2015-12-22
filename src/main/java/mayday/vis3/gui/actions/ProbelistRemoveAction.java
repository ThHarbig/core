package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.ProbeList;
import mayday.core.gui.probelist.ProbeListSelectionDialog;
import mayday.core.gui.probelist.ProbeListSelectionFilter;
import mayday.vis3.model.ViewModel;

@SuppressWarnings("serial")
public class ProbelistRemoveAction extends AbstractAction {

	private ViewModel viewModel;
	
	public ProbelistRemoveAction(ViewModel viewModel) {
		super("Remove...");
		this.viewModel = viewModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		ProbeListSelectionDialog plsd = new ProbeListSelectionDialog(viewModel.getDataSet().getProbeListManager());
		plsd.setFilter(new ProbeListSelectionFilter() {
			public boolean pass(ProbeList pl) {
				return (viewModel.getProbeLists(false).contains(pl));
			}
		});	
		plsd.setDialogDescription("Select ProbeLists to remove from the visualizer.");
		plsd.setVisible(true);
		for (ProbeList pl : plsd.getSelection()) {
			viewModel.removeProbeListFromSelection(pl);
		}
	}

}
