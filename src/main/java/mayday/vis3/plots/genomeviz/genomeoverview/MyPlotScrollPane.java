package mayday.vis3.plots.genomeviz.genomeoverview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

import javax.swing.JPanel;
import javax.swing.JViewport;

import mayday.vis3.components.PlotScrollPane;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeoverview.panels.ChromosomeHeaderPanel;

public class MyPlotScrollPane extends PlotScrollPane{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3282535023110663024L;
	protected GenomeOverviewModel chromeModel = null;
	protected ChromosomeHeaderPanel chromeHeaderPanel = null;
	protected Controller c;

	public MyPlotScrollPane(Component comp, GenomeOverviewModel ChromeModel, Controller controller){
		super(comp);
		c = controller;
		chromeModel = ChromeModel;
		addMouseListener(c);
		addMouseWheelListener(c);
	}
	


	public Dimension getPreferredSize() {
		Dimension newSize = getViewport().getView().getPreferredSize();
		int newWidth = newSize.width+getInsets().left+getInsets().right+
		(getVerticalScrollBar().isVisible()?getVerticalScrollBar().getWidth():0);
		int newHeight = newSize.height+getInsets().top+getInsets().bottom+
		(getColumnHeader()!=null&&getColumnHeader().isVisible()?getColumnHeader().getSize().height:0)+
		(getHorizontalScrollBar().isVisible()?getHorizontalScrollBar().getHeight():0);
		return new Dimension(newWidth,newHeight);
	}

	public void validate() {
		getViewport().setBackground(Color.white);
		if (getPreferredSize().width!=getWidth()) {

			if(this.getParent() != null){
				((JPanel)this.getParent()).revalidate();
			}
		}
		super.validate();
	}

	public void initScrollPane(){
		//		if(chromeModel.getOrganiser() != null && chromeModel.getOrganiser().getChromeManager()!=null &&
		//				chromeModel.getOrganiser().getChromeManager().isValid()){
		chromeHeaderPanel = new ChromosomeHeaderPanel(chromeModel, c, this, 
				ConstantData.LEFT_MARGIN, ConstantData.RIGHT_MARGIN, ConstantData.MARKER_DIFF);
		getHorizontalScrollBar().addAdjustmentListener(c);
		getVerticalScrollBar().addAdjustmentListener(c);
		//this.setViewport(new FixedHeaderViewport());
		setColumnHeaderView(chromeHeaderPanel);
		//		}
	}

	public void repositionHeader() {
		this.remove(chromeHeaderPanel);
		this.setColumnHeaderView(chromeHeaderPanel);
	}

	@SuppressWarnings("serial")
	class FixedHeaderViewport extends JViewport {


		public void setViewPosition(Point p) {
			super.setViewPosition(new Point(0, 0));
		}


		public Dimension getExtentSize() {
			return getSize();
		}
	}

	
	// === MOUSE MARKER VERTICAL LINE
	
	protected int marker;
	protected Stroke markerStroke = new BasicStroke(2f,0,0,1f, new float[]{5f,5f},0);
	
	
	public void setMarker(int marker) {
		if (marker!=this.marker) {
			this.marker = marker;
			repaint();
		}
	}

	public void paint(Graphics gg) {
		super.paint(gg);
		if (marker>0) {
			int y1 = chromeModel.getHeight_chromePanel();
			int y2 = viewport.getHeight();

			Graphics2D g =  (Graphics2D)gg;
			g.setColor(Color.RED);
			Stroke oldStroke = g.getStroke();
			g.setStroke(markerStroke);
			g.drawLine(marker,y1,marker,y2+y1);
			g.setStroke(oldStroke);
		}


	}
}