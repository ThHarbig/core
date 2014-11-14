package mayday.clustering.kmeans.searchK;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import mayday.core.EventFirer;
import mayday.core.structures.linalg.vector.DoubleVector;

public class SearchKDrawPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1333347850812771198L;
	
	private SearchKSetting setting;
	private DoubleVector data;
	
	private int xSpace = 100;
	private int ySpace = 20;
	
	private double xScaleFactor = 1;
	private double yScaleFactor = 1;
	
	private int csWidth = 1;
	private int csHeight = 1;
	
	private int chosenK = 1;
	private boolean plotWaiting = true;
	
	private EventFirer<SearchKEvent, SearchKChangeListener> eventFirer = new EventFirer<SearchKEvent, SearchKChangeListener>() {
		@Override
		protected void dispatchEvent(SearchKEvent event,
				SearchKChangeListener listener) {
			listener.stateChanged(event);
		}
	};
	
	public SearchKDrawPanel() {
		this.setPreferredSize(new Dimension(640, 480));
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		
		this.setBackground(Color.WHITE);
		this.setVisible(true);
	}
	
	private Dimension calcDimension() {
		int x = this.getWidth() - this.xLabel.length() * 10;
		int y = this.getHeight() - this.ySpace - 30 ;
		
		this.csWidth = x;
		this.csHeight = y;
		
		this.xScaleFactor = (this.csWidth - this.xSpace) / this.distancesMaxValue;
		this.yScaleFactor = (this.csHeight - this.ySpace) / this.distributionMaxValue;
		
		return new Dimension(x,y);
	}
	
	private String xLabel = "Paramter k";
	private String yLabel = "Score";
	private double distancesMaxValue = 1;
	private double distributionMaxValue = 1;
	
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, getWidth(), getHeight());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
		
		this.calcDimension();
		
		super.paint(g2d);
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		
		if (this.data == null || this.plotWaiting) {
			g2d.setColor(Color.black);
			g2d.drawString("Please wait for data calculations.", 10, this.csHeight / 2);
		} else {
			this.drawCoordinateLines(g2d);
			this.drawData(g2d);
			this.drawAxes(g2d);
			
			g2d.setColor(Color.BLUE);
			double dx = (this.chosenK-1) * xScaleFactor + xSpace;
			g2d.draw(new Line2D.Double(dx, ySpace, dx, csHeight));
		}
	}
	
	private void drawCoordinateLines(Graphics2D g2d) {
		g2d.setColor(Color.lightGray);
		//y-axis
		for(int i = this.ySpace+10; i < this.csHeight; i+=this.csHeight / 10) {
			
			g2d.draw(new Line2D.Double(this.xSpace, i, this.csWidth, i));
		}
		//x-axis
		for(int i = 0; i < this.distancesMaxValue; i ++) {
			double x = i * this.xScaleFactor + this.xSpace;
			g2d.draw(new Line2D.Double(x, this.csHeight, x, this.ySpace));
		}
	}
	
	private void drawData(Graphics2D g2d) {
		if(this.data != null) {
			for(int i = 0; i < this.data.size()-1; i++) {
				g2d.setColor(Color.RED);
				double x1 = (i * this.xScaleFactor) + this.xSpace;
				double y1 = this.csHeight -(this.data.get(i) * this.yScaleFactor);
				double x2 = ((i+1) * this.xScaleFactor) + this.xSpace;
				double y2 = this.csHeight - (this.data.get(i+1) * this.yScaleFactor);
				g2d.draw(new Line2D.Double(x1,y1,x2,y2));
				g2d.setColor(Color.BLACK);
				g2d.fill(new Rectangle2D.Double(x1-2,y1-2,4,4));
				g2d.fill(new Rectangle2D.Double(x2-2,y2-2,4,4));
			}
		}
	}
	
	private void drawAxes(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		//X-Axis
		g2d.drawLine(this.xSpace, this.csHeight, this.csWidth, this.csHeight);
		g2d.drawLine(this.csWidth, this.csHeight, this.csWidth-5, this.csHeight-2);
		g2d.drawLine(this.csWidth, this.csHeight, this.csWidth-5, this.csHeight+2);
		//Y-Axis
		g2d.drawLine(this.xSpace, this.ySpace, this.xSpace, this.csHeight);
		g2d.drawLine(this.xSpace, this.ySpace, this.xSpace-2, this.ySpace+5);
		g2d.drawLine(this.xSpace, this.ySpace, this.xSpace+2, this.ySpace+5);
		//Zero-Point
		g2d.drawLine(this.xSpace, this.csHeight, this.xSpace-5, this.csHeight+5);
		g2d.drawString("0", this.xSpace-15, this.csHeight+20);
		
		//Labeling: x-axis
		int count = 1;
		for(int i = 0; i < this.distancesMaxValue; i ++) {
			double x = i * xScaleFactor + this.xSpace;
			g2d.draw(new Line2D.Double(x, this.csHeight, x, this.csHeight + 5));
			String s = "" + (count++); 
			g2d.drawString(s, (int)Math.rint(x), this.csHeight + 30);
		}
		
		g2d.drawString(this.xLabel, this.csWidth, this.csHeight + 3);
			
		//Labeling: y-axis
		for(int i = this.ySpace+10; i < this.csHeight; i+=this.csHeight/10) {
			g2d.draw(new Line2D.Double(this.xSpace, i, this.xSpace-5, i));
			double rounded = Math.round((this.getYScaling(i))*100.)/100.;
			String s  = Double.toString(rounded);
			g2d.drawString(s, (this.xSpace-5)-s.length()*8, i+3);
		}
		
		g2d.drawString(this.yLabel, (this.xSpace+yLabel.length()*8)/2, this.ySpace-5);
	}
	
	/**
	 * @param pos 
	 * @return yScaleFactor
	 */
	public double getYScaling(double pos) {
		return (((this.csHeight - pos))/this.yScaleFactor);
	}
	
	/**
	 * @param pos 
	 * @return xScaleFactor
	 */
	public double getXScaling(double pos) {
		return (((this.csWidth - pos))/this.xScaleFactor);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.drawDiameterLine(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.drawDiameterLine(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {}

	public void triggerPlotWaiting() {
		this.plotWaiting = this.plotWaiting ? false : true;
		this.repaint();
	}

	public void setData(DoubleVector data) {
		this.data = data;
	}

	public void setSetting(SearchKSetting setting) {
		this.setting = setting;
	}
	
	public void addChangeListener(SearchKChangeListener listener) {
		this.eventFirer.addListener(listener);
	}
	
	public void removeChangeListener(SearchKChangeListener listener) {
		eventFirer.removeListener(listener);
	}
	
	public void fireChanged() {
		eventFirer.fireEvent(new SearchKEvent(getChosenClusterSize()));
	}

	public int getChosenClusterSize() {
		return this.chosenK;
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
	
	/**
	 * @param distances 
	 * @param distribution 
	 * @param gaussian 
	 * @param distancesMaxValue 
	 * @param distributionMaxValue 
	 * @param gaussianMaxValue 
	 */
	public void updatePlot(DoubleVector data) {
		this.data = data;
		this.distancesMaxValue = this.data.size();
		this.distributionMaxValue = this.data.max();
		repaint();
	}
	
	public void drawDiameterLine(MouseEvent e) {
		double x = e.getPoint().x;
		double newK = this.distancesMaxValue - this.getXScaling(x) + 1;
		int k = (int)Math.rint(newK);
		
		if(k >= 1 && k <= this.distancesMaxValue) {
			this.chosenK = k;
			this.setting.setMaxCluster(this.chosenK);
			this.fireChanged();
		}
		
		repaint();
	}
}
