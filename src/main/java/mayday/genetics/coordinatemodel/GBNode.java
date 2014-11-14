package mayday.genetics.coordinatemodel;

import java.util.List;

import mayday.genetics.basic.Strand;

public interface GBNode {

	/** @return the strand of this coordinate, if possible */
	public Strand getStrand();
	
	/** @return the first covered base of this coordinate */
	public long getStart();
	
	/** @return the last covered base of this coordinate*/
	public long getEnd();
	
	/** @return a flat list of coordinate atoms */  
	public List<GBAtom> getCoordinateAtoms();
	
	/** memory-saving method to collect a list of coordinate atoms */
	public void addCoordinateAtoms(List<GBAtom> list);
	
	/** @return the number of bases covered by particles in this coordinate. is usually different from end-start.
	 * This is the SUM of all primitive components' lenghts. If components overlap, this sum is too large. */
	public long getCoveredBases();

	/** @return a Genbank-format serialization of this coordinate*/
	public String serialize();
	
	/** @return if this node contains a primitive model.
	 * The return value is identical to getCoordinateAtoms().size()>1 but the implementation is much faster
	 */
	public boolean isPrimitive();
	
}
