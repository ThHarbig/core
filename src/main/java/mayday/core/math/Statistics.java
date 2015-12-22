package mayday.core.math;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import mayday.core.math.average.Mean;
import mayday.core.math.average.Median;
import mayday.core.structures.linalg.vector.AbstractVector;


public final class Statistics 
{
	
	protected static TreeSet<Integer> createSortingSet(double... x) {
		DoubleArrayComparator dbArrComp = new DoubleArrayComparator(x);
		TreeSet<Integer> SortingIndices = new TreeSet<Integer>(dbArrComp);
		
		for (int i = 0; i < x.length; i++) 
			SortingIndices.add(i);
		
		return SortingIndices;
	}
	
	
	protected static TreeSet<Integer> createSortingSet(List<Double> x) {
		DoubleListComparator dbArrComp = new DoubleListComparator(x);
		TreeSet<Integer> SortingIndices = new TreeSet<Integer>(dbArrComp);
		
		for (int i = 0; i < x.size(); i++) 
			SortingIndices.add(i);
		
		return SortingIndices;
	}
	
	/**
	 * Calculates the ordering of a double array:
	 * x={34, 27, 45, 55, 22, 34} leads to:
	 * [4, 1, 0, 5, 2, 3]
	 * NAs are always sorted to the end.
	 * This function's result is the same as order(x)-1 in R
	 * @param x the array of doubles to order
	 * @return the array of indices that define the ordering on the input
	 */
	public static int[] order(double... x)
	{
		int[] res=new int[x.length];
		
		int i=0;		
		for (int r: createSortingSet(x)) {
			res[i++]=r;
		}
		return res;	
	}

	/**
	 * Calculates the ordering of a double array:
	 * x={34, 27, 45, 55, 22, 34} leads to: [4, 1, 0, 5, 2, 3]
	 * NAs are always sorted to the end.
	 * This function's result is the same as order(x)-1 in R
	 * @param x the list of doubles to order
	 * @return the list of indices that define the ordering on the input
	 */
	public static List<Integer> order(List<Double> x)
	{
		List<Integer> res=new ArrayList<Integer>(x.size());
		
		for (int r:createSortingSet(x)) {
			res.add(r);
		}
		return res;	
	}

	/**
	 * Calculates the ranks of the values in x: 
	 * x={34, 27, 45, 55, 22, 34} leads to: [3, 2, 5, 6, 1, 4]
	 * NAs are always assigned the highest ranks. 
	 * This function's result is the same as rank(x, ties="first") in R
	 * @param x the double array to rank
	 * @return an array of integer ranks
	 */
	public static int[] rank(double... x)
	{
		int[] res=new int[x.length];
		int i=0;
		
		for (int r:createSortingSet(x)) {
			res[r]=++i;
		}
		return res;
	}

	/**
	 * Calculates the ranks of the values in x
	 * x={34, 27, 45, 55, 22, 34} leads to: [3, 2, 5, 6, 1, 4]
	 * NAs are always assigned the highest ranks. 
	 * This function's result is the same as rank(x, ties="first") in R
	 * @param x the list of doubles to rank
	 * @return a list of integer ranks
	 */
	public static List<Integer> rank(List<Double> x)
	{
		List<Integer> res=new ArrayList<Integer>(x.size());
		for (int i=0; i!=x.size(); ++i)
			res.add(0);
		
		int i=0;

		for (int r:createSortingSet(x)) {
			res.set(r,++i);
		}
		return res;
	}

	/**
	 * Calculates the mean of an array of double
	 * @param x
	 * @param ignoreNA set to true if NAs should be excluded from computation
	 * @return
	 */
	public static double mean(double[] x, boolean ignoreNA)
	{
		return Mean.sharedInstance().getAverage(x, ignoreNA);
	}
	
	public static double mean(double... x) {
		return mean(x,false);
	}

	/**
	 * Calculates the mean of a list of double
	 * @param x
	 * @param ignoreNA set to true if NAs should be excluded from computation
	 * @return
	 */
	public static double mean(List<Double> x, boolean ignoreNA)
	{		
		return Mean.sharedInstance().getAverage(x, ignoreNA);
	}
	
	public static double mean(List<Double> x) {
		return mean(x, false);
	}
	
	public static double mean(AbstractVector x, boolean ignoreNA)
	{
		return Mean.sharedInstance().getAverage(x, ignoreNA);
	}

	/**
	 * Calculates the median of an array of double 
	 * @param x
	 * @param ignoreNA set to true if NAs should be excluded from computation
	 * @return
	 */
	public static double median(double[] x, boolean ignoreNA)		
	{
		return Median.sharedInstance().getAverage(x, ignoreNA);
	}
	
