package mayday.genetics.coordinatemodel;

import mayday.genetics.basic.Strand;

public class GBAtom implements Comparable<GBAtom> {
	
	public GBAtom(long f, long t, Strand s){
		if (f<t) {
			from=f; 
			to=t;
		} else {
			from=t;
			to=f;
		}
		strand=s;
	}
	
	public long from;
	public long to;
	public Strand strand;
	
	public String toString() {
		return strand.toChar()+":"+from+"-"+to;
	}
	
    public long getUpstreamCoordinate() {
    	return strand==Strand.MINUS ? to : from;
    }
    
    public long getDownstreamCoordinate() {
    	return strand==Strand.MINUS ? from : to;
    }

    public void setUpstreamCoordinate(long upstream) {
    	if (strand==Strand.MINUS)
    		to  = upstream;
    	else
    		from = upstream;
    }
    
    public void setDownstreamCoordinate(long downstream) {
    	if (strand==Strand.MINUS)
    		from  = downstream;
    	else
    		to = downstream;
    }
    
	public int compareTo(GBAtom other) {
		// SORT (1) by start positions on same chromosome
        int result = (from - other.from > 0 ? 1 : -1); 
        if (from!=other.from) 
        	return result;
        
        // SORT (2) by end position if same start
        result = (to - other.to > 0 ? 1 : -1); 
        if (to!=other.to) 
        	return result;
        
        // SORT (3) by strand if same start
        result = (strand == other.strand ? 0 : (strand.ordinal()<other.strand.ordinal() ? -1 : 1) );
       	return result;
	}
	
	public int hashCode() {
		return (int)(from*31*31+to*31+strand.ordinal());
	}
	
	public boolean equals(Object o) {
		return compareTo((GBAtom)o)==0;
	}
}
