package mayday.genetics.basic.coordinate;

import java.util.List;

import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.coordinatemodel.GBAtom;
import mayday.genetics.coordinatemodel.GBNode;


/**
 * A complex genetic coordinate is composed of one or more loci.
 * The loci are ordered, e.g. by order of translation in an exon model.
 * Loci can be on different strands, but they are all on the same species/chromosome 
 */


@SuppressWarnings("unchecked")
public abstract class AbstractGeneticCoordinate implements Comparable {

	/** for compatibility with primitive genetic coordinates, length=to-from+1. 
	 * If you need the number of bases, use getCoveredBases();
	 */
	public abstract long getCoveredBases();
	public abstract List<GBAtom> getCoordinateAtoms();
	public abstract GBNode getModel();

	public boolean isPrimitive() {
		return (getModel().isPrimitive());
	}
	
	public String toString() {
		if (isPrimitive()) {
			return serialize_primitive();
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append(getChromosome().getSpecies().getName()); 
			sb.append(">");
			sb.append(getChromosome().getId());
			sb.append(":");
			List<GBAtom> atoms = getCoordinateAtoms();
			for (GBAtom gba : atoms) {
				sb.append("[");
				sb.append(gba.toString());
				sb.append("]");
			}
			return sb.toString();
		}
	}

	public int compareTo(Object o)
	{    	
		if(!(o instanceof AbstractGeneticCoordinate)) 
            throw new ClassCastException("Cannot compare to an instance of "+o.getClass().getName());
        
        AbstractGeneticCoordinate cmp = (AbstractGeneticCoordinate)o;
        
        // SORT (1) by chromosome
        int result = this.getChromosome().compareTo(cmp.getChromosome());
        if (result!=0) 
        	return result;
        
        // SORT (2) by start positions on same chromosome
        long fromThis = this.getFrom(); 
        long fromCmp = cmp.getFrom();   
        result = (fromThis - fromCmp > 0 ? 1 : -1); 
        if (fromThis!=fromCmp) 
        	return result;
        
        // SORT (3) by end position if same start
        long toThis = this.getTo(); 
        long toCmp = cmp.getTo();         
        result = (toThis - toCmp > 0 ? 1 : -1); 
        if (toThis!=toCmp) 
        	return result;
        
        // SORT (4) by strand if same start
        Strand sThis = this.getStrand();
        Strand sCmp = cmp.getStrand();
        result = (sThis == sCmp ? 0 : (sThis.ordinal()<sCmp.ordinal() ? -1 : 1) );
        		
        // SORT (5) by coordinate atoms
		if (result==0) {
			List<GBAtom> myList = getCoordinateAtoms();
			List<GBAtom> oList = ((AbstractGeneticCoordinate)o).getCoordinateAtoms();
			// longer coordinates come _later_
			result = myList.size()-oList.size();
			if (result==0) {
				for (int i=0; i!=myList.size(); ++i) {
					GBAtom mA = myList.get(i);
					GBAtom oA = oList.get(i);
					result = mA.compareTo(oA);
					if (result!=0)
						break;
				}
			}
		}

		return result;
	}

	public long getOverlappingBaseCount(AbstractGeneticCoordinate agc) {

		if (!agc.getChromosome().equals(this.getChromosome()))
			return 0;

		// compute overlap for each atom and add them up - if atoms overlap, the result is incorrect.
		long overlap=0;

		List<GBAtom> myAtoms = getCoordinateAtoms();
		List<GBAtom> yourAtoms = agc.getCoordinateAtoms();
		
		for (GBAtom myA : myAtoms) 
			for (GBAtom yourA : yourAtoms) 
				overlap+=getOverlappingBaseCount(myA, yourA);

		return overlap;
	}
	
	public static long getOverlappingBaseCount(GBAtom a1, GBAtom a2) {
		if (!a1.strand.similar(a2.strand))
			return 0;
		
		long startOverlap = Math.max(a1.from, a2.from);
		long endOverlap = Math.min(a1.to, a2.to);
		return Math.max(0, endOverlap-startOverlap+1);		
	}

	/**
	 * compute overlap of this coordinate and one GBAtom
	 */
	public long getOverlappingBaseCount(GBAtom atom) {
		long overlap=0;
		for (GBAtom gba : getCoordinateAtoms())
			overlap += getOverlappingBaseCount(gba, atom);		
		return overlap;
	}

	/** compute overlap based on start/stop coordinates. 
	 * NOTE that this function does not take the strand information into account!
	 */
	public long getOverlappingBaseCount(long from, long to) {
		return getOverlappingBaseCount(new GBAtom(from, to, Strand.UNSPECIFIED));
	}

	public String serialize() {
		if (isPrimitive())  
			return serialize_primitive();
		else {
			String serialForm=
				getChromosome().getSpecies().getName() +">" +
				getChromosome().getId() + ":" +
				getModel().serialize();	        
			return serialForm;
		}
	}


	// code from the old, primitive class AbstractGeneticCoordinate
    /** returns the length of this coordinate. This is ALWAYS to-from+1.
     * for primitive coordinates, this is also the number of bases covered. 
     * for complex coordinates, see @see {@link AbstractComplexGeneticCoordinate.getCoveredBases()}; 
     * @return
     */
    public long length() {
        return (getTo() - getFrom() + 1 );
    }
    
