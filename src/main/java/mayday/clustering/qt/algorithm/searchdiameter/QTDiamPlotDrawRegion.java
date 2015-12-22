package mayday.clustering.qt.algorithm.searchdiameter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;


/**
 * @author Sebastian Nagel
 * @author G&uuml;nter J&auml;ger
 * @version 0.1
 */
@SuppressWarnings("serial")
public class QTDiamPlotDrawRegion extends JComponent {
	private int[] distribution;
	private double[] distances;
	
	private double xScaleFactor = 100;
	private double yScaleFactor = 100;
	
	private Graphics2D g2d;
	private Line2D diameterLine;
	private int xWidth = 500;
	private int yWidth = 350;
	private int xSpace = 100;
	private int ySpace = 20;
	
	private String xLabel = "Distance";
	private String yLabel = "Quantity";
	private double distancesMaxValue = 1;
	private int distributionMaxValue = 1;
	
	/**
	 * @param width
	 * @param height
	 */
	public QTDiamPlotDrawRegion(int width, int height) {	
		this.xWidth = width;
		this.yWidth = height;
		this.diameterLine = new Line2D.Double(this.xSpace, this.ySpace, this.xSpace, this.yWidth);
		this.setSize(this.calcDimension());
		this.setBackground(Color.WHITE);
		this.setVisible(true);
	}

	/**
	 * 
	 * @return
	 */
	private Dimension calcDimension() {
		int x = this.xWidth + this.xLabel.length()*10;
		int y = this.yWidth + this.ySpace + 30 ;
		return new Dimension(x,y);
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		this.g2d = (Graphics2D)g;
		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, getWidth(), getHeight());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
		
