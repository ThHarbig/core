package mayday.vis3.plots.genomeviz.genomeoverview;


public class FromToPosition {

	protected long from = -1;
	protected long to = -1;
	protected boolean valid = false;
	
	public FromToPosition(){
		from = -1;
		to = -1;
		valid = false;
	}
	
	public long getFrom(){
		return from;
		
	}
	
	public long getTo(){
		return to;
	}
	
	
	/**
	 * Sets the parameters of this object, notice that its neccessary to input values
	 * in the right order. 
	 * @param From
	 * @param To
	 */
	public void setPositions(long From, long To) {
		from = From;
		to = To;
		valid = (from>0 && to>0 && from<=to);
	}

	public String getTooltiptext_bp() {
		if(from==to){
			return Long.toString(from) + " (1bp)";
		} else {
			return Long.toString(from)+"-"+Long.toString(to) + " (" + Long.toString(to-from+1)+"bp)";
		}	
	}

	public boolean isValid() {
		return valid;
	}

	public void clear() {
		from = -1;
		to = -1;
		valid = false;
	}
	
	public void clear(long i) {
		from = i;
		to = i;
		valid = false;
	}
	
}
