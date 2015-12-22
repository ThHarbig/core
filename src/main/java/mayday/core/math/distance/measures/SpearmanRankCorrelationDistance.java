package mayday.core.math.distance.measures;

import java.util.HashMap;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.linalg.Algebra;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;

/**
 * The Spearman Rank Correlation Distance<p>
 * 
 * d(x,y) = 1 - cor(rank(x),rank(y))
 * 
 * @author Florian Battke
 * @version 0.1
 * 
 */
public class SpearmanRankCorrelationDistance extends DistanceMeasurePlugin {

	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.distance.SpearmanRankCorrelation",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Florian Battke",
					"battke@informatik.uni-tuebingen.de",
					"The Spearman Rank Correlation distance: 1-cor(rank(x),rank(y))",
					"Spearman Rank Correlation"
					);
			return pli;
	}
	
	public SpearmanRankCorrelationDistance() {
		//empty for pluginmanager
	}
   
    public double getDistance(double[] VectorOne, double[] VectorTwo) {
        return getDistance(new DoubleVector(VectorOne), new DoubleVector(VectorTwo));
    }

    public double getDistance(AbstractVector VectorOne, AbstractVector VectorTwo) {
        return 1 - Algebra.cor(VectorOne.rank(),VectorTwo.rank());
    }
}
