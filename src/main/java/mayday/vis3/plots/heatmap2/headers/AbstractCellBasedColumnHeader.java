package mayday.vis3.plots.heatmap2.headers;

import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;

public abstract class AbstractCellBasedColumnHeader extends AbstractColumnHeaderPlugin {

	public void renderCells(Graphics2D g, HeatmapStructure data, AbstractColumnGroupPlugin group) {

		AffineTransform old = g.getTransform();
		Rectangle2D oldClip = g.getClipBounds();
		Rectangle2D viewClip = g.getClipBounds();
		
		// find out which rows and columns to render
		int[] columns = data.getColumnsInView(g);
		
		Rectangle2D cellRect = new Rectangle2D.Double();
		
		for (int col = columns[0]; col<=columns[1]; ++col) {			
			data.getCellRectangle(0,col,cellRect);
			cellRect.setFrame(cellRect.getX(), cellRect.getY(),cellRect.getWidth(),  getSize());
			g.setClip(cellRect.createIntersection(viewClip));
			g.translate(cellRect.getX(), 0);
			renderCell(g,col,group); 
			g.setClip(oldClip);
			g.setTransform(old);
		}
		
	}
	
	@Override
	public MouseListener getMouseListener() {
		return null;
	}

	@Override
	public MouseMotionListener getMouseMotionListener() {
		return null;
	}

	@Override
	public MouseWheelListener getMouseWheelListener() {
		return null;
	}
	
	public abstract void renderCell(Graphics2D g, int column, AbstractColumnGroupPlugin group);
}
