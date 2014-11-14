/*
 * File ISomKernel.java
 * Created on 07.03.2005
 * As part of package mayday.clustering.som.kernel
 * By Janko Dietzsch
 *
 */
package mayday.clustering.som.kernel;

/**
 * @author  Janko Dietzsch
 * @version 0.1
 * 
 * 
 */
public interface ISomKernel {
    /**
     * Get the value for the unit in distance dist.
     * @param dist Distance as integer units.
     * @return Value of the function at x = dist.
     */
    public double getValueOfDistance(int dist);
    /**
     * Switch the intern clock of the kernel one 
     * time slice further.
     */
    public void tick();
    /**
     * Maximal count of execution cycles reached?
     * @return True if all cycles executed.
     */
    public boolean endReached();
    /**
     * Set the maximal count of execution cycles.
     * @param Maximal count of cycles that will be executed.
     */
    public void setMaxCycles(int maxCycles);
    /**
     * Get the maximal count of execution cycles.
     * @return Maximal count of cycles that will be executed.
     */
    public int getMaxCycles();
    /**
     * Resets the kernel to the pre-start-condition
     */
    public void resetTime();
    
    /**
     * Set the maximal distance that can be observed.
     * Important for the used cache size
     * @param cache size
     */
    public void setMaxDistance(int maxDistance);
    
    /**
     * This method reports the actual progress status of the 
     * kernel as fraction current cycle count / maximal cycle count 
     * @return progress status as fraction of current cycle count / maximal cycle count
     */
    public double getCurrentProgress();
    
}
