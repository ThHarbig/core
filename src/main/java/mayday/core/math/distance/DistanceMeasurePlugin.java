/*
 * File DistanceMeasure.java
 * Created on 28.02.2004
 *As part of package MathObjects.DistanceMeasures
 *By Janko Dietzsch
 *
 */
package mayday.core.math.distance;

import mayday.core.Probe;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginManager;
import mayday.core.structures.linalg.vector.AbstractVector;


/**
 * @author  Janko Dietzsch
 * @author  Markus Riester 
 * @version 0.2
 */
public abstract class DistanceMeasurePlugin extends AbstractPlugin {
	
	public final static String MC = "Math/Distance Measures"; 
	
	public double getDistance(AbstractVector Vec1, AbstractVector Vec2) {
		return getDistance(Vec1.toArrayUnpermuted(), Vec2.toArrayUnpermuted());
	}
	
	public double getDistance(AbstractVector Vec1, double[] Vec2) {
		return getDistance(Vec1.toArrayUnpermuted(), Vec2);
	}
	
	public double getDistance(double[] Vec1, AbstractVector Vec2) {
		return getDistance(Vec1, Vec2.toArrayUnpermuted());
	}
	
	public double getDistance(Probe pb1, Probe pb2) {
		return getDistance(pb1.getValues(), pb2.getValues());
	}
	
	public abstract double getDistance(double[] VectorOne, double[] VectorTwo);
	
    public void dimensionCheck(double[] Vec1, double[] Vec2) {
		if ( Vec1.length != Vec2.length ) 
			throw new IllegalArgumentException("Dimension of the vectors are different");
	}
    
    public void dimensionCheck(AbstractVector Vec1, AbstractVector Vec2) {
		if ( Vec1.size() != Vec2.size() ) 
			throw new IllegalArgumentException("Dimension of the vectors are different");
	}
    
    public void init() {}
    
    public String toString() {
    	return PluginManager.getInstance().getPluginFromClass(getClass()).getName();
    }
    
    public boolean equals(Object o) {
    	return toString().equals(o.toString()); 
    }

}
