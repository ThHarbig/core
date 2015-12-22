/*
 * File Median.java
 * Created on 11.07.2005
 * As part of mayday.core.misc.MathObjects.statistics.average
 * By Janko Dietzsch
 */

package mayday.core.math.average;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * Median
 *
 * @author Janko Dietzsch
 * @version 0.1
 */

public class Median extends AbstractAverage {

	/**
	 * Constructor of class Median
	 */
	public Median() {
		super();
	}

	/* (non-Javadoc)
	 * @see mayday.core.misc.MathObjects.statistics.average.IAverage#getAverage(double[])
	 */
	public double getAverage(double[] x, boolean ignoreNA) 
	{
		class DoubleArrayComparator implements Comparator<Integer> 
		{
			double[] compDoubArray;
			public DoubleArrayComparator(double[] doubleArray) 
			{
				this.compDoubArray = doubleArray;
			}
			public int compare(Integer ind1, Integer ind2) 
			{
				double res = this.compDoubArray[ind1] - this.compDoubArray[ind2];
				// Equal numbers are treated as different to keep all numbers alive in TreeSet.
				// If this would not be done TreeSet would reduce ties to only one number.
				int ret_res = +1; // = 0 
				if (res < 0.0) ret_res = -1;
				//if (res > 0.0) ret_res = +1;
				return ret_res;
			}
		}
		
		if (ignoreNA) {
			double[] x_noNA = new double[x.length];
			int j=0;
			for (int i=0; i!=x.length; ++i)
				if (!Double.isNaN(x[i]))
						x_noNA[j++] = x[i];
			x = Arrays.copyOf(x_noNA, j);
		}
		
		DoubleArrayComparator dbArrComp = new DoubleArrayComparator(x);
		TreeSet<Integer> SortingIndices = new TreeSet<Integer>(dbArrComp);
		int i;
		double median = 0.0;
		
		for (i = 0; i < x.length; i++) SortingIndices.add(i);
		i = 0;
		int median_ind = 0;
		Iterator<Integer> it = null;
		if ( x.length % 2 != 0) {// length is an odd number
			median_ind = (int) (x.length / 2); // first index is 0 and not 1 
			for (it = SortingIndices.iterator(); i!=median_ind; it.next() ) i++;
			median = x[it.next()];
		} else { // length is an even number
			median_ind = (int) (x.length / 2) - 1;
			for (it = SortingIndices.iterator(); i!=median_ind; it.next() ) i++;
			median = (x[it.next()] + x[it.next()]) / 2;
 		}
		
//		System.out.println();
//		for (it = SortingIndices.iterator();it.hasNext(); ) {
//			median_ind = it.next();
//			System.out.print(" " + median_ind + ":");
//			System.out.print(" " + x[median_ind] + " ");
//		}
//		System.out.print("\n");
//		System.out.println("Median of "+Arrays.toString(x)+"="+median);
		return median;
	}


	public double getAverage(List<Double> x, boolean ignoreNA) 
	{
		
		class DoubleArrayComparator implements Comparator<Integer> 
		{
			List<Double> compDoubList;
			public DoubleArrayComparator(List<Double> doubleArray) 
			{
				this.compDoubList = doubleArray;
			}
			public int compare(Integer ind1, Integer ind2) 
			{
				double res = this.compDoubList.get(ind1) - this.compDoubList.get(ind2);
				// Equal numbers are treated as different to keep all numbers alive in TreeSet.
				// If this would not be done TreeSet would reduce ties to only one number.
				int ret_res = +1; // = 0 
				if (res < 0.0) ret_res = -1;
				//if (res > 0.0) ret_res = +1;
				return ret_res;
			}
		}
		
		if (ignoreNA) {
			LinkedList<Double> x_noNA = new LinkedList<Double>();
			for (double d : x)
				if (!Double.isNaN(d))
						x_noNA.add(d);
			x = x_noNA;
		}
		
		DoubleArrayComparator dbArrComp = new DoubleArrayComparator(x);
		TreeSet<Integer> SortingIndices = new TreeSet<Integer>(dbArrComp);
		int i;
		double median = 0.0;
		
		for (i = 0; i < x.size(); i++) SortingIndices.add(i);
		i = 0;
		int median_ind = 0;
		Iterator<Integer> it = null;
		if ( x.size() % 2 != 0) {// length is an odd number
			median_ind = (int) (x.size() / 2); // first index is 0 and not 1 
			for (it = SortingIndices.iterator(); i!=median_ind; it.next() ) i++;
			median = x.get(it.next());
		} else { // length is an even number
			median_ind = (int) (x.size() / 2) - 1;
			for (it = SortingIndices.iterator(); i!=median_ind; it.next() ) i++;
			median = (x.get(it.next()) + x.get(it.next())) / 2;
 		}
			
		return median;
	}
	
    private static final Median sharedInstance=new Median();
    
    public static Median sharedInstance()
    {
    	return sharedInstance;
    }
	
    public String toString() {
		return "Median";
	}

}
