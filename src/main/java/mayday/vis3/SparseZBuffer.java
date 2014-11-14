package mayday.vis3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class SparseZBuffer {

	protected static final int MAXDELTA = 500;
	
	protected class PointWithObject {
		Double x, y;
		Object o;
		public Double distanceTo(PointWithObject other) {
			return (other.x-x)*(other.x-x)+(other.y-y)*(other.y-y);
		}
	}
	
	protected boolean needsSorting = false;
	
	protected class PXComparator implements Comparator<PointWithObject> {
		public int compare(PointWithObject o1, PointWithObject o2) {
			return o1.x.compareTo(o2.x);
		}		
	}
	
	protected class PYComparator implements Comparator<PointWithObject> {
		public int compare(PointWithObject o1, PointWithObject o2) {
			return o1.y.compareTo(o2.y);
		}		
	}
	
	protected class DistanceComparator implements Comparator<PointWithObject> {
		
		protected PointWithObject partner;
		
		public DistanceComparator(PointWithObject p) {
			partner=p;
		}
		
		public int compare(PointWithObject o1, PointWithObject o2) {
			return o1.distanceTo(partner).compareTo(o2.distanceTo(partner)); 
		}		
	}
	
	protected ArrayList<PointWithObject> byX = new ArrayList<PointWithObject>();
	protected ArrayList<PointWithObject> byY = new ArrayList<PointWithObject>();
	
	public SparseZBuffer() {
	}
	
	public void clear() {
		byX.clear();
		byY.clear();
	}
	
	public void setObject(double x, double y, Object obj) {
		PointWithObject pwo = new PointWithObject();
		pwo.x = x;
		pwo.y = y;
		pwo.o = obj;
		byX.add(pwo);
		byY.add(pwo);
		needsSorting = true;
	}

	protected void extractRegion(ArrayList<PointWithObject> source, int start, int maxDelta, Collection<PointWithObject> target) {
		if (start>=0 && start<source.size())
			target.add(source.get(start));
		
		for(int delta=1; delta!=maxDelta; ++delta) {
			int lX = start-delta;
			int rX = start+delta;
			boolean shouldBreak = true;
			if (lX>=0 && lX<source.size()) { 
				target.add(source.get(lX));
				shouldBreak = false;
			}
			if (rX>=0 && rX<source.size()) {
				target.add(source.get(rX));
				shouldBreak = false;
			}
			if (shouldBreak) 
				break;
		}
	}	
	
	public Object getObject(double x, double y) {
		if (needsSorting) {
			Collections.sort(byX, new PXComparator());
			Collections.sort(byY, new PYComparator());
		}
		PointWithObject pwo = new PointWithObject();
		pwo.x=x;
		pwo.y=y;
		int idxX = Collections.binarySearch(byX, pwo, new PXComparator());
		// find x objects: go left and right until at maximum 1000 objects are found 
		LinkedList<PointWithObject> xList = new LinkedList<PointWithObject>();
		if (idxX<0)
			extractRegion(byX, -idxX, MAXDELTA, xList);
		else
			xList.add(pwo);
		
		int idxY = Collections.binarySearch(byY, pwo, new PYComparator());
		LinkedList<PointWithObject> yList = new LinkedList<PointWithObject>();
		if (idxY<0)
			extractRegion(byY, -idxY, MAXDELTA, yList);
		else
			yList.add(pwo);
		
		// intersect X and Y lists
		xList.retainAll(yList);
		
		// sort by Distance to clickpoint
		Collections.sort(xList, new DistanceComparator(pwo));
		
		if (xList.size()>0)		
			return xList.get(0).o;
		return null;
	}
	
}