	public static double median(double... x) {
		return median(x, false);
	}

	public static double median(AbstractVector x, boolean ignoreNA)
	{
		return Median.sharedInstance().getAverage(x, ignoreNA);
	}

	
	/**
	 * Calculates the median of a list of double
	 * @param x
	 * @param ignoreNA set to true if NAs should be excluded from computation
	 * @return
	 */
	public static double median(List<Double> x, boolean ignoreNA)
	{
		return Median.sharedInstance().getAverage(x, ignoreNA);
	}
	
	public static double median(List<Double> x) {
		return median(x, false);
	}

	/**
	 * Calculates the standard deviation
	 * @param x
	 * @param ignoreNA set to true if NAs should be excluded from computation
	 * @return
	 */
	public static double sd(double[] x, boolean ignoreNA) 
	{
		return sd(x, ignoreNA, mean(x,ignoreNA));
	}
	
	public static double sd(double... x ) {
		return sd(x, false);
	}

	private static double sd(double[] x, boolean ignoreNA, double precalculatedMean) {
		double sd=0;
		double mean=precalculatedMean;
		int ignored=0;
		for (double d : x )
			if (ignoreNA && Double.isNaN(d))
				++ignored;
			else
				sd+=(d-mean)*(d-mean); 
		sd=Math.sqrt(sd/(x.length-ignored-1));
		return sd;
	}
	
	public static double sd(AbstractVector x, boolean ignoreNA) 
	{
		return sd(x, ignoreNA, mean(x,ignoreNA));
	}
	
	private static double sd(AbstractVector x, boolean ignoreNA, double precalculatedMean) {
		double sd=0;
		double mean=precalculatedMean;
		int ignored=0;
		for (int i=0; i!=x.size(); ++i) {
			double d = x.get(i);
			if (ignoreNA && Double.isNaN(d))
				++ignored;
			else
				sd+=(d-mean)*(d-mean);
		}
		sd=Math.sqrt(sd/(x.size()-ignored-1));
		return sd;
	}
	/**
	 * Calculates the standard deviation
	 * @param x
	 * @param ignoreNA set to true if NAs should be excluded from computation
	 * @return
	 */
	public static double sd(List<Double> x, boolean ignoreNA ) 
	{
		return sd(x, ignoreNA, mean(x,ignoreNA));
	}	
	
	public static double sd(List<Double> x ) {
		return sd(x, false);
	}

	private static double sd(List<Double> x, boolean ignoreNA, double precalculatedMean) {
		double sd=0;
		double mean=precalculatedMean;
		int ignored=0;
		for (Double d : x )
			if (ignoreNA && Double.isNaN(d))
				++ignored;
			else
				sd+=(d-mean)*(d-mean); 
		sd=Math.sqrt(sd/(x.size()-ignored-1));
		return sd;
	}
	
	/**
	 * Calculates the median absolute deviation
	 * @param x
	 * @param ignoreNA set to true if NAs should be excluded from computation
	 * @return
	 */
	public static double mad(double[] x, boolean ignoreNA) 
	{
		return mad(x, ignoreNA, mean(x,ignoreNA));
	}
	
	public static double mad(double... x ) {
		return mad(x, false);
	}

	private static double mad(double[] x, boolean ignoreNA, double precalculatedMedian) {
		ArrayList<Double> devs=new ArrayList<Double>();
		double median=precalculatedMedian;
//		int ignored=0;
		for (double d : x )
			if (ignoreNA && Double.isNaN(d)) {
//				++ignored;
			} else
				devs.add(Math.abs(d-median)); 
		double mad=1.4826*median(devs);
		return mad;
	}
	
	public static double mad(AbstractVector x, boolean ignoreNA) {
		return mad(x, ignoreNA, mean(x,ignoreNA));
	}
	
	private static double mad(AbstractVector x, boolean ignoreNA, double precalculatedMedian) {
		ArrayList<Double> devs=new ArrayList<Double>();
		double median=precalculatedMedian;
//		int ignored=0;
		for (int i=0; i!=x.size(); ++i) {
			double d = x.get(i);
			if (ignoreNA && Double.isNaN(d)) {
//				++ignored;
			} else
				devs.add(Math.abs(d-median)); 
		}
		double mad=1.4826*median(devs);
		return mad;
	}
	/**
	 * Calculates the standard deviation
	 * @param x
	 * @param ignoreNA set to true if NAs should be excluded from computation
	 * @return
	 */
	public static double mad(List<Double> x, boolean ignoreNA ) 
	{
		return mad(x, ignoreNA, median(x,ignoreNA));
	}	
	
