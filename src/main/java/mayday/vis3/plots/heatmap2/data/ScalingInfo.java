package mayday.vis3.plots.heatmap2.data;

import java.util.Arrays;

public class ScalingInfo {

	protected double[] sizes;
	protected double[] starts;
	protected int lastGoodStart;
	protected double scaleFactor = 1.0;
	protected boolean[] unscaled;
	
	protected long modifyCount=0;
	
	public ScalingInfo(int numberOfElements, int defaultSize) {
		setNumberOfElements(numberOfElements, defaultSize);
	}
	
	public void setNumberOfElements(int numberOfElements, double defaultScale) {
		sizes = new double[numberOfElements];
		starts = new double[numberOfElements];
		unscaled = new boolean[numberOfElements];
		scaleFactor = defaultScale;
		if (numberOfElements>0)
			starts[0] = 0;
		setSizesForAll(1);
	}
	
	public void setSizes(double[] sizes) {
		if (sizes.length==this.sizes.length) {
			System.arraycopy(sizes, 0, this.sizes, 0, sizes.length);
			lastGoodStart=0;
			++modifyCount;
		} else {
			throw new RuntimeException("Setting sizes for the wrong number of elements");
		}
	}
	
	public void setSizesForAll(double size) {
		Arrays.fill(sizes, size);
		lastGoodStart=0;
		++modifyCount;
	}
	
	public void setSize(int position, double size, boolean preventScaling) {
		if (sizes[position]!=size || unscaled[position]!=preventScaling) {
			sizes[position] = size;
			lastGoodStart = Math.min(lastGoodStart,position);
			unscaled[position]=preventScaling;
			++modifyCount;
		}	
	}
	
	public double getSize(int position) {
		if (position>=starts.length)
			return 0;
		if (position<0)
			return 0;
		if (unscaled[position])
			return sizes[position];
		else
			return (sizes[position]*scaleFactor);
	}
	
	public double getStart(int position) {
		if (position>=starts.length)
			return 0;
		if (position>lastGoodStart) {
			for (int i=lastGoodStart+1; i<=position; ++i) {
				starts[i] = starts[i-1] + getSize(i-1);
			}
			lastGoodStart = position;
		}
		if (position>=starts.length)
			return 0;
		if (position<0)
			return 0;
		return starts[position];
	}
	
	public double getEnd(int position) {
		return getStart(position)+getSize(position);
	}
	
	public int size() {
		return sizes.length;
	}
	
	public int indexAtPosition(int position) {
		// make sure all starts are computed
		getStart(size()-1);
		// starts are sorted by definition
		int target = Arrays.binarySearch(starts, position);
		if (target<0)
			target = target*-1 -1; 
		target--;
		if (target>size()-1)
			target = size()-1;
		return target;
	}
	
	public void setScale(double scale) {
		scaleFactor = scale;
		lastGoodStart = 0;
		++modifyCount;
	}
	
	public long getModificationCount() {
		return modifyCount;
	}
	
}
