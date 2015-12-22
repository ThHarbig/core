/*
 * File BubbleFunction.java
 * Created on 08.03.2004
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
public class BubbleFunction implements IRotationalKernelFunction {
	private double radius;
	
	public BubbleFunction(double Radius) {
		this.radius = Math.abs(Radius);
	}
	
	/* (non-Javadoc)
	 * @see MathObjects.Functions.IOneDimFunctions#getFunctionValueOf(double)
	 */ 
	/**
	  * This method returns the membership of the unit in the given distance to the 
	  * kernel.
	  * @param Distance gives the distance to the center of the kernel
	  */
	public double getFunctionValueOf(double Distance) {
		if (Math.abs(Distance) <= radius) return 1.0; 
		else return 0.0;
	}
	
	/**
	 * @return
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * @param Radius
	 */
	public void setRadius(double Radius) {
		this.radius = Math.abs(Radius);
	}

}
