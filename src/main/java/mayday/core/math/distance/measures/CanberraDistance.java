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
 * 
 * CanberraDistance<p>
 * 
 *  sum(|x_i - y_i| / |x_i + y_i|).<p>  
 *  Values where numerator and/or denominator are zero will be ignored
 * 
 * @author Markus Riester
 * @version 0.1
 */
public class CanberraDistance extends DistanceMeasurePlugin {
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.distance.Canberra",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Markus Riester",
					"riester@informatik.uni-tuebingen.de",
					"The canberra distance: sum(|x_i - y_i| / |x_i + y_i|).",
					"Canberra"
					);
			return pli;
	}
	
	public CanberraDistance() {		
		//empty for pluginmanager
	}
	
    public double getDistance(double[] VectorOne, double[] VectorTwo) {
        dimensionCheck(VectorOne, VectorTwo);
		double Distance = 0;
		for (int x = 0; x < VectorOne.length; x++) {
		    double nominator = Math.abs( (VectorOne[x] - VectorTwo[x]) );
		    double denominator = Math.abs( (VectorOne[x] + VectorTwo[x]) );
		    
		    if (nominator > 0 && denominator > 0) {
		        Distance += (nominator / denominator);
		    }
		}
		return Distance;
    }

}