	public static double mad(List<Double> x ) {
		return mad(x, false);
	}

	private static double mad(List<Double> x, boolean ignoreNA, double precalculatedMedian) {
		ArrayList<Double> devs=new ArrayList<Double>();
		double median=precalculatedMedian;
//		int ignored=0;
		for (Double d : x )
			if (ignoreNA && Double.isNaN(d)) {
//				++ignored;
			} else
				devs.add(Math.abs(d-median)); 
		double mad=1.4826*median(devs);
		return mad;
	}
	
	/**
	 * z-Score normalizes the input array
	 * @param x
	 * @param ignoreNA set to true if NAs should be excluded from computation
	 * @return
	 */
	public static void normalize(double[] x, boolean ignoreNA) 
	{
		double mean=mean(x, ignoreNA);
		double sd=sd(x, ignoreNA, mean);
		if (sd==0) 
			sd=1;
		for (int i=0;i<x.length;i++) x[i]=(x[i]-mean)/sd;			
	}
	
	public static void normalize(double... x) {
		normalize(x, false);
	}
	
	public static void normalize(AbstractVector x, boolean ignoreNA) 
	{
		double mean=mean(x, ignoreNA);
		double sd=sd(x, ignoreNA, mean);
		if (sd==0) 
			sd=1;
		x.add(mean);
		x.divide(sd);			
	}

	/**
	 * z-Score normalizes the input list
	 * @param x
	 * @param ignoreNA set to true if NAs should be excluded from computation
	 * @return
	 */
	public static void normalize(List<Double> x, boolean ignoreNA) 
	{
		double mean=mean(x, ignoreNA);
		double sd=sd(x, ignoreNA, mean);
		for (int i=0;i<x.size();i++) 
			x.set(i,(x.get(i)-mean)/sd);			
	}
	
	public static void normalize(List<Double> x) {
		normalize(x, false);
	}

	/**
	 * Calculates the k-th q-quantile of a sorted list of values. If the values are not sorted, the result is undefined.
	 * @param x The sorted sample values
	 * @param q The quantile (i.e. 100 for percentiles, 4 for quartiles)
	 * @param k The number of the quantile
	 */
	public static double quantile(List<Double> x, double q, double k)
	{
		if(k > q) throw new IllegalArgumentException("Can not calculate the "+k+"th quantile of "+q);		
		double p=(x.size()-1)*(k/q);
		int idx=(int)Math.ceil(p);
		return x.get(idx); 
	}
	
	
//	public static void main(String... arg) {
//		double[] a = new double[]{Double.POSITIVE_INFINITY, 34, 34, 45, Double.NaN, 55, 22, 34, Double.NaN};
//		System.out.println(Arrays.toString(rank(a)));
//		System.out.println(Arrays.toString(order(a)));
//		List<Double> l = new ArrayList<Double>();
//		for (double d : a)
//			l.add(d);
//		System.out.println(rank(l));
//		System.out.println(order(l));
//	}
	
	
	
	
	public static final class DoubleArrayComparator implements Comparator<Integer> {
		
		double[] compDoubArray;
		
		public DoubleArrayComparator(double[] doubleArray)	{
			this.compDoubArray = doubleArray;
		}
		
		public int compare(Integer ind1, Integer ind2)	{
			
			// Equal numbers are treated as different to keep all numbers alive in TreeSet.
			// If this would not be done TreeSet would reduce ties to only one number.
			// ==> NEVER RETURN 0
			
			double d1 = compDoubArray[ind1];
			double d2 = compDoubArray[ind2];
			
			if (Double.isNaN(d1))
				return 1; 
			if (Double.isNaN(d2))
				return -1;
			
			if (d1-d2 < 0d) 
				return -1;
			
			return 1;
			
		}
	}
	
	public static final class DoubleListComparator implements Comparator<Integer>
	{
		List<Double> compDoubList;
		
		public DoubleListComparator(List<Double> doubleList) {
			this.compDoubList = doubleList;
		}
		
		public int compare(Integer ind1, Integer ind2) 
		{
			// Equal numbers are treated as different to keep all numbers alive in TreeSet.
			// If this would not be done TreeSet would reduce ties to only one number.
			// ==> NEVER RETURN 0
			
			double d1 = compDoubList.get(ind1);
			double d2 = compDoubList.get(ind2);
			
			if (Double.isNaN(d1))
				return 1; 
			if (Double.isNaN(d2))
				return -1;
			
			if (d1-d2 < 0d) 
				return -1;
			
			return 1;
		}
	}

}
