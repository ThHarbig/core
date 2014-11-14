package mayday.vis3.plots.pca;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.Probe;
import mayday.core.math.JamaSubset.Matrix;
import mayday.vis3.SparseZBuffer;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.plots.scatter.AbstractScatterPlotComponent;
import mayday.vis3.vis2base.DataSeries;
import mayday.vis3.vis2base.PointIterator;
import mayday.vis3.vis2base.Shape;

@SuppressWarnings("serial")
public abstract class XYScatterPlot<T> extends AbstractScatterPlotComponent {

	private Matrix PCAData;
	private int d1,d2;
	private List<T> allThings;
	private HashMap<T, Integer> fastMap = new HashMap<T, Integer>();
	
	protected Rectangle selRect;
	protected SparseZBuffer szb;
	
	protected abstract void setup0(PlotContainer plotContainer);
	protected abstract void updateSelection(Set<T> newSelection, boolean control, boolean alt);

	public XYScatterPlot(Matrix pcaData, int dim1, int dim2, List<T> thingsOnDisplay) {
		PCAData = pcaData;
		d1=dim1;
		d2=dim2;
		allThings = thingsOnDisplay;
		int i=0;
		for (T pb : allThings) {
			fastMap.put(pb, i++);
		}

		getZoomController().setActive(false);
		
		addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (selRect!=null) {
					Graphics2D g = ((Graphics2D)farea.getGraphics());
					drawSelectionRectangle(g);
					int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();
					boolean control = ((e.getModifiers()&CONTROLMASK) == CONTROLMASK);
					boolean alt = e.isAltDown();
					selectByRectangle(selRect, control, alt);
					selRect = null;
				}
			}
		});
		
		farea.addMouseMotionListener(new MouseMotionListener() {
			
			protected Point dragPoint;
			protected Point targPoint;
			
			public void mouseDragged(MouseEvent e) {					
				Graphics2D g = ((Graphics2D)farea.getGraphics());
				if (selRect==null) {
					dragPoint = e.getPoint();
				} else {
					drawSelectionRectangle(g);
				}
				targPoint = e.getPoint();
				selRect = new Rectangle(dragPoint, new Dimension(1,1));
				selRect.add(targPoint);					
				drawSelectionRectangle(g);					
			}

			public void mouseMoved(MouseEvent e) {
			}
		});
	}
	
	final protected void drawSelectionRectangle(Graphics2D g) {
		if (selRect==null)
			return;
		g.setXORMode(getBackground());
		g.setColor(Color.RED);
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(2));
		g.draw(selRect);
		g.setStroke(oldStroke);
		g.setPaintMode();
	}
	
	final public DataSeries doSelect1(Collection<T> things) {
		DataSeries ds = new DataSeries();		
		for (T pb : things) {
			int i = fastMap.get(pb);
			double x = PCAData.get(i, d1);
	    	double y = PCAData.get(i, d2);
	    	ds.addPoint(x, y, pb);	  	    	
		}
		ds.setShape(new Shape() {
			@Override
			public void paint(Graphics2D g) {
				g.fillOval(-3,-3,6,6);
			}
			public boolean wantDeviceCoordinates() {
				return true;
			}
		});
		return ds;
	}	
	final public DataSeries doSelect(Collection<Probe> things) {
		return null;
	} // not called ever


	@Override
	final public int getNumberOfComponents() {
		return 1;
	}

	@Override
	public DataSeries getPlotComponent(int j) {
		DataSeries ds = new DataSeries();		
		for (int i=0; i!=PCAData.getRowDimension(); ++i) {
	    	double x = PCAData.get(i, d1);
	    	double y = PCAData.get(i, d2);
	    	ds.addPoint(x, y, allThings.get(i));
	    	szb.setObject(x, y, allThings.get(i));
		}
		return ds;		
	}
	
	final protected void selectByRectangle(Rectangle r, boolean control, boolean alt) {
		Set<T> newSelection = new HashSet<T>();
		double[] clicked1 = getPoint(r.x, r.y);
		double[] clicked2 = getPoint(r.x+r.width, r.y+r.height);
		Rectangle2D sel = new Rectangle2D.Double(clicked1[0], clicked2[1], clicked2[0]-clicked1[0], clicked1[1]-clicked2[1]);
		
		PointIterator<T> it = new PointIterator<T>(Layers);

		while(it.hasNext()) {
			Double[] point = it.next();
			double px = point[0];
			double py = point[1];
			// fast evaluation of easy hits
			if (sel.contains(px, py))
				newSelection.add(it.getObject());
		}
		
		/*
		 * selection modes: 
		 * - no modifier: replace
		 * - ctrl: union with previous selection
		 * - alt: intersect with previous selection
		 * - ctrl+alt: remove from previous selection
		 */
		
		updateSelection(newSelection, control, alt);
	}
	
	
	
	final public void setup(PlotContainer plotContainer) {
		super.setup(plotContainer);
		setName("PC "+(d1+1)+ " vs PC "+(d2+1));
		szb = new SparseZBuffer();
		setup0(plotContainer);
		updatePlot();
	}

	final public String getPreferredTitle() {
		return "PC "+(d1+1)+ " vs PC "+(d2+1);
	}

	@Override
	final public String getAutoTitleY(String ytitle) {
		return "PC "+(d2+1);		
	}

	@Override
	final public String getAutoTitleX(String xtitle) {
		return "PC "+(d1+1);
	}
	
}
