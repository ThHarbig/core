/**
 *  File InitializerType.java 
 *  Created on 05.04.2005
 *  As part of the package MathObjects.Initializer
 *  By Janko Dietzsch
 *  
 */
package mayday.core.math.clusterinitializer;

/**
 * This class implements type selector for initializer of the unit maps.
 * 
 * @author  Janko Dietzsch
 * @version 0.1
 * 
 */
public enum InitializerType {
	KMEANSPP("k-Means++") {
    	/**
    	 * Method to create a k-Means++ data point initializer object
    	 * @ reference to the ICLusterInitializer-Interface of the newly created KmeansppInitializer object
    	 */
    	public IClusterInitializer createInstance() {
    		return new KmeansppInitializer();
    	}
    },
    RANDOM("Random initializer") {
        /**
         * Method to create a random initializer object
         * @return reference to the IClusterInitializer-Interface of the newly created RandomInitializer object
         */
        public IClusterInitializer createInstance() {
            return new RandomInitializer();
        }
    },
    RANDOM_DATA_POINT("Random data point") {
        /**
         * Method to create a RandomDataPoint-initializer object
         * @return reference to the IClusterInitializer-Interface of the newly created RandomDataPointInitializer object
         */
        public IClusterInitializer createInstance() {
            return new RandomDataPointInitializer();
        }
    };
    
    private final String type;
    
    private InitializerType(String type) {this.type = type;};
    public String getName() {return this.type;};
    public String toString() {return this.type;};
    
    /**
     * Abstract method to get an instance of an cluster initializer 
     * object according the enum type. This function must be overriden 
     * by every concrete enum type.
     *   
     * @return reference to the IClusterInitializer-Interface of the created ClusterInitializer-instance
     */
    public abstract IClusterInitializer createInstance();
}
