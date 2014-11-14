/*
 * File Mean.java
 * Created on 11.07.2005
 * As part of mayday.core.misc.MathObjects.statistics.average
 * By Janko Dietzsch
 */

package mayday.core.math.average;

import java.util.List;

import mayday.core.structures.linalg.vector.AbstractVector;

/**
 * Mean
 *
 * @author Janko Dietzsch
 * @version 0.1
 */
public class Mean extends AbstractAverage {

	/**
	 * Constructor of class Mean
	 */
	public Mean() {
		super();
	}

	public double getAverage(AbstractVector x, boolean ignoreNA) {
		double average = 0.0;
		int ignored=0;		
		for (int i=0; i!=x.size(); ++i) {
			double d = x.get(i);
			if (ignoreNA && Double.isNaN(d))
				++ignored;
			else
				average += d;
		}
		average = average / (x.size()-ignored);
		return average;
	}
	
	/* (non-Javadoc)
	 * @see mayday.core.misc.MathObjects.statistics.average.IAverage#getAverage(double[])
	 */
	public double getAverage(double[] x, boolean ignoreNA) {
		double average = 0.0;
		int ignored=0;		
		for (double d: x)
			if (ignoreNA && Double.isNaN(d))
				++ignored;
			else
				average += d;
		
		average = average / (x.length-ignored);
		return average;
	}

   /* (non-Javadoc)
     * @see mayday.core.math.average.IAverage#getAverage(java.util.List)
     */
    public double getAverage(List<Double> data, boolean ignoreNA) 
    {
    	double average=0.0d;
    	int l=0;
    	for(Double item:data)
    	{
    		if (item!=null && !(ignoreNA && Double.isNaN(item))) {
    			average+=item;
    			l++;
    		}
    	}
    	return average/l;    	
    }
    
    private static final Mean sharedInstance=new Mean();
    
    public static Mean sharedInstance()
    {
    	return sharedInstance;
    }
   
    public String toString() {
		return "Mean";
	}

}
