package mayday.vis3.plots.genomeviz.genomeheatmap.view.cellrenderer;

import java.awt.Color;
import java.awt.Graphics;

import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.CellObject;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.MyColorProvider;

public class BoxRendererMIO extends AbstractBoxRenderer{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7910958559060103605L;

	public BoxRendererMIO(MyColorProvider coloring,  MasterManager master, GenomeHeatMapTableModel model){
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
		
		Color color = coloring.getColorByMioForProbelist(selectedProbes, model
				.getSplitView());
		if (color != null) {
			setBackground(color);
			setOpaque(true);
		} else {
			setOpaque(false);
		}
	}
}
