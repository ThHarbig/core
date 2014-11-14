package mayday.vis3.plots.pca;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.Experiment;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.math.JamaSubset.Matrix;
import mayday.core.plugins.probe.ProbeMenu;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;

@SuppressWarnings("serial")
public class XYScatterPlot_Experiments extends XYScatterPlot<Experiment> {

	public XYScatterPlot_Experiments(Matrix pcaData, int dim1, int dim2, List<Experiment> experimentsOnDisplay) {
		super(pcaData, dim1, dim2, experimentsOnDisplay);

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				double[] clicked = getPoint(e.getX(), e.getY());
				switch (e.getButton()) {
				case MouseEvent.BUTTON1:
					Experiment pb = (Experiment)szb.getObject(clicked[0], clicked[1]);
					if (pb!=null) {
						if (e.getClickCount()==2) {
							PropertiesDialogFactory.createDialog(pb).setVisible(true);
						} else {
							int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();
							if ((e.getModifiers()&CONTROLMASK) == CONTROLMASK) {
								// 			toggle selection of the clicked probe
								viewModel.toggleExperimentSelected(pb);
							} else {
								// 			select only one probe
								viewModel.setExperimentSelection(pb);
							}
						}
					}
					break;
				case MouseEvent.BUTTON3:
					ProbeMenu pm = new ProbeMenu(viewModel.getSelectedProbes(), viewModel.getDataSet().getMasterTable());
					pm.getPopupMenu().show(XYScatterPlot_Experiments.this, e.getX(), e.getY());
					break;
				}
			}		
		});
		
		
	}

	protected void updateSelection(Set<Experiment> newSelection, boolean control, boolean alt) {
		
		Set<Experiment> previousSelection = viewModel.getSelectedExperiments();
		if (control && alt) {
			previousSelection = new HashSet<Experiment>(previousSelection);
			previousSelection.removeAll(newSelection);
			newSelection = previousSelection;
		} else if (control) {
			newSelection.addAll(previousSelection);
		} else if (alt) {
			newSelection.retainAll(previousSelection);
		} else {
			// nothing to do with prev selection
		}

		viewModel.setExperimentSelection(newSelection);
	}
	

	public void viewModelChanged(ViewModelEvent vme) {
		switch (vme.getChange()) {
		case ViewModelEvent.EXPERIMENT_SELECTION_CHANGED:
			select(Color.RED);
			break;
		}
	}
	
	public void select(Color selection_color)
	{
		if (selectionLayer!=null)
			removeDataSeries(selectionLayer);
		
		Set<Experiment> s = new HashSet<Experiment>();
		s.addAll(viewModel.getDataSet().getMasterTable().getExperiments());
		
		s.retainAll(viewModel.getSelectedExperiments());
		
		selectionLayer = doSelect1(s);
		if (selectionLayer!=null) {
			selectionLayer.setColor(Color.RED);
			addDataSeries(selectionLayer);
		}
		clearBuffer();
		repaint();
	}

	@Override
	protected void setup0(PlotContainer plotContainer) {
		// nothing to do
	}
	
}
