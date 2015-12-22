package mayday.vis3.plots.genomeviz.genomeheatmap.delegates;

import java.awt.Rectangle;

import javax.swing.JViewport;

import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTable;

public class VisRangeMovements {
	
	/**
	 * Scrolls the wanted cell into the middle of visible rect.
	 * @param cellRect
	 * @param table
	 */
	public static void centerView(Rectangle cellRect, GenomeHeatMapTable table){
		
		Rectangle viewRect = ((JViewport) table.getParent()).getViewRect();
//		int w = viewRect.width;
		int h = viewRect.height;

		//cellRect.x -= (w / 2 - cellRect.width / 2);
		cellRect.y -= (h / 2 - cellRect.height / 2);
		//cellRect.width = w;
		cellRect.height = h;
		
		table.scrollRectToVisible(cellRect);
	}
}
