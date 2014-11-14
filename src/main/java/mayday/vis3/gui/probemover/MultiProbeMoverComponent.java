package mayday.vis3.gui.probemover;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.LinkedList;

import javax.swing.JPanel;

import mayday.core.ProbeList;
import mayday.vis3.components.MultiPlotPanel;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.vis2base.DataSeries;


@SuppressWarnings("serial")
public class MultiProbeMoverComponent extends MultiPlotPanel implements ViewModelListener, PlotContainer {

	protected DataSeries selectionLayer;
	protected DataSeries[] Layers;
	protected ViewModel viewModel;

	public MultiProbeMoverComponent() {
	}

	public void setup(PlotContainer plotContainer) {
		super.setup(plotContainer);
		viewModel = plotContainer.getViewModel();
		viewModel.addViewModelListener(this);
		plotContainer.setPreferredTitle("Probe Copy/Move/Remove", this);
	}


	public void viewModelChanged(ViewModelEvent vme) {
		switch (vme.getChange()) {
		case ViewModelEvent.PROBELIST_SELECTION_CHANGED: // fallthrouh
		case ViewModelEvent.PROBELIST_ORDERING_CHANGED:
			updatePlot();
			break;
		case ViewModelEvent.PROBE_SELECTION_CHANGED: // ignore
			break;
		}	
	}

	public void updatePlot() {
		int oldNumber = plots.length;
		LinkedList<Component> pcs = new LinkedList<Component>();
		for (ProbeList pl : viewModel.getProbeLists(false)) {
			ProbeMoverComponent ppcm = new ProbeMoverComponent(pl);
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(ppcm, BorderLayout.CENTER);
			pcs.add((Component)panel); 
		}

		if (oldNumber==pcs.size())
			setPlots(pcs, dimensions);
		else 
			setPlots(pcs);
	}
	
	public void addNotify() {
		super.addNotify();
		updatePlot();
	}

}

