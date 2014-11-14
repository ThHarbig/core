/*
 * File IVectorFunction.java
 * Created on 07.07.2005
 * As part of package MathObjects.Functions
 * By Janko Dietzsch
 *
 */
package mayday.core.math.functions;

import mayday.core.structures.linalg.vector.DoubleVector;


/**
 * The interface IVectorFunction represents n-dimensional vector functions
 *
 * @author Janko Dietzsch
 * @version 0.1
 */
public interface IVectorFunction {
	
	public double getFunctionValueOf(double[] x);
	
	public double getFunctionValueOf(DoubleVector x);
}

