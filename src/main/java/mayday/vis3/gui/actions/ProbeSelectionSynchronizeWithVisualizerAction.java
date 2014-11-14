package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.vis3.gui.VisualizerSelectionDialog;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.model.Visualizer;

@SuppressWarnings("serial")
public class ProbeSelectionSynchronizeWithVisualizerAction extends AbstractAction implements ViewModelListener{

	private ViewModel viewModel1;
	private ViewModel viewModel2;
	
	public ProbeSelectionSynchronizeWithVisualizerAction(ViewModel viewModel) {
		super("Synchronize selection with Visualizer...");
		this.viewModel1 = viewModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (viewModel2!=null) {
			viewModel1.removeViewModelListener(this);
			viewModel2.removeViewModelListener(this);
			viewModel2 = null;
			putValue("Name", "Synchronize selection with Visualizer...");
		} else {
			VisualizerSelectionDialog vsd = new VisualizerSelectionDialog(null);
			vsd.setModal(true);
			vsd.setVisible(true);		
			List<Visualizer> vss = vsd.getSelection();		
			if (vss.size()>0) {
				Visualizer vizz = vss.get(0);
				viewModel2 = vizz.getViewModel();
				if (viewModel1!=viewModel2) {
					viewModel1.addViewModelListener(this);
					viewModel2.addViewModelListener(this);
					viewModelChanged(new ViewModelEvent(viewModel1, ViewModelEvent.PROBE_SELECTION_CHANGED));
					putValue("Name", "Stop synchronizing selection");
				}
			}			
		}
	}

	protected boolean reentry = false;
	
	public void viewModelChanged(ViewModelEvent vme) {
		if (reentry)
			return;
		try {
			reentry=true;
		
			if (vme.getChange()!=ViewModelEvent.PROBE_SELECTION_CHANGED)
				return;
		
			ViewModel src, dst;
			src = (ViewModel)vme.getSource();
			if (src==viewModel1)
				dst=viewModel2;
			else 
				dst=viewModel1;

			Set<Probe> targetSelection  = src.getSelectedProbes();

			DataSet targetDS = dst.getDataSet();
			DataSet sourceDS = src.getDataSet();
			
			if (targetDS != sourceDS) {			
				targetSelection = new HashSet<Probe>();
				for (Probe pb : src.getSelectedProbes()) {
					Probe pb2 = targetDS.getMasterTable().getProbe(pb.getName());
					if (pb2!=null && dst.getProbes().contains(pb2))
						targetSelection.add(pb2);
				}
			}
			dst.setProbeSelection(targetSelection);
		} finally {		
			reentry = false;
		}
	}

}
