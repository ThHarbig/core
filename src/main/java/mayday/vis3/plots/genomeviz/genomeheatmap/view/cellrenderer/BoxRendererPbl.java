package mayday.vis3.plots.genomeviz.genomeheatmap.view.cellrenderer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mayday.core.Probe;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.CellObject;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.MyColorProvider;

public class BoxRendererPbl extends AbstractBoxRenderer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3712318462984742893L;

	public BoxRendererPbl(MyColorProvider coloring, MasterManager master, GenomeHeatMapTableModel model){
		super(coloring,master,model);
	}
	
	// set probes which contains probes
	protected void setProbeCell(CellObject cellObject, int row, int col,
			boolean isSelected) {
		selectedProbes = cellObject.getProbes();

		this.setToolTipText(getWholeToolTipText(cellObject));

		setBackgroundColor_byProbelist(cellObject);

	}
	
	public void setBackgroundColor_byProbelist(CellObject cellObj) {
		if (cellObj != null) {
			List<Probe> list = cellObj.getProbes();
			for (Probe pb : list) {
				Color col = coloring.getColor(pb);
				if(colordistribution.containsKey(col)){
					int occ = colordistribution.get(col);
					occ+=1;
					colordistribution.put(col, occ);
				} else {
					colordistribution.put(col, 1);
				}	
			}
			paintMe = true;
		} else {
			setOpaque(false);
		}
	}
	
	// paint number of colors each probelist in one cell
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
	
	protected void drawCellcolorByProbelist(Graphics2D g2_help) {
		int width = model.getTableSettings().getBoxSizeX();
		int height = model.getTableSettings().getBoxSizeY();
		Set<Color> colorset = colordistribution.keySet();
		int colorCount = colorset.size();
		Rectangle2D.Double r2d = null;
		
		switch (model.getPbListColoring()){
		case COLOR_ALL_PROBELISTS:
			ArrayList<Color> colArray = new ArrayList<Color>();

			for(Color col: colorset){
				colArray.add(col);
			}

			double oneColorWidth = ((double) width) / ((double) colorCount);

			r2d = new Rectangle2D.Double(0, 0, Math.max(
					oneColorWidth, 1.0),
					height);

			for (int i = 0; i != colorCount; ++i) {
				g2_help.setColor(colArray.get(i));
				r2d.x = i * oneColorWidth;
				g2_help.fill(r2d);
			}
			break;
			
		case COLOR_HIGHEST_PROBELIST:
			Color highestCol = null;
			int occ = 0;
			for(Color col: colorset){
				int val = colordistribution.get(col);
				if (val > occ){
					occ = val;
					highestCol = col;
				}
			}
			
			r2d = new Rectangle2D.Double(0, 0,Math.max(
					width, 1.0),
					height);

			for (int i = 0; i != colorCount; ++i) {
				g2_help.setColor(highestCol);
				r2d.x = i * width;
				g2_help.fill(r2d);
			}
			break;
		default:
			System.err.println("ChromeBoxRenderer - drarCellcolorByProbelist: " +
					"selected probelist coloring not valid");	
		}
	}
}
