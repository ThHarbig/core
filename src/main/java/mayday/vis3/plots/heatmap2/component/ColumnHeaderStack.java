package mayday.vis3.plots.heatmap2.component;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;

import mayday.vis3.components.AntiAliasPlotPanel;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.headers.ColumnHeaderElement;
import mayday.vis3.plots.heatmap2.headers.HeaderElement;
import mayday.vis3.plots.heatmap2.interaction.PassthroughMouseListener;

@SuppressWarnings("serial")
public class ColumnHeaderStack extends AntiAliasPlotPanel {

	HeatmapStructure hms;
	int spacing;
	
	public ColumnHeaderStack(HeatmapStructure data, int spacing) {
		hms = data;
		this.spacing=spacing;
		
		PassthroughMouseListener ptl = new PassthroughMouseListener0();
		addMouseListener(ptl);
		addMouseWheelListener(ptl);
		addMouseMotionListener(ptl);
	}
	
	public Dimension getPreferredSize() {
		Dimension s = new Dimension((int)hms.getTotalColumnWidth(),0);
		double maximumStackHeight=0;
		for (AbstractColumnGroupPlugin hcg : hms.getColumnGroups()) {
			double sh =0;
			for (HeaderElement he : hcg.getColumnHeaderElements()) {
				sh+=he.getSize()+spacing;
			}
			maximumStackHeight = Math.max(maximumStackHeight,sh); 
		}
		s.height = (int)maximumStackHeight;			
		return s;
	}
	
	@Override
	public void paintPlot(Graphics2D g) {
		AffineTransform old = g.getTransform();
		
		// get the outer clip due to scrolling
		Rectangle2D outerClip = g.getClipBounds();
		
		g.clearRect((int)outerClip.getX(), (int)outerClip.getY(), (int)outerClip.getWidth(), (int)outerClip.getHeight());
		
		int colIdx=0;
		
		for (AbstractColumnGroupPlugin hcg : hms.getColumnGroups()) {
			
			// find out which columns are in there
			double xStart = hms.getColStart(colIdx);
			int colIdx2 = colIdx+hcg.getColumns().size()-1;
			double xEnd = hms.getColStart(colIdx2)+hms.getColWidth(colIdx2);
			AffineTransform at = (AffineTransform)old.clone();
			
			at.translate(0, getPreferredSize().height);
			
			List<ColumnHeaderElement> hes = hcg.getColumnHeaderElements();
			for (int i=hes.size()-1; i>=0; --i) {
				// render the stack of all the elements here
				ColumnHeaderElement he = hes.get(i);
				at.translate(0, -he.getSize()-spacing);
				g.setTransform(at);
				Rectangle2D innerClip = new Rectangle2D.Double(xStart, 0, xEnd-xStart, he.getSize());
				g.setClip(innerClip.createIntersection(outerClip)); 
				he.render(g, hcg);
			}									
			
			g.setTransform(old);
			g.setClip(outerClip);		
			
			colIdx = colIdx2+1;		
		}
		
	}

	@Override
	public void setup(PlotContainer plotContainer) {
		// ignore		
	}
	
	public ColumnHeaderElement coordinateToElement( int mousePosX, int mousePosY ) {
		int colIdx=0;

		for (AbstractColumnGroupPlugin hcg : hms.getColumnGroups()) {
			
			// find out which columns are in there
			double xStart = hms.getColStart(colIdx);
			int colIdx2 = colIdx+hcg.getColumns().size()-1;
			double xEnd = hms.getColStart(colIdx2)+hms.getColWidth(colIdx2);
			
			if (mousePosX>=xStart && mousePosX<=xEnd) {
				double yEnd = getPreferredSize().height;
				
				List<ColumnHeaderElement> hes = hcg.getColumnHeaderElements();
				for (int i=hes.size()-1; i>=0; --i) {
					// 	render the stack of all the elements here
					ColumnHeaderElement he = hes.get(i);
					double yStart = yEnd-he.getSize()-spacing;
					if (mousePosY>=yStart && mousePosY<=yEnd) {
						return he;
					}
					yEnd = yStart-1;
				}									
			}
			colIdx = colIdx2+1;		
		}
		
		return null;
		
	}

	public class PassthroughMouseListener0 extends PassthroughMouseListener {

		@Override
		protected MouseListener coordinateToListener(MouseEvent e) {
			ColumnHeaderElement che = coordinateToElement(e.getX(), e.getY());
			if (che!=null) 
				return che.getMouseListener();
			return null;
		}

		@Override
		protected MouseMotionListener coordinateToMotionListener(MouseEvent e) {
			ColumnHeaderElement che = coordinateToElement(e.getX(), e.getY());
			if (che!=null) 
				return che.getMouseMotionListener();
			return null;
		}

		@Override
		protected MouseWheelListener coordinateToWheelListener(MouseEvent e) {
			ColumnHeaderElement che = coordinateToElement(e.getX(), e.getY());
			if (che!=null) 
				return che.getMouseWheelListener();
			return null;
		}

	}
}
