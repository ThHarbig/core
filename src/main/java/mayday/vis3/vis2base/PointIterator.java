package mayday.vis3.vis2base;

import java.util.Iterator;

public class PointIterator<T> implements Iterator<Double[]>, Iterable<Double[]>
{
	DataSeries[] series;
	int series_index;
	int index;
	Double[] next;
	Object nextpb, pb;
	double step=1,nextstep=1;
	
	public PointIterator(DataSeries[] series)
	{
		this.series = series;
		index = -1;
		series_index = 0;
		next();
	}

	public boolean hasNext() {
		return next!=null;
	}
	
	public double getStepSize() {
		return step; 
	}

	public Double[] next() {
		Double[] ret = next;
		
		next=null;
		
		while (next==null && series_index<series.length) {
			++index;				
			if (index==series[series_index].getSize()) {
				++series_index;
				index=-1;
			}
			if (index!=-1 && series_index<series.length && index<series[series_index].getSize()) {
				next = new Double[] {series[series_index].getDPoint(index).x, 
						series[series_index].getDPoint(index).y, 
						series[series_index].getDPoint(index).s};
				pb = nextpb;
				nextpb = series[series_index].getDPoint(index).o;
				step = nextstep;
				if (index==0)
					nextstep=1.0;
				else
					nextstep=Math.abs(series[series_index].getDPoint(index-1).x - series[series_index].getDPoint(index).x);
			}

		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public T getObject()	{
		return (T)pb;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public Iterator<Double[]> iterator() {
		return this;
	}
}