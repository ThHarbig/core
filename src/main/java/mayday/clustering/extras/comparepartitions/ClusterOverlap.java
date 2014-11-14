/**
 * 
 */
package mayday.clustering.extras.comparepartitions;

@SuppressWarnings("unchecked")
class ClusterOverlap implements Comparable {
	
	public int  leftIndex, 
				rightIndex,
				leftCount, 
			    rightCount;
	public String leftName, 
				  rightName;
	public int overlap;
	public boolean used = false;
	public int direction;
	
	public static final int DIR_BOTH = 0;
	public static final int DIR_LTR = 1;
	public static final int DIR_RTL = 2;
	
	public ClusterOverlap(int li, int ri, int lc, int rc, int overl, String lN, String rN) {
		leftIndex=li; 
		rightIndex=ri; 
		leftCount=lc; 
		rightCount=rc; 
		overlap=overl; 
		leftName = lN; 
		rightName=rN;
	}
	
	public int compareTo(Object o) {
		ClusterOverlap partner = (ClusterOverlap)o;
				
		// two-way go to the top, then left-to-right, then right-to-left
		int c1 = new Integer(this.direction).compareTo(partner.direction); 		

		if (c1!=0)
			return c1;
		
		// either both are two-way or both are one-way
		
		double this_perc = (double)overlap/Math.min((double)leftCount, (double)rightCount);
		double part_perc = (double)partner.overlap/Math.min((double)partner.leftCount, (double)partner.rightCount);
		
		return -(new Double(this_perc).compareTo(part_perc)); 
	}
	
	public double leftPercentage() {
		return 100.0*(double)overlap/(double)leftCount;
	}
	
	public double rightPercentage() {
		return 100.0*(double)overlap/(double)rightCount;
	}

}