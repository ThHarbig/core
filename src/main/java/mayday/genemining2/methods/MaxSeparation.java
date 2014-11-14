package mayday.genemining2.methods;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import mayday.core.ClassSelectionModel;
import mayday.core.math.scoring.AbstractScoringResult;
import mayday.core.math.scoring.ScoringPlugin;
import mayday.core.math.scoring.ScoringResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.linalg.Algebra;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class MaxSeparation extends AbstractMiningMethod {

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(this.getClass(),
				"PAS.genemining.maxseparation", new String[0], ScoringPlugin.MC,
				new HashMap<String, Object>(), "Florian Battke",
				"battke@informatik.uni-tuebingen.de", 
				"The distance between the maximum of the less expressed and the minimum of the more expressed class",
				"Max Separation");
		return pli;
	}

	@Override
	public ScoringResult runTest(Map<Object, double[]> values, ClassSelectionModel classes) {
		ScoringResult res;
		
		res = new AbstractScoringResult() {
			public Comparator<Double> getRawScoreComparator() {
				return new Comparator<Double>() {
					public int compare(Double o1, Double o2) {
						// larger is better
						return o1.compareTo(o2);
					}
				};
			}
		};
		
		Map<Object, Integer> indexMap = new TreeMap<Object, Integer>();		
		PermutableMatrix allInputData = Algebra.matrixFromMap(values, indexMap);			
		Integer[][] index = classIndices(classes);
		PermutableMatrix data1 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[0]));
		PermutableMatrix data2 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[1]));
		int num_gene = values.size();

		double[] vals = new double[num_gene];

		for (int i=0; i!=num_gene; ++i) {
			AbstractVector v1 = data1.getRow(i);
			AbstractVector v2 = data2.getRow(i);
			
			double min1 = v1.min();
			double min2 = v2.min();
			double max1 = v1.max();
			double max2 = v2.max();
			
			double higher1 = min1-max2;
			double higher2 = min2-max1;
			
			vals[i] = Math.max(higher1, higher2);
		}

		MIGroup resgroup = res.getRawScore();

		for (Object o : values.keySet()) {
			int i = indexMap.get(o);
			resgroup.add(o, new DoubleMIO(vals[i]));
		}

		return res;
	}

	
}
