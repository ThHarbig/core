package mayday.core.structures;

import java.util.LinkedList;

/** The CompactableStrucure interface defines functions to make an object smaller in memory.
 * Furthermore, classes implementing this interface can be queried for parameters that will
 * allow initialization with the correct memory footprint for the currently contained data.
 * @author battke
 *
 */
public interface CompactableStructure {

	/** 
	 * Reduce the memory footprint of this object, if possible.
	 * Many structures do not allow further modification after a call to compact()
	 */
	public void compact();
	
	/** 
	 * Provide a string representation of the current compaction status of this object.
	 * Should be called after compact() was performed.
	 * @return a string that can be used to initialize this object with the correct memory
	 * footprint when it is created the next time, @see setCompaction()
	 */
	public String getCompactionInitializer(); // string separated by tabs, children elements at the end
	
	/**
	 * set the compaction rate of this object using values learned last time it was used.
	 * This should be done _before_ putting data into the structure
	 * @param compactionInitializer the information recieved from @see getCompactionInitializer()
	 */
	public void setCompaction(LinkedList<String> compactionInitializer); // remove as many as you need, then hand on to children
}
