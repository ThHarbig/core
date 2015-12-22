/**
 * File AverageType.java
 * Created on 05.07.2004
 * As part of package clustering.kmeans
 * By Janko Dietzsch
 *
 * Modified 2009-10-09 to prevent excessive object creation and add harmonic/geometric means
 * By fb
 */

package mayday.core.math.average;

public enum AverageType {
	MEAN("mean") {
        public IAverage createInstance() {
            return Mean.sharedInstance();
        }
	},
	MEDIAN("median") {
        public IAverage createInstance() {
            return Median.sharedInstance();
        }
	},
	HARM("harmonic mean"){
		public IAverage createInstance() {
			return new HarmonicMean();
		}		
	}
	;
	
	private final String type;
	
	private AverageType(String type) {this.type = type;}
	public String getName() {return this.type;}
	public String toString() {return this.type;}
	
	/**
     * Abstract method to get an instance of an average algorithm 
     * object according the enum type. This function must be overriden 
     * by every concrete enum type.
     *   
     * @return reference to the IClusterInitializer-Interface of the created ClusterInitializer-instance
     */
    public abstract IAverage createInstance();
}
