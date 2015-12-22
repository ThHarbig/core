package mayday.vis3.plots.histogram;

import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import mayday.core.Probe;
import mayday.vis3.ValueProvider;
import mayday.vis3.plots.histogram.binning.BinnedData;
import mayday.vis3.plots.histogram.binning.DistinctValueBinning;
import mayday.vis3.plots.histogram.binning.EqualWidthBinning;

public class Histogram {

	protected ValueProvider v;
	protected BinnedData<Probe> binning;
	protected int valCount;
	protected int binCount;
	protected boolean allowDistinctValues;

	public Histogram(ValueProvider V, int binCount, boolean allowDistinctValueBinning) {	
		v=V;
		v.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				update();
			}
		});
		allowDistinctValues = allowDistinctValueBinning;
		setBinCount(binCount);
	}

	public Histogram(ValueProvider V, int binCount) {
		this(V, binCount, true);
	}

	public Histogram(ValueProvider V) {
		this(V, 25);
	}

	public void setBinCount(int binCount) {
		this.binCount=binCount;
		update();
	}
	
	public void update() {
		Map<Probe, Double> values = v.getValuesMap();
		
		valCount = values.size();

		boolean useDistinctBinning = allowDistinctValues;
		TreeSet<Double> distinctValues = null;
		
		if (useDistinctBinning) {
			// Create new binning object if necessary
			distinctValues = new TreeSet<Double>(v.getValues());
			for (Double d : values.values()) {
				if (!Double.isNaN(d)) {
					distinctValues.add(d);
					if (distinctValues.size()>binCount) {
						useDistinctBinning = false;
						break;
					}
				}
			}
		}
			
		if (useDistinctBinning && distinctValues!=null) {
			binning = new DistinctValueBinning<Probe>(distinctValues);			
		} else {
			binning = new EqualWidthBinning<Probe>(binCount, v.getMinimum(), v.getMaximum());
		}
		
		binning.addData(values);
		
		fireChanged();
	}

	public double getBinFrequency(int bin) {
		return ((double)binning.getBinCount(bin))/valCount;
	}
	
	public int getBinCount(int bin) {
		return binning.getBinCount(bin);
	}

	public double getBinPosition(int bin) {
		return binning.getBinPosition(bin);
	}
	
	public int getNumberOfBins() {
		return binning.getNumberOfBins();
	}
	
	private EventListenerList eventListenerList = new EventListenerList();

	public void addChangeListener(ChangeListener cl) {
		eventListenerList.add(ChangeListener.class, cl);		
	}

	public void removeChangeListener(ChangeListener cl) {
		eventListenerList.remove(ChangeListener.class, cl);
	}

	protected void fireChanged() {
		Object[] l_listeners = this.eventListenerList.getListenerList();

		if (l_listeners.length==0)
			return;

		ChangeEvent event = new ChangeEvent(this);

		// process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = l_listeners.length-2; i >= 0; i-=2 )  {
			if ( l_listeners[i] == ChangeListener.class )  {
				ChangeListener list = ((ChangeListener)l_listeners[i+1]);
				list.stateChanged(event);
			}
		}
	}

	public double mapValueForPlotting(double d) {
		return binning.mapValueForPlotting(d);
	}
	
	public Collection<Probe> getObjectsInBin(int bin) {
		return binning.getObjectsInBin(bin);
	}
	
}
