package mayday.vis3.plots.heatmap2.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.plots.heatmap2.columns.HeatmapColumn;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.interaction.HeatmapMouseWheelListener;
import mayday.vis3.plots.heatmap2.interaction.PassthroughMouseListener;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;
import mayday.vis3.plots.heatmap2.interaction.UpdateListener;

@SuppressWarnings("serial")
public class HeatmapCentralComponent extends JComponent implements PlotComponent, UpdateListener {

	protected HeatmapStructure data;
	
	public HeatmapCentralComponent( HeatmapStructure struct ) {
		data = struct;
		setBackground(Color.WHITE);
		
		addMouseWheelListener(new HeatmapMouseWheelListener(struct));
		
		PassthroughMouseListener ptl = new PassthroughMouseListener0();

		addMouseListener(ptl);
		addMouseWheelListener(ptl);
		addMouseMotionListener(ptl);

	}

	public void paint(Graphics gg) {

		Graphics2D g = (Graphics2D)gg;
		
		AffineTransform old = g.getTransform();
		Rectangle2D oldClip = g.getClipBounds();
		
		// find out which rows and columns to render
		int[] columns = data.getColumnsInView(g);
		int[] rows = data.getRowsInView(g);
		
		Rectangle2D cellRect = new Rectangle2D.Double();
		Rectangle viewRect = (Rectangle)oldClip.clone();

//		long l=0;
		
		for (int row=rows[0]; row<=rows[1]; ++row) {

			for (int col = columns[0]; col<=columns[1]; ++col) {

				HeatmapColumn hc = data.getColumn(col);
				
//				long l1 = System.currentTimeMillis();
				data.getCellRectangle(row,col,cellRect);
//				long l2 = System.currentTimeMillis();
//				l += (l2-l1);
				
				g.setClip(viewRect.createIntersection(cellRect));
				g.translate(cellRect.getX(), cellRect.getY());
				hc.render(g,row,col, data.isSelected(row)); 
				g.setTransform(old);
			}
		}
//		System.out.println("HCC: "+l+" ms");
		
		g.setClip(oldClip);

	}

	public Dimension getPreferredSize() {
		Dimension s = new Dimension((int)data.getTotalColumnWidth(),(int)data.getTotalRowHeight());
		return s;
	}
	
	@Override
	public void setup(PlotContainer plotContainer) {
		data.setData(plotContainer);	
		plotContainer.setPreferredTitle("Heatmap", this);
	}

	public void updatePlot() {
		throw new RuntimeException("HMC: update");
	}		
	

	public void addNotify() {
		super.addNotify();
		Component comp = this;
		while (comp!=null && !(comp instanceof PlotContainer)) {
			comp=comp.getParent();
		}
		if (comp!=null) {
			setup((PlotContainer)comp);
		}
	}
	
	public int coordinateToIndex( int mousePos ) {
		int index=data.getColumnAtPosition(mousePos);
		return index;
	}

	
	@Override
	public void elementNeedsUpdating(UpdateEvent evt) {
		if (evt.getChange()==UpdateEvent.REPAINT) {
			repaint(100);			
		}
	}
	
	
	public class PassthroughMouseListener0 extends PassthroughMouseListener {

		@Override
		protected MouseListener coordinateToListener(MouseEvent e) {
			int i = coordinateToIndex(e.getX());
			if (i>=0) 
				return data.getColumn(i).getMouseListener();
			return null;
		}

		@Override
		protected MouseMotionListener coordinateToMotionListener(MouseEvent e) {
			int i = coordinateToIndex(e.getX());
			if (i>=0) 
				return data.getColumn(i).getMouseMotionListener();
			return null;
		}

		@Override
		protected MouseWheelListener coordinateToWheelListener(MouseEvent e) {
			int i = coordinateToIndex(e.getX());
			if (i>=0) 
				return data.getColumn(i).getMouseWheelListener();
			return null;
		}

	}




}
