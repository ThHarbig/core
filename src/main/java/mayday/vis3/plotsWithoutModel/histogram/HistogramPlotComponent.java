package mayday.vis3.plotsWithoutModel.histogram;

import java.awt.Color;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.vis3.CollectionValueProvider;
import mayday.vis3.ValueProvider;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.plots.histogram.Histogram;

@SuppressWarnings("serial")
public class HistogramPlotComponent extends mayday.vis3.plots.histogram.HistogramPlotComponent {
	
	public HistogramPlotComponent(int binCount) {
		V = initValueProvider();
		hist = new Histogram(V, binCount);

		ChangeListener cl = new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				setXLabeling();
				updatePlot();
			}
		};	
		
		setYLabeling(null);
		
		hist.addChangeListener(cl);
		
		updatePlot();
	}
	
	public HistogramPlotComponent() {
		this(15);
	}
	
	protected ValueProvider initValueProvider() {
		return new CollectionValueProvider();
	}
	
	public CollectionValueProvider getValueProvider() {
		return (CollectionValueProvider)V;
	}
	
	public void select(Color selection_color) {} // override empty
	
	public void setup(PlotContainer parent) {} //override empty
	
}
