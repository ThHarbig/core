/*
 *  Created on Aug 18, 2004
 *
 */
package mayday.core.math.distance.measures;

import java.util.HashMap;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.linalg.Algebra;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;

/**
 * The Pearson Correlation Distance<p>
 * 
 * d(x,y) = 1 - cor(x,y)
 * 
 * @author Markus Riester
 * @version 0.1
 * 
 */
public class PearsonCorrelationDistance extends DistanceMeasurePlugin {

	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.distance.PearsonCorrelation",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Markus Riester",
					"riester@informatik.uni-tuebingen.de",
					"The Pearson Correlation distance: 1-cor(x,y)",
					"Pearson Correlation"
					);
			return pli;
	}
	
	public PearsonCorrelationDistance() {
		//empty for pluginmanager
	}
   
    public double getDistance(double[] VectorOne, double[] VectorTwo) {
        return getDistance(new DoubleVector(VectorOne), new DoubleVector(VectorTwo));
    }

    public double getDistance(AbstractVector VectorOne, AbstractVector VectorTwo) {
        return 1 - Algebra.cor(VectorOne,VectorTwo);
    }
}
