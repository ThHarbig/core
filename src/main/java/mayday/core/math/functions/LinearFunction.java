/**
 * File LinearFunction.java
 * Created on 08.03.2005
 * As part of package MathObjects.Functions
 * By Janko Dietzsch
 *
 */

package mayday.core.math.functions;

/**
 * This class implements a linear function. 
 * @author Janko Dietzsch
 * @version 0.1
 */
public class LinearFunction implements IOneDimFunction {
    private double slope;
    private double intercept;

    public LinearFunction(double slope, double intercept) {
        this.slope = slope;
        this.intercept = intercept;
    }

    /**
     * This method is used to get intercept. 
     *
     * @return Returns the intercept.
     */
    public double getIntercept() {
        return intercept;
    }
    

    /**
     * This method is used to set the intercept 
     *
     * @param intercept The intercept to set.
     */
    public void setIntercept(double intercept) {
        this.intercept = intercept;
    }
    

    /**
     * This method is used to get slope. 
     *
     * @return Returns the slope.
     */
    public double getSlope() {
        return slope;
    }
    

    /**
     * This method is used to set the slope 
     *
     * @param slope The slope to set.
     */
    public void setSlope(double slope) {
        this.slope = slope;
    }
    

    /* (non-Javadoc)
     * @see MathObjects.Functions.IOneDimFunction#getFunctionValueOf(double)
     */
    public double getFunctionValueOf(double x) {
        double y = this.slope * x + this.intercept; 
        return y;
    }

}
