/*
 *  Created on Aug 13, 2004
 *
 */
package mayday.core.math.distance.measures;

import java.util.HashMap;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;


/**
 * ManhattanDistance<p>
 * 
 * Defined as the absolute distance between the two vectors x and y
 * 
 * @author Markus Riester
 * @version 0.1
 *
 */
public class SupremumDifference extends DistanceMeasurePlugin {

	/* we are an abstractplugin
    and implement coreplugin(via distancemeasuretype) as well as idistancemeasure (via distancemeasure)
    */
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.distance.Supremum",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Florian Battke",
					"battke@informatik.uni-tuebingen.de",
					"The supremum norm is defined as the maximum of all coordinate-wise differences: max_i |x_i-y_i|",
					"Supremum"
					);
			return pli;
	}
	
	public SupremumDifference() {
		//empty for pluginmanager
	}
	
    public double getDistance(double[] VectorOne, double[] VectorTwo) {
        dimensionCheck(VectorOne, VectorTwo);
		double Distance = 0.0;
		for (int x = 0; x < VectorOne.length; x++) {
			double next = Math.abs( (VectorOne[x] - VectorTwo[x]) ); 
			Distance=(Distance>=next?Distance:next);
		}
		return Distance;
    }

}