    public boolean equals(Object obj)
    {
        return ( this.compareTo(obj)==0 );
    }

    
    public int hashCode() {
    	// hashcode is identical if compareTo==0
    	return (int)(getChromosome().hashCode()+getCoordinateAtoms().hashCode());
    }
    
    public abstract Chromosome getChromosome();

    /** 
	 * @return the first position of the coordinate, INCLUDED in the coordinate     
	 */
    public abstract long getFrom();

    public abstract Strand getStrand();

    /** 
     * @return the last position of the coordinate, INCLUDED in the coordinate
     */
    public abstract long getTo();
 
    public long getUpstreamCoordinate() {
    	return getStrand()==Strand.MINUS ? getTo() : getFrom();
    }
    
    public long getDownstreamCoordinate() {
    	return getStrand()==Strand.MINUS ? getFrom() : getTo();
    }
    
    public static enum DistanceAnchor {
    	FROM,
    	TO,
    	UPSTREAM,
    	DOWNSTREAM,
    	CENTER,
    	CLOSEST;
    };
    
    public long getAnchor(DistanceAnchor d) {
     	switch(d) {
    	case FROM: return getFrom();
    	case TO: return getTo();
    	case UPSTREAM: return getUpstreamCoordinate();
    	case DOWNSTREAM: return getDownstreamCoordinate();
    	case CENTER: 
    		double mean = ((double)(getFrom()+getTo()))/2d;
    		if (getStrand()==Strand.PLUS) // round towards upstream always
    			return (long)Math.floor(mean);
    		return (long)Math.ceil(mean);
    	}
     	return -1;
    }
    
    /** returns the distance (i.e. number of bases) between the two coordinates or -1 if 
     * coordinates are on different chromosomes or strands. 
     */
    public long getDistanceTo(AbstractGeneticCoordinate agc) {
    	return getDistanceTo(agc, false);
    }
    
    /** returns the distance (i.e. number of bases) between the two coordinates.
     * @param ignoreChromosomeAndStrand return distance EVEN if coordinates are on different strands or chromosomes
     */
    public long getDistanceTo(AbstractGeneticCoordinate agc, boolean ignoreChromosomeAndStrand) {
    	if (!ignoreChromosomeAndStrand) {
    		if (!agc.getChromosome().equals(this.getChromosome()))
    			return -1;
    		if (!agc.getStrand().similar(this.getStrand()))
    			return -1;
    	}
    	long startOverlap = Math.max(agc.getFrom(), getFrom());
    	long endOverlap = Math.min(agc.getTo(), getTo());    	
    	return - Math.min(0, endOverlap-startOverlap+1);
    }
    
    /** returns the distance between this coordinate (determined by anchor) and position. This distance
     * may be negative or positive, the sign signifies whether the position given is 
     * downstream (pos distance) or upstream (neg. distance) of this coordinate  
     */
    public long getDistanceTo(long position, DistanceAnchor anchor) {
    	if (anchor==DistanceAnchor.CLOSEST) {
    		long dfrom = position-getFrom();
    		long dto = position-getTo();
    		if (Math.abs(dfrom)<Math.abs(dto)) 
    			return dfrom;
    		else
    			return dto;
    	}
       	long myPos = getAnchor(anchor);
       	return position-myPos;
    }
    
    /** returns true if, IN THE strand-specific DIRECTION OF THIS COORDINATE, the other coordinate lies "upstream".
     * I.e. if this coordinate is on the PLUS strand, this.from>other.from and
     * if this coordinate is on the MINUS strand, this.to<other.to
     * @param c
     * @return
     */
    public boolean isDownstreamOf(AbstractGeneticCoordinate c) {
    	if (getStrand()==Strand.MINUS)
    		return c.getTo()<this.getTo();
    	return this.getFrom()>c.getFrom();
    }
    
    /** returns true if, IN THE strand-specific DIRECTION OF THIS COORDINATE, the other coordinate lies "downstream".
     * I.e. if this coordinate is on the PLUS strand, this.from<other.from and
     * if this coordinate is on the MINUS strand, this.to>other.to
     * @param c
     * @return
     */
    public boolean isUpstreamOf(AbstractGeneticCoordinate c) {
    	return !isDownstreamOf(c);
    }
    

    /** returns true if all bases in this coordinate are also bases in the other coordinate */ 
    public boolean isCompletelyCoveredBy(AbstractGeneticCoordinate c) {
    	return getOverlappingBaseCount(c)==getCoveredBases();
    }
    
    /** returns true if this coordinate completely lies within the boundaries of the other coordinate */
    public boolean isCompletelySpannedBy(AbstractGeneticCoordinate c) {
    	return getFrom()>=c.getFrom() && getTo()<=c.getTo();
    }

    public boolean isValid() {
    	return getFrom()>=0 && getTo()>=0 && getFrom()<=getTo() && getStrand()!=null && getChromosome()!=null; 
    }
    
	public String serialize_primitive()
	{
		String serialForm=
			getChromosome().getSpecies().getName() +">" +
			getChromosome().getId() + ":" +
			getFrom() + "-" +
			getTo() + 
			(getStrand()!=Strand.UNSPECIFIED ? ":" + getStrand().toChar(): "" )
			;
		return serialForm;
	}
	
}
