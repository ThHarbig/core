package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;

import mayday.core.Experiment;
import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.tasks.AbstractTask;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.subset.SubsetVisualizer;
import mayday.vis3.model.wrapped.WrappedExperiment;
import mayday.vis3.model.wrapped.WrappedMasterTable;
import mayday.vis3.model.wrapped.WrappedProbeList;

@SuppressWarnings("serial")
public class ExperimentSelectionCreateVisualizerAction extends AbstractAction {

	private ViewModel viewModel;

	public ExperimentSelectionCreateVisualizerAction(ViewModel viewModel) {
		super("Plot in separate visualizer");
		this.viewModel = viewModel;
	}

	public void actionPerformed(ActionEvent e) {

		AbstractTask at = new AbstractTask("Preparing for subset generation") {
			public void doWork() {
				Set<Experiment> initial = viewModel.getSelectedExperiments();

				// first, if the viewmodel is already restricted, UN-restrict the data before restricting again

				MasterTable mt = viewModel.getDataSet().getMasterTable();
				if (mt instanceof WrappedMasterTable)
					mt = ((WrappedMasterTable)mt).getWrappedMasterTable();

				List<Experiment> le = new ArrayList<Experiment>();

				for (Experiment ex : initial) {
					if (ex instanceof WrappedExperiment) 
						ex = ((WrappedExperiment)ex).getWrappedExperiment();
					le.add(ex);
				}		

				Collections.sort(le);

				List<ProbeList> pls = viewModel.getProbeLists(false);
				for (int i=0; i!=pls.size(); ++i) {
					ProbeList pl = pls.get(i);
					if (pl instanceof WrappedProbeList)
						pls.set(i, ((WrappedProbeList)pl).getWrappedProbeList());
				}

				SubsetVisualizer.createVisualizer(le, mt, pls);

			}

			@Override
			protected void initialize() {}
			
		};
		at.start();
	}

}
