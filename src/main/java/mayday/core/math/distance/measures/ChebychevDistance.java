/*
 *  Created on Aug 16, 2004
 *
 */
package mayday.core.math.distance.measures;

import java.util.HashMap;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;


/**
 * Chebychev Distance <p>
 * 
 *  The maximum distance between two corresponding elements of x and y:<p>
 * 
 *      dist(x,y) = max | x_i - y_i |
 * 
 * @author Markus Riester
 * @version 0.1
 */
public class ChebychevDistance extends DistanceMeasurePlugin {
	/* we are an abstractplugin
    and implement coreplugin(via distancemeasuretype) as well as idistancemeasure (via distancemeasure)
    */
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.distance.Chebychev",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Markus Riester",
					"riester@informatik.uni-tuebingen.de",
					"The chebychev distance: max | x_i - y_i |).",
					"Chebychev"
					);
			return pli;
	}
	
	public ChebychevDistance() {
		//empty for pluginmanager
	}
	
    public double getDistance(double[] VectorOne, double[] VectorTwo) {
        dimensionCheck(VectorOne, VectorTwo);
		double Distance = Double.MIN_VALUE;
		for (int x = 0; x < VectorOne.length; x++) {
		    double tmp = Math.abs( (VectorOne[x] - VectorTwo[x]) );
		    if (tmp > Distance) {
		        Distance = tmp;
		    }
		}
		return Distance;
    }

}
