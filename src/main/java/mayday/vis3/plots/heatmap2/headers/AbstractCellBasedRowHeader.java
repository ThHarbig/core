package mayday.vis3.plots.heatmap2.headers;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import mayday.vis3.plots.heatmap2.data.HeatmapStructure;

public abstract class AbstractCellBasedRowHeader extends AbstractRowHeaderPlugin {

	public void renderCells(Graphics2D g, HeatmapStructure data) {

		AffineTransform old = g.getTransform();
		Rectangle2D oldClip = g.getClipBounds();
		Rectangle2D viewClip = g.getClipBounds();
		
		// find out which rows and columns to render
		int[] rows = data.getRowsInView(g);
		
		Rectangle2D cellRect = new Rectangle2D.Double();
		
		for (int row = rows[0]; row<=rows[1]; ++row) {			
			data.getCellRectangle(row,0,cellRect);
			cellRect.setFrame(cellRect.getX(), cellRect.getY(), getSize(), cellRect.getHeight());
			g.setClip(cellRect.createIntersection(viewClip));
			g.translate(0, cellRect.getY());
			renderCell(g,row); 
			g.setClip(oldClip);
			g.setTransform(old);
		}
		

	}
	
	public abstract void renderCell(Graphics2D g, int column);
}
