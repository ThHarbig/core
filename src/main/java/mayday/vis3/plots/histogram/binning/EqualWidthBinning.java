package mayday.vis3.plots.histogram.binning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public class EqualWidthBinning<T> implements BinnedData<T> {

	protected ArrayList<T>[] bins;
	protected double[] binpositions;
	protected double max, min;

	@SuppressWarnings("unchecked")
	public EqualWidthBinning(int binCount, double Min, double Max) {
		bins = new ArrayList[binCount];
		binpositions = new double[binCount];
		max=Max;
		min=Min;
	}

	public void addData(Map<T,Double> data) {
		
		if (bins.length==0 | bins[0]!=null )
			throw new RuntimeException("Histogram has zero bins or data already added");
		
		for (int i=0; i!=bins.length; ++i)
			bins[i] = new ArrayList<T>();

		if (!Double.isNaN(min) && !Double.isNaN(max)) {
			double range = max-min;
			double binWidth = range/binpositions.length;
			double mult = binpositions.length/range;
//			double count=0;
			for (Entry<T, Double> ed : data.entrySet()) {
				Double d = ed.getValue();
				int targetBin = Math.min(binpositions.length-1,(int)Math.floor((d-min)*mult));
				bins[targetBin].add(ed.getKey());
//				++count;
			}

			for (int i=0; i!=binpositions.length; ++i) {
				double binCenter = (((double)i/mult)+min+(binWidth/2));
				binpositions[i] = binCenter;
			}
		}
	}

	public int getBinCount(int bin) {
		return bins[bin].size();
	}

	public double getBinPosition(int bin) {
		return binpositions[bin];
	}

	public int getNumberOfBins() {
		return binpositions.length;
	}

	public double mapValueForPlotting(double d) {
		double range = max-min;
		double relpos = (d-min)/range;
		double plotpos = (binpositions.length) * relpos;
		return plotpos;
	}

	public Collection<T> getObjectsInBin(int bin) {
		return bins[bin];
	}
}
