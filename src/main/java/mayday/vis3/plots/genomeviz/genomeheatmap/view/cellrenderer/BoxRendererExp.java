package mayday.vis3.plots.genomeviz.genomeheatmap.view.cellrenderer;

import java.awt.Color;
import java.awt.Graphics;

import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.CellObject;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.MyColorProvider;


public class BoxRendererExp extends AbstractBoxRenderer {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4654001942991783977L;

	public BoxRendererExp(MyColorProvider coloring, MasterManager master, GenomeHeatMapTableModel model){
		super(coloring,master,model);
	
	}
	
	public void paintComponent(Graphics g) {		
		super.paintComponent(g);
	}

	// set probes which contains probes
	protected void setProbeCell(CellObject cellObject, int row, int col,
			boolean isSelected) {
		selectedProbes = cellObject.getProbes();

		this.setToolTipText(getWholeToolTipText(cellObject));

		Double doubleValue = null;
		int actualExperiment = model.getActualExperimentNumber();
//		int previousExperiment = model.getPreviousExperimentNumber();

//		if (model.getDifferenceCheckbox()
//				&& actualExperiment != previousExperiment) {
//			doubleValue = getValueOfDifference(actualExperiment,
//					previousExperiment);
//
//		} else 
		{
			switch (model.getSplitView()) {
			case mean:
				doubleValue = viewModel.getMean(actualExperiment,
						selectedProbes);
				break;
			case max:
				doubleValue = viewModel.getMaximum(actualExperiment,
						selectedProbes);
				break;
			case min:
				doubleValue = viewModel.getMinimum(actualExperiment,
						selectedProbes);
				break;
			default:
				doubleValue = null;
			}
		}
		setBackgroundColor_byValue(doubleValue);
	}

//	private Double getValueOfDifference(int actualExperiment,
//			int previousExperiment) {
//		Double doubleValue;
//		Double prevdoubleValue = 0.;
//
//			switch (model.getSplitView()) {
//			case first:
//				doubleValue = master.getViewModel().getProbeValues(
//						selectedProbes.get(0))[actualExperiment];
//				prevdoubleValue = master.getViewModel().getProbeValues(
//						selectedProbes.get(0))[previousExperiment];
//
//				doubleValue = doubleValue - prevdoubleValue;
//				break;
//			case mean:
//				doubleValue = viewModel.getMean(actualExperiment, selectedProbes);
//				prevdoubleValue = viewModel.getMean(previousExperiment, selectedProbes);
//
//				doubleValue = doubleValue - prevdoubleValue;
//				break;
//			case max:
//				doubleValue = viewModel.getMaximum(actualExperiment, selectedProbes);
//				prevdoubleValue = viewModel.getMaximum(previousExperiment, selectedProbes);
//
//				doubleValue = doubleValue - prevdoubleValue;
//				break;
//			case min:
//				doubleValue = viewModel.getMinimum(actualExperiment, selectedProbes);
//				prevdoubleValue = viewModel.getMinimum(previousExperiment, selectedProbes);
//
//				doubleValue = doubleValue - prevdoubleValue;
//				break;
//			default:
//				doubleValue = null;
//
//		}
//		return doubleValue;
//	}

	public void setBackgroundColor_byValue(Double dvalue) {

		if (dvalue != null && !Double.isNaN(dvalue)) {
			Color l_color = coloring.getColor(dvalue);
			setBackground(l_color);
			setOpaque(true);
		} else {
			setOpaque(false);
		}
	}
}
