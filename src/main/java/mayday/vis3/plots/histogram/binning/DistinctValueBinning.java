package mayday.vis3.plots.histogram.binning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DistinctValueBinning<T> implements BinnedData<T> {

	protected List<T>[] bins;
	protected Double[] binpositions;
	
	@SuppressWarnings("unchecked")
	public DistinctValueBinning(Collection<Double> binPositions) {
		binpositions = binPositions.toArray(new Double[0]);
		Arrays.sort(binpositions);
		bins = (List<T>[])new List[binpositions.length];
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
		double pos = 0;
		for (double b : binpositions) {
			if (d==b)
				return pos+.5;
			else 
				++pos;
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	public void addData(Map<T, Double> data) {
		
		if (bins.length==0)
			return;
		
		if (bins[0]!=null )
			throw new RuntimeException("Histogram already contains data");
		
		ObjectDoubleVector<T> odv = new ObjectDoubleVector<T>((Map<T,Number>)(Map)data);
		odv.sort();
		
		int bin=-1;
		double last=Double.NEGATIVE_INFINITY;
		
		for (int j=0; j!=odv.size(); ++j) {
			double val = odv.get(j);
			if (last!=val && !( Double.isNaN(last) && Double.isNaN(val)) ) {
				last = val;
				++bin;
				bins[bin] = new ArrayList<T>();
			}
			bins[bin].add(odv.getObject(j));
		}

	}

	public Collection<T> getObjectsInBin(int bin) {
		return bins[bin];
	}

}