		if (this.distribution==null) {
			g2d.setColor(Color.black);
			g2d.drawString("Please select a distance measure and click \"update plot\".", 10, getHeight()/2);
		} else {
			this.drawCoordinateLines();
			this.drawValues();

			g2d.setColor(Color.BLUE);
			g2d.draw(this.diameterLine);

			this.drawAxes();
		}
	}

	/**
	 * 
	 */
	private void drawCoordinateLines() {
		g2d.setColor(Color.lightGray);
		//y-axis
		for(int i = this.ySpace+10; i < this.yWidth; i+=this.yWidth/10) {
			g2d.draw(new Line2D.Double(this.xSpace, i, this.xWidth, i));
		}
		//x-axis
		for(int i = this.xSpace+10; i < this.xWidth; i+=this.xWidth/10) {
			g2d.draw(new Line2D.Double(i, this.yWidth, i, this.ySpace));
		}
	}

	/**
	 * 
	 */
	private void drawAxes() {
		g2d.setColor(Color.BLACK);
		//X-Axis
		g2d.drawLine(this.xSpace, this.yWidth, this.xWidth, this.yWidth);
		g2d.drawLine(this.xWidth, this.yWidth, this.xWidth-5, this.yWidth-2);
		g2d.drawLine(this.xWidth, this.yWidth, this.xWidth-5, this.yWidth+2);
		//Y-Axis
		g2d.drawLine(this.xSpace, this.ySpace, this.xSpace, this.yWidth);
		g2d.drawLine(this.xSpace, this.ySpace, this.xSpace-2, this.ySpace+5);
		g2d.drawLine(this.xSpace, this.ySpace, this.xSpace+2, this.ySpace+5);
		//Zero-Point
		g2d.drawLine(this.xSpace, this.yWidth, this.xSpace-5, this.yWidth+5);
		g2d.drawString("0", this.xSpace-15, this.yWidth+20);
		
		//Labeling: x-axis
		for(int i = this.xSpace+10; i < this.xWidth; i+=this.xWidth/10) {
			g2d.draw(new Line2D.Double(i, this.yWidth, i, this.yWidth+5));
			String s = Double.toString(Math.round(this.getXScaling(i)*100.)/100.);
			g2d.drawString(s, i, this.yWidth+30);
		}
		
		g2d.drawString(this.xLabel, this.xWidth + 10, this.yWidth+3);
			
		//Labeling: y-axis
		for(int i = this.ySpace+10; i < this.yWidth; i+=this.yWidth/10) {
			g2d.draw(new Line2D.Double(this.xSpace, i, this.xSpace-5, i));
			double rounded = Math.round((this.getYScaling(i))*100.)/100.;
			String s  = Double.toString(rounded);
			g2d.drawString(s, (this.xSpace-5)-s.length()*8, i+3);
		}
		
		g2d.drawString(this.yLabel, (this.xSpace+yLabel.length()*8)/2, this.ySpace-5);
	}

	/**
	 * 
	 */
	private void drawValues() {
		if(this.distribution != null) {
			for(int i = 0; i < this.distribution.length-1; i++) {
				g2d.setColor(Color.RED);
				double x1 = (this.distances[i]*this.xScaleFactor)+this.xSpace;
				double y1 = this.yWidth -(this.distribution[i]*this.yScaleFactor);
				double x2 = (this.distances[i+1]*this.xScaleFactor)+this.xSpace;
				double y2 = this.yWidth - (this.distribution[i+1]*this.yScaleFactor);
				g2d.draw(new Line2D.Double(x1,y1,x2,y2));
				g2d.setColor(Color.BLACK);
				g2d.draw(new Rectangle2D.Double(x1,y1,1,1));
			}
		}
	}

	/**
	 * @param pos 
	 * @return xScaleFactor
	 */
	public double getXScaling(double pos) {
		if(isRange(pos)) {
			return ((pos-this.xSpace)/this.xScaleFactor);
		}
		return 0.;
	}
	
	/**
	 * @param pos 
	 * @return xScaleFactor
	 */
	public double getYScaling(double pos) {
		return (((this.yWidth-pos))/this.yScaleFactor);
	}
	
	/**
	 * @param pos
	 * @return
	 */
	private boolean isRange(double pos) {
		return(this.xSpace <= pos && pos <= this.xWidth);
	}

	/**
	 * @param x
	 */
	public void setDiameterLine(double x) {
		if(isRange(x)) {
			this.diameterLine.setLine(x, this.ySpace, x, this.yWidth);
			this.repaint();
		}
	}
	
	/**
	 * @param diameter
	 * @throws Exception
	 */
	public void setDiameter(double diameter) throws Exception {
		double x = diameter*this.xScaleFactor + this.xSpace;
		if(isRange(x)) {
			this.diameterLine.setLine(x, this.ySpace, x, this.yWidth);
			this.repaint();
		} else {
			throw new Exception("Not in Range");
		}
	}
	
	/**
	 * @param distances 
	 * @param distribution 
	 * @param gaussian 
	 * @param distancesMaxValue 
	 * @param distributionMaxValue 
	 * @param gaussianMaxValue 
	 */
	public void updatePlot(double[] distances, int[] distribution, 
			double distancesMaxValue, int distributionMaxValue) {
		this.distances = distances;
		this.distribution = distribution;
		
		this.distancesMaxValue = distancesMaxValue;
		this.distributionMaxValue = distributionMaxValue;
		this.xScaleFactor = (this.xWidth-this.xSpace)/distancesMaxValue;
		
		double yValue = (this.yWidth-this.ySpace);
		this.yScaleFactor = yValue/distributionMaxValue;
		
		repaint();
	}
	
	/**
	 * @param measureType
	 * @return diameter
	 */
	public double calculateBestDiameter() {
		double diameter = QTStatistics.getBestDiameter(this.distances);
		this.setDiameterLine((diameter*this.xScaleFactor)+this.xSpace);
		return diameter;
	}
	
	/**
	 * (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	public void plotResized(ComponentEvent e) {
		if (distributionMaxValue != 1) {
			Object o = e.getSource();
			if (o instanceof Component) {
				Component comp = (Component) o;
				this.setWidth(comp.getWidth()-100);
				this.setHeight(comp.getHeight()-100);
				this.xScaleFactor = (this.xWidth-this.xSpace)/distancesMaxValue;
				this.yScaleFactor = (this.yWidth-this.ySpace)/distributionMaxValue;
				this.repaint();
			}
		}
    }

	/**
	 * @param width
	 */
	public void setWidth(int width) {
		this.xWidth = width;
	}

	/**
	 * @param height
	 */
	public void setHeight(int height) {
		this.yWidth = height;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#getMinimumSize()
	 */
	public Dimension getMinimumSize() {
		return getSize();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		return getSize();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#getMaximumSize()
	 */
	public Dimension getMaximumSize() {
		return getSize();
	}
}

