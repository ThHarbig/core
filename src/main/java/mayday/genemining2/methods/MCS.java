package mayday.genemining2.methods;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import mayday.core.ClassSelectionModel;
import mayday.core.math.scoring.DefaultScoringResult;
import mayday.core.math.scoring.ScoringPlugin;
import mayday.core.math.scoring.ScoringResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.linalg.Algebra;
import mayday.core.structures.linalg.matrix.PermutableMatrix;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class MCS extends AbstractMiningMethod {

	private Double mcsSDWeight = 1.0;
	
	@SuppressWarnings("unchecked")
	@Override
	public ScoringResult runTest(Map<Object, double[]> values,
			ClassSelectionModel classes) {
		ScoringResult res = new DefaultScoringResult(false);
		
		// transform the data into a form that is much easier to work with for RP
		Map<Object, Integer> indexMap = new TreeMap<Object, Integer>();		
		PermutableMatrix allInputData = Algebra.matrixFromMap(values, indexMap);			
		Integer[][] index = classIndices(classes);
		PermutableMatrix data1 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[0]));
		PermutableMatrix data2 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[1]));
		
		double min_1, min_2, max_1, max_2, separability, mean1 = 0, sd1 = 0, mean2 = 0, sd2 = 0;
		int size1 = index[0].length;
		int size2 = index[1].length;
		
		int num_genes = values.size();
		Gene<Object>[] list = new Gene[num_genes];
		Object[] probes = values.keySet().toArray();
		
		
		for(int i = 0; i < num_genes; i++) {
			double[] v1 = data1.getRow(i).toArray();
			for(int j = 0; j < v1.length; j++) {
				mean1 += v1[j];
			}
			mean1 /= size1;
			
			for(int j = 0; j < v1.length; j++) {
				sd1 += (v1[j] - mean1) * (v1[j] - mean1);
			}
			sd1 /= (size1 - 1);
			sd1 = Math.sqrt(sd1);
			
			min_1 = mean1 - (sd1 * mcsSDWeight);
			max_1 = mean1 + (sd1 * mcsSDWeight);
			
			double[] v2 = data2.getRow(i).toArray();
			for(int j = 0; j < v2.length; j++) {
				mean2 += v2[j];
			}
			mean2 /= size2;
			for(int j = 0; j < v2.length; j++) {
				sd2 += (v2[j] - mean2) * (v2[j] - mean2);
			}
			sd2 /= (size2 - 1);
			sd2 = Math.sqrt(sd2);
			
			min_2 = mean2 - (sd2 * mcsSDWeight);
			max_2 = mean2 + (sd2 * mcsSDWeight);
			
			if((min_1 > min_2 && max_1 < max_2) || (min_1 < min_2 && max_1 > max_2)) {
				separability = -1;
			} else {
				double shortestDistance = Math.max(min_1, min_2) - Math.min(max_1, max_2);
				double longestDistance = Math.max(max_1, max_2) - Math.min(min_1, min_2);
				separability = shortestDistance / longestDistance;
			}
			list[i] = new Gene<Object>(separability, probes[i]);
		}
		Arrays.sort(list);
		
		for (int i = 0; i < list.length / 2; i++) {
			Gene<Object> tmp = list[i];
			list[i] = list[list.length - 1 - i];
			list[list.length - 1 - i] = tmp;
		}
		
		MIGroup rawScore = res.getRawScore();
		rawScore.setName("values");
		
		for(int i = 0; i < list.length; i++) {
			rawScore.add(list[i].getAnnotationValue(), new DoubleMIO(list[i].getDataValue()));
		}
		
		return res;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(this.getClass(),
				"PAS.genemining.mcs", new String[0], ScoringPlugin.MC,
				new HashMap<String, Object>(), "G\u00FCnter J\u00E4ger",
				"jaeger@informatik.uni-tuebingen.de", 
				"Max Class Separability compares the separability of classes by investigating \n" +
				"the overlap of a hypercubes induced by  genes ",
				"Max Class Separability");
		return pli;
	}
	
}
