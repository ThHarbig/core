package mayday.clustering.qt.algorithm;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public abstract class QTPMath {
	/**
	 * @param values
	 */
	public static void round(double[] values) {
		for(int i = 0; i < values.length; i++) {
			values[i] = Math.round(values[i]*100.)/100.;
		}
	}
	
	/**
	 * @param values
	 * @return maximum of values
	 */
	public static double getArrayMax(double[] values) {
		double max = values[0];
		for(int i = 0; i < values.length; i++) {
			if(values[i] > max) {
				max = values[i];
			}
		}
		return max;
	}
	
	/**
	 * @param values
	 * @return minimum of values
	 */
	public static double getArrayMin(double[] values) {
		double min = values[0];
		for(int i = 0; i < values.length; i++) {
			if(values[i] < min) {
				min = values[i];
			}
		}
		return min;
	}
	
	/**
	 * @param values
	 * @return maximum of values
	 */
	public static int getArrayMax(int[] values) {
		int max = values[0];
		for(int i = 0; i < values.length; i++) {
			if(values[i] > max) {
				max = values[i];
			}
		}
		return max;
	}
	
	/**
	 * @param value
	 * @return rounded value
	 */
	public static double round(double value) {
		return Math.round(value*100.)/100.;
	}
	
	/**
	 * 
	 * @param low
	 * @param high
	 * @return random int value between low and high
	 */
	public static int random(int low, int high) {  
	    return (int) Math.floor((Math.random() * (high - low) + low));  
	}
	
	/**
	 * Every distance-value is represented just once
	 * @param array
	 * @return distances
	 */
	public static double[] removeDuplicatedEntries(double[] array) {
		Set<Double> treeSet = new TreeSet<Double>();

		for(int i = 0; i < array.length; i++)
			treeSet.add(array[i]);
		
		double[] newArray = new double[treeSet.size()];
		
		int i = 0;
		for(double d : treeSet)
			newArray[i++] = d;
		
		return newArray;
	}
	
	public static double[] toArray(ArrayList<Double> list) {
		double[] newArray = new double[list.size()];
		
		int i = 0;
		for(Double d : list)
			newArray[i++] = d.doubleValue();
		
		return newArray;
	}
}
