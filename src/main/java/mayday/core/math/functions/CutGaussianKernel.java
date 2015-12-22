/*
 * File CutGaussianKernel.java
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
public class CutGaussianKernel implements IOneDimFunction,IRotationalKernelFunction {
	private double sigma; 
	
	public CutGaussianKernel(double Sigma){
		this.sigma = Math.abs(Sigma);		
	}
	
	/* (non-Javadoc)
	 * @see MathObjects.Functions.IOneDimFunctions#getFunctionValueOf(double)
	 */
	 /**
	  * This method returns the CutGaussian kernel value for the distance given by the 
	  * input parameter. The CutGaussian is a mixture of Gaussian and Bubble kernel 
	  * function. It gives back the value of the Gaussian kernel if the distance is smaller 
	  * than sigma. 
	  * @param Distance gives the distance to the center of the Gaussian kernel
	  */
	public double getFunctionValueOf(double Distance) {
		if ( Math.abs(Distance) <= this.sigma ) {
			double Expo = - Math.pow( Distance,2.0) / (2.0 * Math.pow(this.sigma, 2.0));
			return Math.exp( Expo );
		} else return 0.0;
	}
	
	/**
	 * @return
	 */
	public double getSigma() {
		return this.sigma;
	}

	/**
	 * @param Sigma
	 */
	public void setSigma(double Sigma) {
		this.sigma = Math.abs(Sigma);
	}
    
    /**
     * Get the defining parameter of this kernel
     * @return
     */
    public double getRadius() {
        return this.sigma;
    }

    /**
     * Set the defining parameter of this kernel
     * @param Radius
     */
    public void setRadius(double Radius) {
        this.sigma = Math.abs(Radius);
    }

}
