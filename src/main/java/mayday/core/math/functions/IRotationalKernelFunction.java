/**
 * File IRotationalKernelFunction.java
 * Created on 07.03.2005
 * As part of package MathObjects.Functions
 * By Janko Dietzsch
 *
 */
package mayday.core.math.functions;

/**
 * @author  Janko Dietzsch
 * @version 0.1
 * 
 */

public interface IRotationalKernelFunction extends IOneDimFunction {
    /**
     * Get the radius of the used kernel function.
     * @return 
     */
    public double getRadius();

    /**
     * Set the radius of the used kernel function.
     * @param Radius
     */
    public void setRadius(double Radius);
}
