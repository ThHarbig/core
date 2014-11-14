package mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures;

public class SelectedRange {
 
	long fromPosition = 0;
	long toPosition = 0;
	
	public SelectedRange(long from, long to){
		this.fromPosition = from;
		this.toPosition = to;
	}
	
	public long getFromPosition(){
		return fromPosition;
	}
	
	public long getToPosition(){
		return toPosition;
	}
}
