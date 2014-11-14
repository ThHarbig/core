/**
 *  File RotationalKernelFunctionType.java 
 *  Created on 05.04.2005
 *  As part of the package MathObjects.Functions
 *  By Janko Dietzsch
 *  
 */
package mayday.core.math.functions;

/**
 * This class implements type selector for the wanted kernel function.
 * 
 * @author  Janko Dietzsch
 * @version 0.1
 * 
 */
public enum RotationalKernelFunctionType {
    GAUSSIAN("Gaussian") {
        /**
         * Method to create a gaussian kernel function
         * @return reference to the IRotationalKernelFunction-Interface of the newly created GaussianKernel object
         */
        public IRotationalKernelFunction createInstance(double radius) {
            return new GaussianKernel(radius);
        }  
    },
    CUT_GAUSSIAN("Cut gaussian") {
        /**
         * Method to create a cut gaussian kernel function
         * @return reference to the IRotationalKernelFunction-Interface of the newly created CutGaussianKernel object
         */
        public IRotationalKernelFunction createInstance(double radius) {
            return new CutGaussianKernel(radius);
        }  
    },
    BUBBLE("Bubble") {
        /**
         * Method to create a bubble function
         * @return reference to the IRotationalKernelFunction-Interface of the newly created BubbleFunction object
         */
        public IRotationalKernelFunction createInstance(double radius) {
            return new BubbleFunction(radius);
        }  
    },
    PARABOLA("Parabola") {
        /**
         * Method to create a parabola kernel function
         * @return reference to the IRotationalKernelFunction-Interface of the newly created ParabolaKernel object
         */
        public IRotationalKernelFunction createInstance(double radius) {
            return new ParabolaKernel(radius);
        }  
    };
    
    private final String type;
    
    private RotationalKernelFunctionType(String type) {this.type = type;};
    public String getName() {return this.type;};
    public String toString() {return this.type;};
    
    /**
     * Abstract method to get an instance of a rotational kernel function 
     * object according the enum type. This function must be overriden 
     * by every concrete enum type.
     *   
     * @return reference to the IRotationalKernelFunction-Interface of the created ClusterInitializer-instance
     */
    public abstract IRotationalKernelFunction createInstance(double radius);
}
