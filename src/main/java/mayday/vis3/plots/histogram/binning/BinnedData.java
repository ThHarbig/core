package mayday.vis3.plots.histogram.binning;

import java.util.Collection;
import java.util.Map;

public interface BinnedData<T> {
	public void addData(Map<T,Double> data);
	public int getNumberOfBins();
	public int getBinCount(int bin);
	public double getBinPosition(int bin);
	public double mapValueForPlotting(double d);
	public Collection<T> getObjectsInBin(int bin);
}

