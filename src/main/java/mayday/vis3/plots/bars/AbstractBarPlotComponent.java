package mayday.vis3.plots.bars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.vis2base.ChartComponent;
import mayday.vis3.vis2base.DataSeries;
import mayday.vis3.vis2base.Shape;

/* No mouse/key listeners here, because we don't support selection in this plot
 */

@SuppressWarnings("serial")
public abstract class AbstractBarPlotComponent extends ChartComponent implements ViewModelListener, ProbeListListener {
	
	protected DataSeries selectionLayer;
	protected DataSeries[] Layers;
	protected boolean selectionBehindPlot = false;
	protected int lastSelected=-1;
	
	public class BarShape extends Shape
	{
		private double width, height;
		private Color color;
		
		public BarShape(double width, double height , Color color ) {
			this.width = width;
			this.height = height;
			this.color = color;
		}
		
		public void paint(Graphics2D g) {
			g.setColor(color);
			Rectangle2D.Double r;
			if (height<0)
				r = new Rectangle2D.Double(-width/2, height, width, -height);
			else
				r = new Rectangle2D.Double(-width/2, 0, width, height);
			g.fill(r);
		}
		
		public void setColor(Color c) {
			color = c;
		}
	}
	
	public AbstractBarPlotComponent() {
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				double[] clicked = getPoint(evt.getX(), evt.getY());
				int idx = (int)(clicked[0]-.5);
				processMouseClick(idx, evt);
			}
		});
	}
	
	
	public abstract int getNumberOfBars();
	
	public abstract BarShape getBar(int i);
	
	public abstract DataSeries doSelect(Collection<Probe> probes);
	
	
	public void createView() {
		
		// Set distances for scaling (axis)
		setScalingUnitX(1.0);
		
		Layers = new DataSeries[getNumberOfBars()+1];
		int h=0;
		
		double barMax = 0;
		double barMin = 0;
		
		if (selectionBehindPlot)
			select(Color.RED);
		
		for(int i = 0; i!=getNumberOfBars(); ++i) {
			DataSeries ds = new DataSeries();
			BarShape bar = getBar(i);
			ds.addPoint(i+1, 0, null);
			barMax = Math.max(barMax, bar.height);
			barMin = Math.min(barMin, bar.height);
			ds.setShape(bar);
			Layers[h++] = ds;
			addDataSeries(ds);
		}		

		barMax*=1.05;
		
		DataSeries pretty = new DataSeries();
		pretty.addPoint(0,barMin,null);
		pretty.addPoint(getNumberOfBars()+.5,barMax,null);
		pretty.setShape(new Shape() {
			public void paint(Graphics2D g) {			
			}
		});
		addDataSeries(pretty);
		Layers[Layers.length-1] = pretty;
		
		if (!selectionBehindPlot)
			select(Color.RED);
	}
	
	
	protected void processMouseClick(int barIndex, MouseEvent evt) {
//		System.out.println("Mouse "+evt+"\n"+evt.isShiftDown()+"\t"+lastSelected);
		// selection handling etc.
		if (evt.getClickCount()==1 && evt.getButton()==1) {
			Boolean operation = true;
			int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();
			if ((evt.getModifiers()&CONTROLMASK) != CONTROLMASK) {
				unselectAllBars();
			} else {
				operation = null;
			}
			// if shift is used, select range from last selected
			if (evt.isShiftDown() && lastSelected>-1) {
				for (int i=Math.min(lastSelected, barIndex); i<=Math.max(lastSelected, barIndex); ++i)
					selectBar(i, operation);
			} else {
				selectBar(barIndex, operation);
			}
			lastSelected = barIndex;
		}
	}
	
	protected void unselectAllBars() {
		for (int i=0; i!=getNumberOfBars(); ++i)
			selectBar(i, false);
	}
	
	/**
	 * 
	 * @param barIndex the bar affected
	 * @param select true/false: select/unselect, null:toggle
	 */
	protected void selectBar(int barIndex, Boolean select) {
		// ignored by default
	}
		
	public void setup(PlotContainer plotContainer) {
		super.setup(plotContainer);
		viewModel.addViewModelListener(this);
		plotContainer.setPreferredTitle(getPreferredTitle(), this);
		updatePlot();
	}

	public void viewModelChanged(ViewModelEvent vme) {
		switch (vme.getChange()) {
		case ViewModelEvent.PROBE_SELECTION_CHANGED:
			select(Color.RED);
			break;
		case ViewModelEvent.PROBELIST_ORDERING_CHANGED: // fallthrough
		case ViewModelEvent.DATA_MANIPULATION_CHANGED:
		case ViewModelEvent.PROBELIST_SELECTION_CHANGED:
			updatePlot();
			break;			
		}
	}

	public void probeListChanged(ProbeListEvent event) {
		switch (event.getChange()) {
		case ProbeListEvent.LAYOUT_CHANGE:
			repaint();
			break;
		case ProbeListEvent.CONTENT_CHANGE:
			updatePlot();
			break;
		}
	}
	
	public abstract String getPreferredTitle();
	
	public void removeNotify() {
		super.removeNotify();
		if (viewModel!=null)
			viewModel.removeViewModelListener(this);	
	}
	
	protected class IterableExperiment implements Iterable<Double>, Iterator<Double> {

		int experiment;
		Iterator<Probe> pb;
		
		public IterableExperiment(ProbeList pl, int experiment) {
			this.experiment=experiment;
			pb = pl.getAllProbes().iterator();
		}
		
		public Iterator<Double> iterator() {
			return this;
		}

		public boolean hasNext() {
			return pb.hasNext();
		}

		public Double next() {
			return pb.next().getValue(experiment);
		}

		public void remove() {
		}
		
	}
	
	
	public void select(Color selection_color)
	{
		if (selectionLayer!=null)
			removeDataSeries(selectionLayer);
		
		Set<Probe> s = new HashSet<Probe>();
				
		for(ProbeList probe_list : viewModel.getProbeLists(true))
			s.addAll(probe_list.getAllProbes());
		
		s.retainAll(viewModel.getSelectedProbes());
		
		selectionLayer = doSelect(s);
		
		selectionLayer.setColor(Color.RED);
		if (selectionBehindPlot) {
			clear();
			addDataSeries(selectionLayer);
			for (DataSeries ds : Layers)
				if (ds!=null)
					addDataSeries(ds);
		} else {
			addDataSeries(selectionLayer);
		}
		clearBuffer();
		repaint();
	}
		
	
}

