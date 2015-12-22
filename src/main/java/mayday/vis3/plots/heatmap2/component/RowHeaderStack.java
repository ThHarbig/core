package mayday.vis3.plots.heatmap2.component;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import mayday.vis3.components.AntiAliasPlotPanel;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.headers.HeaderElement;
import mayday.vis3.plots.heatmap2.headers.RowHeaderElement;
import mayday.vis3.plots.heatmap2.interaction.PassthroughMouseListener;

@SuppressWarnings("serial")
public class RowHeaderStack extends AntiAliasPlotPanel {

	HeatmapStructure hms;
	int spacing;

	public RowHeaderStack(HeatmapStructure data, int spacing) {
		hms = data;
		this.spacing=spacing;
		PassthroughMouseListener ptl = new PassthroughMouseListener0();
		addMouseListener(ptl);
		addMouseWheelListener(ptl);
		addMouseMotionListener(ptl);
	}

	public Dimension getPreferredSize() {
		Dimension s = new Dimension(0, (int)hms.getTotalRowHeight());
		for (HeaderElement he : hms.getRowHeaderElements()) {
			s.width+=he.getSize()+spacing;			
		}		
		return s;
	}

	@Override
	public void paintPlot(Graphics2D g) {
		AffineTransform at = g.getTransform();
		AffineTransform old = (AffineTransform)at.clone();

		// get the outer clip due to scrolling
		Rectangle2D outerClip = g.getClipBounds();

		for (RowHeaderElement he : hms.getRowHeaderElements()) {
			Rectangle2D innerClip = new Rectangle2D.Double(0, 0, he.getSize(), hms.getTotalRowHeight());
			g.setClip(innerClip.createIntersection(outerClip)); 
			he.render(g);
			at.translate(he.getSize()+spacing, 0);
			g.setTransform(at);
		}

		g.setTransform(old);
		g.setClip(outerClip);

	}

	@Override
	public void setup(PlotContainer plotContainer) {
		// ignore
	}	

	public int coordinateToIndex( int mousePos ) {
		double width=0;
		int index=0;
		for (HeaderElement he : hms.getRowHeaderElements()) {
			width+=he.getSize()+spacing;
			if (mousePos < width)
				return index;
			++ index;
		}	
		return -1;
	}

	public class PassthroughMouseListener0 extends PassthroughMouseListener {

		@Override
		protected MouseListener coordinateToListener(MouseEvent e) {
			int i = coordinateToIndex(e.getX());
			if (i>=0) 
				return hms.getRowHeaderElements().get(i).getMouseListener();
			return null;
		}

		@Override
		protected MouseMotionListener coordinateToMotionListener(MouseEvent e) {
			int i = coordinateToIndex(e.getX());
			if (i>=0) 
				return hms.getRowHeaderElements().get(i).getMouseMotionListener();
			return null;
		}

		@Override
		protected MouseWheelListener coordinateToWheelListener(MouseEvent e) {
			int i = coordinateToIndex(e.getX());
			if (i>=0) 
				return hms.getRowHeaderElements().get(i).getMouseWheelListener();
			return null;
		}

	}



}
