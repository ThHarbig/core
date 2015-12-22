/*
 * File EuclideanDistance.java
 * Created on 29.02.2004
 *As part of package MathObjects.DistanceMeasures
 *By Janko Dietzsch
 *
 */
package mayday.core.math.distance.measures;

import java.util.HashMap;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;


/**
 * Euclidean Distance<p>
 * 
 * @author  Janko Dietzsch
 * @author  Markus Riester
 * @version 0.2
 */
public class EuclideanDistance extends DistanceMeasurePlugin {
	
	/* we are an abstractplugin
    and implement coreplugin(via distancemeasuretype) as well as idistancemeasure (via distancemeasure)
    */
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.distance.Euclidean",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Markus Riester, Janko Dietzsch",
					"riester@informatik.uni-tuebingen.de",
					"The euclidean distance: sqrt( sum( (x_i - y_i)^2 ) ).",
					"Euclidean"
					);
			return pli;
	}
	
	public EuclideanDistance() {
		//empty for pluginmanager
	}
	
	public double getDistance(double[] Vec1, double[] Vec2) {
        dimensionCheck(Vec1, Vec2);

		double Distance = 0.0;
		for (int x = 0; x < Vec1.length; x++) {
			double d = (Vec1[x] - Vec2[x]);
			Distance += d * d; 
			//Math.pow( (Vec1[x] - Vec2[x]) , 2 ); // this is five times slower than the above
		}
		Distance = Math.sqrt(Distance);
		return Distance;
	}
	
}
