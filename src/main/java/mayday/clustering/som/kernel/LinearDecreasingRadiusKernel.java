/*
 *  File LinearDecreasingRadiusKernel.java 
 *  Created on 08.03.2005
 *  As part of the package mayday.clustering.som.kernel
 *  By Janko Dietzsch
 *  
 */
package mayday.clustering.som.kernel;

import mayday.core.math.functions.IRotationalKernelFunction;
import mayday.core.math.functions.LinearFunction;


/**
 * @author Janko Dietzsch
 * @version 0.1
 */
public class LinearDecreasingRadiusKernel implements ISomKernel {
    private double startRadius;
    private double endRadius;
    private int maxTicks;
    private int maxDistance;
    private IRotationalKernelFunction kernel;
    
    private int tickCount;
    
    private LinearFunction timeFunction;
    private double[] tabulatedValues;
      
    
    public LinearDecreasingRadiusKernel(double startRadius, double endRadius, int maxTicks, int estMaxDistance, IRotationalKernelFunction kernel) {
        this.startRadius = startRadius;
        this.endRadius = endRadius;
        this.maxTicks = maxTicks;
        this.kernel = kernel;
        
        this.tickCount = -1;
        
        // Initialize the time function
        double slope = (this.endRadius - this.startRadius) / this.maxTicks;
        double intercept = this.startRadius;
        this.timeFunction = new LinearFunction(slope,intercept);
        
        // Initialize the cache-array
        this.setMaxDistance(estMaxDistance);
    }



    /**
     * This method is used to get estMaxDistance. 
     *
     * @return Returns the estMaxDistance.
     */
    public int getMaxDistance() {
        return maxDistance;
    }


    /**
     * This method is used to get endRadius. 
     *
     * @return Returns the endRadius.
     */
    public double getEndRadius() {
        return endRadius;
    }
    

    /**
     * This method is used to set the endRadius 
     *
     * @param endRadius The endRadius to set.
     */
    public void setEndRadius(double endRadius) {
        this.endRadius = endRadius;
    }
    

    /**
     * This method is used to get kernel. 
     *
     * @return Returns the kernel.
     */
    public IRotationalKernelFunction getKernel() {
        return kernel;
    }
    

    /**
     * This method is used to set the kernel 
     *
     * @param kernel The kernel to set.
     */
    public void setKernel(IRotationalKernelFunction kernel) {
        this.kernel = kernel;
    }
    

    /**
     * This method is used to get startRadius. 
     *
     * @return Returns the startRadius.
     */
    public double getStartRadius() {
        return startRadius;
    }
    

    /**
     * This method is used to set the startRadius 
     *
     * @param startRadius The startRadius to set.
     */
    public void setStartRadius(double startRadius) {
        this.startRadius = startRadius;
    }
    

    /* (non-Javadoc)
     * @see clustering.som.kernel.ISomKernel#getValueOfDistance(int)
     */
    public double getValueOfDistance(int dist) {
        if ((dist < 0) || (this.maxDistance < dist)) return this.kernel.getFunctionValueOf(dist);
        else return this.tabulatedValues[dist];
    }

    /* (non-Javadoc)
     * @see clustering.som.kernel.ISomKernel#tick()
     */
    public void tick() {
        // Switch the time slice one round further or 
        // do nothing if the end already is reached 
        if (this.tickCount < this.maxTicks) this.tickCount++;
        else return;
        // Update the radius of the kernel
        double radius = this.timeFunction.getFunctionValueOf(this.tickCount);
        this.kernel.setRadius(radius);
        // Tabulate the values for all new distances
        for (int i = 0; i < this.tabulatedValues.length ; i++) this.tabulatedValues[i] = this.kernel.getFunctionValueOf(i);
    }

    /**
     * Maximal count of execution cycles reached?
     * @return True if all cycles executed.
     */
    public boolean endReached() {
        return (this.tickCount >= this.maxTicks) ;
    }



    public void setMaxCycles(int maxCycles) {
        this.maxTicks = maxCycles;
        double newSlope = (this.endRadius - this.startRadius) / this.maxTicks;
        this.timeFunction.setSlope(newSlope);
        this.resetTime();
    }



    public int getMaxCycles() {
        return this.maxTicks;
    }

    public void resetTime() {
        this.tickCount = -1;
        for (@SuppressWarnings("unused") double val: this.tabulatedValues) val = 0.0;
    }
    
    /**
     * Set the maximal distance that can will be observed.
     * Important for the used cache size
     * @param cache size
     */
    public void setMaxDistance(int maxDistance){
        this.maxDistance = maxDistance;
        this.tabulatedValues = new double[(this.maxDistance + 1)];
    }

    public double getCurrentProgress() {
    	if (this.tickCount == -1) return 0;
    	else return ((double) this.tickCount / this.maxTicks);
    }
}
