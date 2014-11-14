/*
 * File IAverage.java
 * Created on 07.07.2005
 * As part of package MathObjects.Functions
 * By Janko Dietzsch
 *
 */

package mayday.core.math.average;

import java.util.List;

import mayday.core.structures.linalg.vector.AbstractVector;

/**
 * 
 * The interface IAverage represents different averaging methods.
 *
 * @author Janko Dietzsch
 * @version 0.1
 */
public interface IAverage {
	
	/**
	 * Calculate the average of an array of doubles.
	 * @param x an array of double
	 * @return The average of x
	 */
	public double getAverage(double[] x);
	
	/**
	 * Caluclate the average of a NumVector. 
	 * @param x A num Vector
	 * @return The average of x
	 */
	public double getAverage(AbstractVector x);
	
	/**
	 * Calculate  the average on any list containing doubles.
	 * @param a list of doubles
	 * @return The average of x
	 */
	public double getAverage(List <Double> x);
	
	/**
	 * Calculate the average of an array of doubles.
	 * @param x an array of double
	 * @param ignoreNA set to true if NA should be ignored for computation
	 * @return The average of x
	 */
	public double getAverage(double[] x, boolean ignoreNA);
	
	/**
	 * Caluclate the average of a NumVector. 
	 * @param x A num Vector
	 * @param ignoreNA set to true if NA should be ignored for computation
	 * @return The average of x
	 */
	public double getAverage(AbstractVector x, boolean ignoreNA);
	
	/**
	 * Calculate  the average on any list containing doubles.
	 * @param a list of doubles
	 * @param ignoreNA set to true if NA should be ignored for computation
	 * @return The average of x
	 */
	public double getAverage(List <Double> x, boolean ignoreNA);
	
}
