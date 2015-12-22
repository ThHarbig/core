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
public class ManhattanDistance extends DistanceMeasurePlugin {

	/* we are an abstractplugin
    and implement coreplugin(via distancemeasuretype) as well as idistancemeasure (via distancemeasure)
    */
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.distance.Manhattan",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Markus Riester",
					"riester@informatik.uni-tuebingen.de",
					"The Manhattan distance: sum (x_i - y_i)",
					"Manhattan"
					);
			return pli;
	}
	
	public ManhattanDistance() {
		//empty for pluginmanager
	}
	

    public double getDistance(double[] VectorOne, double[] VectorTwo) {
        dimensionCheck(VectorOne, VectorTwo);
		double Distance = 0.0;
		for (int x = 0; x < VectorOne.length; x++) {
			Distance += Math.abs( (VectorOne[x] - VectorTwo[x]) );
		}
		return Distance;
    }

}
