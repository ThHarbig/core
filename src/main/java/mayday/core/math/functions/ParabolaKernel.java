/*
 * File ParabolaKernel.java
 * Created on 09.03.2004
 *As part of package MathObjects.Functions
 *By Janko Dietzsch
 *
 */
package mayday.core.math.functions;

/**
 * @author  Janko Dietzsch
 * @version 0.1
 * 
 * 
 */
public class ParabolaKernel implements IRotationalKernelFunction {
	private double nullPoint; 
	private double exponent;
	
    public ParabolaKernel(double NullPoint) {
        this(NullPoint,2);
    }
    
	public ParabolaKernel(double NullPoint, double Exponent){
		this.nullPoint = Math.abs(NullPoint);
		this.exponent = Math.abs(Exponent);
		if ( (this.exponent % 2) != 0 ) this.exponent++;		
	}
	
	/* (non-Javadoc)
	 * @see MathObjects.Functions.IOneDimFunctions#getFunctionValueOf(double)
	 */
	 /**
	  * This method returns the parabola kernel value for the distance given by the 
	  * input parameter.
	  * @param Distance gives the distance to the center of the parabola kernel
	  */
	public double getFunctionValueOf(double Distance) {
		if ( Math.abs(Distance) <= this.nullPoint ) {
			return  ( 1 - Math.pow( (Distance/this.nullPoint) , this.exponent ) );
		} else return 0.0;
	}
	
	/**
	 * @return
	 */
	public double getNullPoint() {
		return this.nullPoint;
	}

	/**
	 * @param Sigma
	 */
	public void setNullPoint(double NullPoint) {
		this.nullPoint = Math.abs(NullPoint);
	}

	/**
	 * @return
	 */
	public double getExponent() {
		return this.exponent;
	}

	/**
	 * @param Exponent
	 */
	public void setExponent(double Exponent) {
		this.exponent = Math.abs(Exponent);
		if ( (this.exponent % 2) != 0 ) this.exponent++;		
	}

    /**
     * Get the defining parameter of this kernel
     * @return
     */
    public double getRadius() {
        return this.nullPoint;
    }

    /**
     * Set the defining parameter of this kernel
     * @param Radius
     */
    public void setRadius(double Radius) {
        this.nullPoint = Math.abs(Radius);
    }
    
}
