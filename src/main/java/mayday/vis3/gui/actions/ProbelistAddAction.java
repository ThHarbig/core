package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import mayday.core.ProbeList;
import mayday.core.gui.probelist.ProbeListSelectionDialog;
import mayday.core.gui.probelist.ProbeListSelectionFilter;
import mayday.vis3.model.ViewModel;

@SuppressWarnings("serial")
public class ProbelistAddAction extends AbstractAction {

	private ViewModel viewModel;
	
	public ProbelistAddAction(ViewModel viewModel) {
		super("Add...");
		this.viewModel = viewModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		ProbeListSelectionDialog plsd = new ProbeListSelectionDialog(viewModel.getDataSet().getProbeListManager());
		plsd.setFilter(new ProbeListSelectionFilter() {
			public boolean pass(ProbeList pl) {
				return !(viewModel.getProbeLists(false).contains(pl));
			}
		});		
		plsd.setDialogDescription("Select ProbeLists to add to the visualizer.");
		plsd.setVisible(true);
		for (ProbeList pl : plsd.getSelection()) {
			viewModel.addProbeListToSelection(pl);
		}
		if (plsd.getSelection().size()==0)
			JOptionPane.showMessageDialog(null, "No probe lists to add.", "Nothing added", JOptionPane.OK_OPTION);
	}

}
